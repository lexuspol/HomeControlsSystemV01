package com.example.homecontrolssystemv01.data.workers

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import androidx.work.*
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.data.database.AppDatabase
import com.example.homecontrolssystemv01.data.database.DataDbModel
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.network.ApiFactory
import com.example.homecontrolssystemv01.data.network.model.DataJsonContainerDto
import com.example.homecontrolssystemv01.data.repository.MainRepositoryImpl
import com.example.homecontrolssystemv01.domain.enum.ControlValue
import com.example.homecontrolssystemv01.domain.enum.DataType
import com.example.homecontrolssystemv01.domain.model.ControlRemote
import com.example.homecontrolssystemv01.domain.model.message.ModeConnect
import com.example.homecontrolssystemv01.util.convertStringTimeToLong
import com.example.homecontrolssystemv01.util.createMessageListLimit
import com.example.homecontrolssystemv01.util.insertMessage
import com.example.homecontrolssystemv01.util.toastMessage
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
    private val alarmMessage = context.resources.getStringArray(R.array.alarmMessage)

    private val listResourses = listOf(listDescription,alarmMessage)

    private val dataFormat = context.resources.getString(R.string.data_format)
    private var wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager



    private val _context = context

    private val apiService = ApiFactory.apiService

    private val dataDao = AppDatabase.getInstance(context).dataDao()

    private val myRef = Firebase.database(MainRepositoryImpl.FIREBASE_URL)

    private val mapper = DataMapper()

    private val ssidSetting = workerParameters.inputData.getString(NAME_SETTING_SSID)
    private val infoDevice = workerParameters.inputData.getString(NAME_INFO_DEVICE)
    private val idControl = workerParameters.inputData.getInt(ID,0)
    private val valueControl = workerParameters.inputData.getString(VALUE)?:"0"
    private val cycleMode = workerParameters.inputData.getBoolean(NAME_CYCLIC_MODE,false)
    private val remoteControl = workerParameters.inputData.getBoolean(NAME_REMOTE_CONTROL,false)


    private val delayTime:Long = 500//milliSeconds
    private val cyclicTime:Long = 5000//milliSeconds
    private val limitErrorCount = 2//кол неудачных попыток, после чего результат - ошибка

    private val limitTimeRemote = 3600000L // 60 минут - для контроля обновления данных удаленного сервера

    override suspend fun doWork(): Result {

        val dataSystem = mutableListOf(
            DataDbModel(DataID.deviceInfo.id,infoDevice,DataID.deviceInfo.name,DataType.STRING.int),
        )

        //чтобы удаленные данные загрузились один раз и после локальных
        //если сразу грузить удаленные данные, то долго ждем загрузку
        var firstCycle = true
        var loop = cycleMode

        var dataList = listOf<DataDbModel>()

            do {

                    val ssid =  wifiManager.connectionInfo.ssid




                    dataSystem.add(DataDbModel(DataID.SSID.id,ssid,DataID.SSID.name,DataType.STRING.int))

                    if (ssid == ssidSetting){


                        //LOCAL MODE

                        val dataLocal = getLocalData(firstCycle)

                        dataList =  if (dataLocal != null) {
                            //loop = true
                            dataSystem.add(DataDbModel(
                                DataID.connectMode.id,
                                ModeConnect.LOCAL.name,
                                DataID.connectMode.name,
                                DataType.STRING.int))
                            dataLocal + dataSystem

                        } else {
                            loop = false
                            dataSystem.add(DataDbModel(
                                DataID.connectMode.id,
                                ModeConnect.STOP.name,
                                DataID.connectMode.name,
                                DataType.STRING.int))
                            dataSystem
                        }

                    }else{


                     //REMOTE MODE
                        loop = false

                        writeControlToRemote()

                        val dataRemote = getRemoteData()//вызываем всегда для проверки времени обновления удаленных данных

                        dataList =  if (dataRemote != null) {
                            dataSystem.add(DataDbModel(
                                DataID.connectMode.id,
                                ModeConnect.REMOTE.name,
                                DataID.connectMode.name,
                                DataType.STRING.int))
                            dataRemote + dataSystem
                        } else {
                            dataSystem.add(DataDbModel(
                                DataID.connectMode.id,
                                ModeConnect.STOP.name,
                                DataID.connectMode.name,
                                DataType.STRING.int))
                            dataSystem
                        }


                    }

                    insertDataToDB(dataList)

                if (firstCycle && ssid == ssidSetting) {
                    getRemoteData()
                    firstCycle = false
                }

                createMessageAndInsertToBase(dataList)

                delay(cyclicTime)

            }while (loop)

            return Result.success()
        }

    private fun writeControlToRemote() {

        val refControl = myRef.getReference(MainRepositoryImpl.FIREBASE_PATH_CONTROL)

        if (!remoteControl){
            when(idControl){
                DataID.buttonWicketUnlock.id ->
                    //refControl.child("$idControl").setValue(ControlValue.GATE_START.value)
                    refControl.setValue(
                        ControlRemote(idControl,ControlValue.GATE_START.value))
            }
        }

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

            val refData = myRef.getReference(MainRepositoryImpl.FIREBASE_PATH)


            val dataSnapshot = refData.get().await()

            if (dataSnapshot != null) {

                val remoteData = dataSnapshot.getValue<List<DataDbModel>>()

                if (!remoteData.isNullOrEmpty()){

                    val timeRemoteString = mapper.convertDateServerToDateUI(remoteData.find {
                        it.id == DataID.lastTimeUpdate.id
                    }?.value,dataFormat)

                    Log.d("HCS_RefreshDataWorker", "timeRemote = $timeRemoteString")

                    val timeRemoteLong = convertStringTimeToLong(timeRemoteString,dataFormat)

                    if (timeRemoteLong==-1L){
                        insertMessage(_context,dataDao,1003)
                    }else{

                        if (Date().time - timeRemoteLong > limitTimeRemote){
                            insertMessage(_context,dataDao,1004)
                        }

                    }

                    return remoteData

                }else return null

            }else return null

        }catch (e:Exception){

            //Log.d("HCS_Error Remote data", e.toString())
                insertMessage(_context,dataDao,1002)

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

    }

    private suspend fun createMessageAndInsertToBase(dataDbList:List<DataDbModel>) {

        try {

            coroutineScope {

                val dataList = dataDbList.map {
                    mapper.mapDataToEntity(it, listResourses,dataFormat,false)
                }


                val flow = dataDao.getSettingListFlow()

                flow.collect() { list ->
                    val listSetting = list.map { mapper.settingDbModelToEntity(it) }
                    // Log.d("HCS_RefreshDataWorker", listSetting.toString())
                    val listMessage = createMessageListLimit(
                        dataList,
                        listSetting,
                        dataFormat,
                    alarmMessage)
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

    private suspend fun getLocalData(firstCycle:Boolean):List<DataDbModel>?{

        try {

            val jsonContainer = when{
               firstCycle && !remoteControl -> writeControlToApiService()
                firstCycle && remoteControl -> {
                    val json = apiService.getData()
                    val data = mapper.mapJsonContainerToListValue(json)
                    val mainDeviceName = data.find { it.id == DataID.mainDeviceName.id }?.value
                    if (mainDeviceName == infoDevice){
                        writeControlToApiService()
                    }else json
                }
                else -> apiService.getData()
           }

            val dataList =  mapper.mapJsonContainerToListValue(jsonContainer)

            if (firstCycle){
                val timeFromApiServer = mapper.convertDateServerToDateUI(dataList.find {
                    it.id == DataID.lastTimeUpdate.id
                }?.value,dataFormat)
                Log.d("HCS_RefreshDataWorker", "timeLocal = $timeFromApiServer")
            }

            return mapper.mapJsonContainerToListValue(jsonContainer).map {mapper.valueDtoToDbModel(it)
            }

        }catch (e:Exception){

            Log.d("HCS_Error Local data", e.toString())

            insertMessage(_context,dataDao,1001)

            return null
        }
    }

    private suspend fun writeControlToApiService(): DataJsonContainerDto {

        // для теста
        if (idControl !=0){
            val mes = "id = $idControl, value = $valueControl"
            Log.d("HCS",mes)
            toastMessage(_context,mes)
        }
        /////////

        return when(idControl){
            //0-> apiService.getData()
            DataID.buttonLightSleep.id-> apiService.buttonLightSleep(valueControl)
            DataID.buttonLightChild.id-> apiService.buttonLightChild(valueControl)
            DataID.buttonLightCinema.id-> apiService.buttonLightCinema(valueControl)
            DataID.buttonLightOutdoor.id -> apiService.buttonLightOutdoor(valueControl)

            DataID.meterWater.id-> apiService.setMeterWater(valueControl)
            DataID.meterElectricity.id-> apiService.setMeterElectricity(valueControl)

            DataID.buttonWicketUnlock.id-> apiService.buttonWicketUnlock(valueControl)
            DataID.buttonGateGarageSBS.id-> apiService.buttonGateGarageSBS(valueControl)
            DataID.buttonGateSlidingSBS.id-> apiService.buttonGateSlidingSBS(valueControl)

            DataID.mainDeviceName.id -> apiService.setMainDeviceName(valueControl)

            DataID.buttonSoundOff.id -> apiService.buttonSoundOff(valueControl)

            else -> {apiService.getData()}
        }
    }

    companion object {


        const val NAME_ONE_TIME = "RefreshDataWorker_ONE_TIME"
        const val NAME_REMOTE_CONTROL = "Remote_CONTROL"
        const val NAME_CYCLIC_MODE = "Cyclic_MODE"
        const val NAME_SETTING_SSID = "SSID"
        const val NAME_INFO_DEVICE = "Info_Device"

        const val ID = "id"
        const val VALUE = "value"

        fun makeRequestOneTime(ssid:String, idControl:Int,
                               valueControl:String,cyclicMode:Boolean,
                               infoDevice:String,remoteControl:Boolean): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RefreshDataWorker>()
                .setConstraints(makeConstraints())
                .setInputData(modeToData(ssid, idControl, valueControl,cyclicMode,infoDevice, remoteControl))
                .build()
        }

        private fun modeToData(ssid:String, idControl:Int,
                               valueControl:String,
                               cyclicMode:Boolean,infoDevice:String,remoteControl:Boolean): Data {
            return Data.Builder()
                .putString(NAME_SETTING_SSID,ssid)
                .putInt(ID,idControl)
                .putString(VALUE,valueControl)
                .putBoolean(NAME_CYCLIC_MODE,cyclicMode)
                .putString(NAME_INFO_DEVICE,infoDevice)
                .putBoolean(NAME_REMOTE_CONTROL,remoteControl)
                .build()
        }

        private fun makeConstraints (): Constraints{
            return Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

        }

    }
}