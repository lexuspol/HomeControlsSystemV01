package com.example.homecontrolssystemv01.data.workers

import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.*

class RefreshDataWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val listDescription = context.resources.getStringArray(R.array.data)

    private val apiService = ApiFactory.apiService

    private val dataDao = AppDatabase.getInstance(context).dataDao()

    private val myRef = Firebase.database(MainRepositoryImpl.FIREBASE_URL).getReference(
        MainRepositoryImpl.FIREBASE_PATH
    )

    private val mapper = DataMapper()

    //private val serverMode = workerParameters.inputData.getBoolean(NAME_SERVER_MODE,false)
    private val remoteMode = workerParameters.inputData.getBoolean(NAME_REMOTE_MODE,false)

    private val delayTime:Long = 1000//milliSeconds
    private val periodTime:Long = 20//seconds
    private val limitErrorCount = 5//кол неудачных попыток, после чего результат - ошибка

    private val idTime = -1
    private val limitTimeRemote = 3600000L // 60 минут

    //private var dataFromDB = listOf<DataDbModel>()



    override suspend fun doWork(): Result {

            var resultWork: Result
            var whileLoop = false//цикл при ошибке
            var errorCount = 0

            do{

                try {

                    var dataLocal = listOf<DataDbModel>()
                    var dataRemove = listOf<DataDbModel>()

                    if (!remoteMode){

                        val dataFromApiServer = runApiService()

                        val timeFromApiServer = mapper.convertDateServerToDateUI(dataFromApiServer.find {
                            it.id == idTime
                        }?.value)

                        Log.d("HCS_RefreshDataWorker", "timeLocal = $timeFromApiServer")

                        dataLocal = dataFromApiServer

                    }

                    dataRemove = getRemoteData()

                    val timeRemoteString = mapper.convertDateServerToDateUI(dataRemove.find {
                        it.id == idTime
                    }?.value)

                    Log.d("HCS_RefreshDataWorker", "timeRemote = $timeRemoteString")

                    val timeRemoteLong = convertStringTimeToLong(timeRemoteString)

                    if (Date().time - timeRemoteLong > limitTimeRemote && errorCount==0){

                        dataDao.insertMessage(
                            MessageDbModel(
                                Date().time,
                                0,
                                2,
                                "Remote time error"))

                    }

                    val data = if (remoteMode)dataRemove else dataLocal

                    dataDao.insertValue(data)

                    if (errorCount==0){
                        dataDao.insertMessage(
                            MessageDbModel(
                                Date().time,
                                0,
                                0,
                                "Обновление данных"))
                    }

                    createMessageAndInsertToBase(data)

                } catch (e: CancellationException) {

                    //нужно ждать загрузку настроек Flow, потом закрывать скоуп, вылетает исключение

                    //Log.d("HCS_RefreshDataWorker", e.toString())

                } catch (e: IOException){

                    //исключение ретрафит, нужно еще добавить для рум

                    Log.d("HCS_Worker_Error", e.toString())
                    delay(delayTime)
                    whileLoop = true
                    errorCount += 1

                }

                try {
                    if (errorCount>limitErrorCount) {
                        Log.d("HCS_Worker_Error", "errorCount = $errorCount")

                        errorCount = 0
                        whileLoop = false

                        dataDao.insertMessage(MessageDbModel(Date().time,0,2,"Ошибка загрузки локальных данных"))
                        resultWork = Result.failure()
                    }else{
                        resultWork = Result.success()
                    }


                }catch (e:Exception){
                    dataDao.insertMessage(MessageDbModel(Date().time,0,2,"Ошибка базы данных"))
                    resultWork = Result.failure()
                }






            }while (whileLoop)


            return resultWork
        }

    private suspend fun getRemoteData():List<DataDbModel>{

        var data = listOf<DataDbModel>()

        val dataSnapshot = try{
            myRef.get().await()
        } catch (e : Exception){
            Log.d("HCS_Worker_Error", "getFirebaseData error = $e")
            dataDao.insertMessage(MessageDbModel(Date().time,0,2,"Error Firebase"))
            null
        }

        if ( dataSnapshot!= null) {
            val dataFirebase = dataSnapshot.getValue<List<DataDbModel>>()

            if(dataFirebase.isNullOrEmpty()){
                Log.d("HCS_Worker_Error","Firebase NO data")
                dataDao.insertMessage(MessageDbModel(Date().time,0,1,"Firebase NO data"))
            } else{
                data = dataFirebase
               // Log.d("HCS_RefreshDataWorker","Write to DB from Firebase")
               // dataDao.insertMessage(MessageDbModel(Date().time,0,0,"Зарузка из Firebase"))
            }

        }else{
            Log.d("HCS_Worker_Error","Firebase Empty Snapshot")
            dataDao.insertMessage(MessageDbModel(Date().time,0,1,"Firebase empty"))
        }

        return data
    }




    private suspend fun createMessageAndInsertToBase(dataDbList:List<DataDbModel>) {

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
    }




    private suspend fun runApiService():List<DataDbModel>{

        val jsonContainer = apiService.getData()
        val dataDtoList = mapper.mapJsonContainerToListValue(jsonContainer)
        //Log.d("HCS_RefreshDataWorker",dataDtoList[0].value.toString())

        val dataDbModelList = dataDtoList.map {
            mapper.valueDtoToDbModel(it)
        }
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