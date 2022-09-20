package com.example.homecontrolssystemv01.data.workers

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import android.widget.Toast
import androidx.work.*
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.data.database.AppDatabase
import com.example.homecontrolssystemv01.data.database.DataDbModel
import com.example.homecontrolssystemv01.data.database.MessageDbModel
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.mapper.insertMessage
import com.example.homecontrolssystemv01.data.mapper.toastMessage
import com.example.homecontrolssystemv01.data.network.ApiFactory
import com.example.homecontrolssystemv01.data.repository.MainRepositoryImpl
import com.example.homecontrolssystemv01.domain.model.ModeConnect
import com.example.homecontrolssystemv01.util.convertStringTimeToLong
import com.example.homecontrolssystemv01.util.createMessageListLimit
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*

class RefreshDataWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val listDescription = context.resources.getStringArray(R.array.data)
    private var wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val _context = context

    private val apiService = ApiFactory.apiService

    private val dataDao = AppDatabase.getInstance(context).dataDao()

    private val myRef = Firebase.database(MainRepositoryImpl.FIREBASE_URL).getReference(
        MainRepositoryImpl.FIREBASE_PATH
    )

    private val mapper = DataMapper()

    private val ssidSetting = workerParameters.inputData.getString(NAME_SETTING_SSID)
    private val idControl = workerParameters.inputData.getInt(ID,0)
    private val valueControl = workerParameters.inputData.getString(VALUE)?:""

    private val delayTime:Long = 500//milliSeconds
    private val cyclicTime:Long = 2000//milliSeconds
    private val limitErrorCount = 2//кол неудачных попыток, после чего результат - ошибка

    private val limitTimeRemote = 3600000L // 60 минут - для контроля обновления данных удаленного сервера

    override suspend fun doWork(): Result {



        //чтобы удаленные данные загрузились один раз и после локальных
        //если сразу грузить удаленные данные, то долго ждем загрузку
        var firstCycle = true
        var loop = false

        var dataList = listOf<DataDbModel>()

            do {

                    val ssid =  wifiManager.connectionInfo.ssid

                    val dataSystem = mutableListOf(
                        DataDbModel(DataID.SSID.id,ssid,DataID.SSID.name,4),
                    )

                    if (ssid == ssidSetting){


                        //LOCAL MODE

                        val dataLocal = getLocalData(firstCycle,idControl,valueControl)

                        dataList =  if (dataLocal != null) {
                            loop = true
                            dataSystem.add(DataDbModel(
                                DataID.connectMode.id,
                                ModeConnect.LOCAL.name,
                                DataID.connectMode.name,
                                4))
                            dataLocal + dataSystem

                        } else {
                            loop = false
                            dataSystem.add(DataDbModel(
                                DataID.connectMode.id,
                                ModeConnect.STOP.name,
                                DataID.connectMode.name,
                                4))
                            dataSystem
                        }



                    }else{


                     //REMOTE MODE

                        val dataRemote = getRemoteData()//вызываем всегда для проверки времени обновления удаленных данных

                        dataList =  if (dataRemote != null) {
                            dataSystem.add(DataDbModel(
                                DataID.connectMode.id,
                                ModeConnect.REMOTE.name,
                                DataID.connectMode.name,
                                4))
                            dataRemote + dataSystem
                        } else {
                            dataSystem.add(DataDbModel(
                                DataID.connectMode.id,
                                ModeConnect.STOP.name,
                                DataID.connectMode.name,
                                4))
                            dataSystem
                        }

                        loop = false
                    }

                    insertDataToDB(dataList)

                if (firstCycle && ssid == ssidSetting) {
                    getRemoteData()
                    firstCycle = false
                }

                delay(cyclicTime)

                    createMessageAndInsertToBase(dataList)

            }while (loop)

            return Result.success()
        }

