package com.example.homecontrolssystemv01.data.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.homecontrolssystemv01.data.database.AppDatabase
import com.example.homecontrolssystemv01.data.database.DataDbModel
import com.example.homecontrolssystemv01.data.database.MessageDbModel
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.network.ApiFactory
import com.example.homecontrolssystemv01.data.repository.MainRepositoryImpl
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.util.*


import java.util.concurrent.TimeUnit

class RefreshDataWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val apiService = ApiFactory.apiService

    private val dataDao = AppDatabase.getInstance(context).dataDao()

    private val myRef = Firebase.database(MainRepositoryImpl.FIREBASE_URL).getReference(
        MainRepositoryImpl.FIREBASE_PATH
    )

    private val mapper = DataMapper()

    private val serverMode = workerParameters.inputData.getBoolean(NAME_SERVER_MODE,false)
    private val remoteMode = workerParameters.inputData.getBoolean(NAME_REMOTE_MODE,false)

    private val delayTime:Long = 1000//milliSeconds
    private val periodTime:Long = 20//seconds
    private val limitErrorCount = 5//кол неудачных попыток, после чего результат - ошибка


        override suspend fun doWork(): Result {

            var resultWork: Result
            var whileLoop = false//цикл делаем если LOCAL или при ошибке
            var errorCount = 0

            do{

                try {
                    //delay(delayTime)//немного ждем, так как бывает вылетает ошибка

                    when{
                        remoteMode ->{
                            val dataSnapshot = getFirebaseData()
                            if ( dataSnapshot!= null) {
                                val dataFirebase = dataSnapshot.getValue<List<DataDbModel>>()

                                if(dataFirebase.isNullOrEmpty()){
                                    Log.d("HCS_RefreshDataWorker","Firebase NO data")
                                    dataDao.insertMessage(MessageDbModel(Date().time,0,1,"Firebase NO data"))
                                    errorCount += 1
                                    whileLoop = true
                                } else{
                                    dataDao.insertValue(dataFirebase)
                                    Log.d("HCS_RefreshDataWorker","Write to DB from Firebase")
                                    dataDao.insertMessage(MessageDbModel(Date().time,0,0,"Зарузка из Firebase"))
                                    whileLoop = false
                                }

                            }else{
                                Log.d("HCS_RefreshDataWorker","Firebase Empty Snapshot")
                                dataDao.insertMessage(MessageDbModel(Date().time,0,1,"Firebase empty"))
                                errorCount += 1
                                whileLoop = true
                            }


                        }

                        serverMode ->{
                            val dataFromApiServer = runApiService()
                            dataDao.insertValue(dataFromApiServer)
                            myRef.setValue(dataFromApiServer)
                            Log.d("HCS_RefreshDataWorker","Write to DB/Firebase from Network")
                            dataDao.insertMessage(MessageDbModel(Date().time,0,1,"Сервер.Обновление данных"))

                            whileLoop = false
                        }
                        !remoteMode && !serverMode ->{//local mode
                            val dataFromApiServer = runApiService()
                            dataDao.insertValue(dataFromApiServer)
                            dataDao.insertMessage(MessageDbModel(Date().time,0,0,"Загрузка данных"))

                            //можно включить переодический опрос
                             //delay(periodTime*1000-delayTime)
                            //whileLoop = true
                            whileLoop = false

                        }
                    }


                    errorCount = 0
                } catch (e: Exception) {
                    Log.d("HCS_RefreshDataWorker", e.toString())
                    delay(delayTime)
                    whileLoop = true
                    errorCount += 1
                    //resultWork = Result.success()
                }

                if (errorCount>limitErrorCount) {
                    whileLoop = false
                    Log.d("HCS_RefreshDataWorker", "errorCount = $errorCount")
                    dataDao.insertMessage(MessageDbModel(Date().time,0,2,"Ошибка загрузки данных"))
                    resultWork = Result.failure()
                }else{
                    resultWork = Result.success()
                }



            }while (whileLoop)


            return resultWork
        }

    private suspend fun getFirebaseData():DataSnapshot?{
        return try{
            val data = myRef.get().await()
            data
        } catch (e : Exception){
            Log.d("HCS_RefreshDataWorker", "getFirebaseData error = $e")
            dataDao.insertMessage(MessageDbModel(Date().time,0,2,"Error Firebase"))
            null
        }
    }





    private suspend fun runApiService():List<DataDbModel>{

        val jsonContainer = apiService.getData()
        val dataDtoList = mapper.mapJsonContainerToListValue(jsonContainer)
        Log.d("HCS_RefreshDataWorker",dataDtoList[0].value.toString())

        val dataDbModelList = dataDtoList.map {
            mapper.valueDtoToDbModel(it)
        }

        return dataDbModelList
    }



    companion object {

        const val NAME_PERIODIC = "RefreshDataWorker_PERIODIC"
        const val NAME_ONE_TIME = "RefreshDataWorker_ONE_TIME"
        const val NAME_SERVER_MODE = "Server_MODE"
        const val NAME_REMOTE_MODE = "Remote_MODE"



        fun makeRequestPeriodic(serverMode: Boolean, remoteMode:Boolean): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<RefreshDataWorker>(20,
                TimeUnit.MINUTES)
                .setConstraints(makeConstraints())
                .setInputData(modeToData(serverMode,remoteMode))
                .build()
        }

        fun makeRequestOneTime(serverMode: Boolean, remoteMode:Boolean): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RefreshDataWorker>()
                .setConstraints(makeConstraints())
                .setInputData(modeToData(serverMode,remoteMode))
                .build()
        }

        private fun modeToData(serverMode: Boolean, remoteMode:Boolean): Data {
            return Data.Builder()
                .putBoolean(NAME_SERVER_MODE,serverMode)
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