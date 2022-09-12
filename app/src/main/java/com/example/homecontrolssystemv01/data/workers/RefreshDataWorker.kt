package com.example.homecontrolssystemv01.data.workers

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.*
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.data.database.AppDatabase
import com.example.homecontrolssystemv01.data.database.DataDbModel
import com.example.homecontrolssystemv01.data.database.MessageDbModel
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.network.ApiFactory
import com.example.homecontrolssystemv01.data.repository.MainRepositoryImpl
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

    private val _context = context

    private val apiService = ApiFactory.apiService

    private val dataDao = AppDatabase.getInstance(context).dataDao()

    private val myRef = Firebase.database(MainRepositoryImpl.FIREBASE_URL).getReference(
        MainRepositoryImpl.FIREBASE_PATH
    )

    private val mapper = DataMapper()

    //private val serverMode = workerParameters.inputData.getBoolean(NAME_SERVER_MODE,false)
    private val remoteMode = workerParameters.inputData.getBoolean(NAME_REMOTE_MODE,false)

    private val delayTime:Long = 1000//milliSeconds
    private val limitErrorCount = 3//кол неудачных попыток, после чего результат - ошибка

    private val idTime = -1 //ID время обнвления с файла Json
    private val limitTimeRemote = 3600000L // 60 минут - для контроля обновления данных удаленного сервера

    override suspend fun doWork(): Result {

        val result:Result

         //контроль исключений
        var resultIsSuccess = true
        var errorCount = 0

        //базы данных с серверов
            var dataLocal = listOf<DataDbModel>()
            var dataRemote = listOf<DataDbModel>()

            do{//делаем циклы когда ошибка

                var whileLoop = false

                if (!remoteMode) {
                    try {
                        val data = getLocalData()
                        if (!data.isNullOrEmpty()) {
                            dataLocal = data
                            insertDataToDB(data)
                        }

                    } catch (e: Exception){
                        //исключение ретрафит, нужно еще добавить для рум
                       Log.d("HCS_Error Local data", e.toString())
                        whileLoop = true

                    }
                }

                try {

                    val data = getRemoteData()

                    if (!data.isNullOrEmpty()){
                        dataRemote = data
                        if (remoteMode) insertDataToDB(data)
                    }

                } catch (e:Exception){
                    Log.d("HCS_Error Remote data", e.toString())
                    whileLoop = true


                }

                if (whileLoop){
                    errorCount += 1
                    delay(delayTime)

                    if (errorCount > limitErrorCount){
                        Log.d("HCS_Worker_Error", "errorCount = $errorCount")
                        insertMessage(MessageDbModel(Date().time,0,2,"Ошибка загрузки данных"))
                        resultIsSuccess = false
                        whileLoop = false
                }

                }else{
                    resultIsSuccess = true
                }

            }while (whileLoop)

            //проверка на исключения
            if (resultIsSuccess){
                result = Result.success()
                createMessageAndInsertToBase(if (remoteMode) dataRemote else dataLocal)
            } else {
                completeUpdate()
                result = Result.failure()
            }

            return result
        }

    private suspend fun insertMessage(message:MessageDbModel){
        try {
            dataDao.insertMessage(message)
        }catch (e:Exception){
            Log.d("HCS_Error_Message", e.toString())
            toastMessage("Error Data Base")

        }


    }

    private suspend fun getRemoteData():List<DataDbModel>?{

            val dataSnapshot = myRef.get().await()

          if (dataSnapshot != null) {

              val remoteData = dataSnapshot.getValue<List<DataDbModel>>()

              if (!remoteData.isNullOrEmpty()){

                  val timeRemoteString = mapper.convertDateServerToDateUI(remoteData.find {
                      it.id == idTime
                  }?.value)

                  Log.d("HCS_RefreshDataWorker", "timeRemote = $timeRemoteString")

                  val timeRemoteLong = convertStringTimeToLong(timeRemoteString)

                  if (Date().time - timeRemoteLong > limitTimeRemote){

                      //message
                      insertMessage(MessageDbModel(Date().time,0,2,"Remote time error"))

                  }

                  return remoteData

              }else return null

          }else return null



    }

    private suspend fun insertDataToDB(data:List<DataDbModel>){
        dataDao.insertValue(data)
        completeUpdate()
    }

    private suspend fun completeUpdate(){
        //message контролируется в UI - SwipeRefreshState
        insertMessage(MessageDbModel(-1,-1,-1,"complete update"))
    }

    private suspend fun toastMessage(message:String){

        coroutineScope {
            launch(Dispatchers.Main){
                Toast.makeText(_context, message, Toast.LENGTH_LONG).show()
            }
        }
    }




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
            toastMessage("Error Data Base")
        }
    }




    private suspend fun getLocalData():List<DataDbModel>{

        val jsonContainer = apiService.getData()
        val dataDtoList = mapper.mapJsonContainerToListValue(jsonContainer)
        //Log.d("HCS_RefreshDataWorker",dataDtoList[0].value.toString())

        val dataDbModelList = dataDtoList.map {
            mapper.valueDtoToDbModel(it)
        }

        val timeFromApiServer = mapper.convertDateServerToDateUI(dataDbModelList.find {
            it.id == idTime
        }?.value)

        Log.d("HCS_RefreshDataWorker", "timeLocal = $timeFromApiServer")

        return dataDbModelList
    }



    companion object {

        const val NAME_ONE_TIME = "RefreshDataWorker_ONE_TIME"
        const val NAME_REMOTE_MODE = "Remote_MODE"


        fun makeRequestOneTime(serverMode: Boolean, remoteMode:Boolean): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RefreshDataWorker>()
                .setConstraints(makeConstraints())
                .setInputData(modeToData(serverMode,remoteMode))
                .build()
        }

        private fun modeToData(serverMode: Boolean, remoteMode:Boolean): Data {
            return Data.Builder()
                .putBoolean(NAME_REMOTE_MODE,remoteMode)
                .build()
        }

        private fun makeConstraints (): Constraints{
            return Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

        }

    }
}