//    private suspend fun insertMessage(message:MessageDbModel){
//        try {
//            dataDao.insertMessage(message)
//        }catch (e:Exception){
//            Log.d("HCS_Error_Message", e.toString())
//            toastMessage("Error Data Base")
//
//        }
//
//
//    }



    private suspend fun getRemoteData():List<DataDbModel>?{

        try {

            val dataSnapshot = myRef.get().await()

            if (dataSnapshot != null) {

                val remoteData = dataSnapshot.getValue<List<DataDbModel>>()

                if (!remoteData.isNullOrEmpty()){

                    val timeRemoteString = mapper.convertDateServerToDateUI(remoteData.find {
                        it.id == DataID.lastTimeUpdate.id
                    }?.value)

                    Log.d("HCS_RefreshDataWorker", "timeRemote = $timeRemoteString")

                    val timeRemoteLong = convertStringTimeToLong(timeRemoteString)

                    if (Date().time - timeRemoteLong > limitTimeRemote){

                        //message
                        val message = MessageDbModel(Date().time,0,2,"Remote time error")
                        insertMessage(_context,dataDao,message)

                    }

                    return remoteData

                }else return null

            }else return null

        }catch (e:Exception){

            Log.d("HCS_Error Remote data", e.toString())
            val message = MessageDbModel(Date().time,0,2,"Ошибка удаленных данных")
                insertMessage(_context,dataDao,message)

            return null
        }
    }

    private suspend fun insertDataToDB(data:List<DataDbModel>){

        try {
            dataDao.insertValue(data)
        }catch (e:Exception){
            Log.d ("HCS_Exception", e.message.toString())
            toastMessage(_context,"Error Data Base")
        }
        completeUpdate()
    }

    private suspend fun completeUpdate(){
        //message контролируется в UI - SwipeRefreshState
        val message = MessageDbModel(-1,-1,-1,"complete update")
        insertMessage(_context,dataDao,message)
    }

//    private suspend fun toastMessage(message:String){
//
//        coroutineScope {
//            launch(Dispatchers.Main){
//                Toast.makeText(_context, message, Toast.LENGTH_LONG).show()
//            }
//        }
//    }




    private suspend fun createMessageAndInsertToBase(dataDbList:List<DataDbModel>) {

        try {

            coroutineScope {

                val dataList = dataDbList.map {
                    mapper.mapDataToEntity(it, listDescription)
                }


                val flow = dataDao.getSettingListFlow()

                flow.collect() { list ->
                    val listSetting = list.map { mapper.settingDbModelToEntity(it) }
                    // Log.d("HCS_RefreshDataWorker", listSetting.toString())
                    val listMessage = createMessageListLimit(dataList, listSetting)
                    val listDbMessage = listMessage.map { mapper.mapEntityToMessage(it) }
                    dataDao.insertMessageList(listDbMessage)
                    //Log.d("HCS_RefreshDataWorker", listDbMessage.toString())
                    this.coroutineContext.cancel()//вылетае исключение

                }
            }

        }catch (e: CancellationException){

        }catch (e:Exception){
            Log.d ("HCS_Exception", e.message.toString())
            toastMessage(_context,"Error Data Base")
        }
    }




    private suspend fun getLocalData(firstCycle:Boolean,idControl:Int, valueControl:String):List<DataDbModel>?{

        try {

            val jsonContainer = if (firstCycle){
                when(idControl){
                    //0-> apiService.getData()
                    23-> apiService.buttonLightSleep()
                    24-> apiService.buttonLightChild()
                    25-> apiService.buttonLightCinema()
                    37-> apiService.setMeterElectricity(valueControl)
                    else -> {apiService.getData()}
                }
            }else apiService.getData()

            //val jsonContainer = apiService.getData()
            val dataDtoList = mapper.mapJsonContainerToListValue(jsonContainer)
            //Log.d("HCS_RefreshDataWorker",dataDtoList[0].value.toString())

            val dataDbModelList = dataDtoList.map {
                mapper.valueDtoToDbModel(it)
            }

            if (firstCycle){
                val timeFromApiServer = mapper.convertDateServerToDateUI(dataDbModelList.find {
                     it.id == DataID.lastTimeUpdate.id
                 }?.value)

                Log.d("HCS_RefreshDataWorker", "timeLocal = $timeFromApiServer")
            }


            return dataDbModelList

        }catch (e:Exception){

            Log.d("HCS_Error Local data", e.toString())
            val message = MessageDbModel(Date().time,0,2,"Ошибка локальных данных")

            insertMessage(_context,dataDao,message)

            return null
        }



    }



    companion object {


        const val NAME_ONE_TIME = "RefreshDataWorker_ONE_TIME"
        const val NAME_REMOTE_MODE = "Remote_MODE"
        const val NAME_CYCLIC_MODE = "Cyclic_MODE"
        const val NAME_SETTING_SSID = "SSID"

        const val ID = "id"
        const val VALUE = "value"

        fun makeRequestOneTime(ssid:String, idControl:Int, valueControl:String): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RefreshDataWorker>()
                .setConstraints(makeConstraints())
                .setInputData(modeToData(ssid, idControl, valueControl))
                .build()
        }

        private fun modeToData(ssid:String, idControl:Int, valueControl:String): Data {
            return Data.Builder()
                .putString(NAME_SETTING_SSID,ssid)
                .putInt(ID,idControl)
                .putString(VALUE,valueControl)
                .build()
        }

        private fun makeConstraints (): Constraints{
            return Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

        }

    }
}