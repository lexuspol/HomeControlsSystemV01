package com.example.homecontrolssystemv01.data.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.homecontrolssystemv01.data.database.AppDatabase
import com.example.homecontrolssystemv01.data.database.DataDbModel
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.network.ApiFactory
import com.example.homecontrolssystemv01.data.repository.DataRepositoryImpl
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await


import java.util.concurrent.TimeUnit

class RefreshDataWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val apiService = ApiFactory.apiService

    private val dataDao = AppDatabase.getInstance(context).dataDao()

    private val myRef = Firebase.database(DataRepositoryImpl.FIREBASE_URL).getReference(
        DataRepositoryImpl.FIREBASE_PATH
    )

    private val mapper = DataMapper()

    private val serverMode = workerParameters.inputData.getBoolean(NAME_SERVER_MODE,false)
    private val remoteMode = workerParameters.inputData.getBoolean(NAME_REMOTE_MODE,false)

    private val delayTime:Long = 500//milliSeconds
    private val periodTime:Long = 10//seconds
    private val limitErrorCount = 5


        override suspend fun doWork(): Result {

            var resultWork: Result
            var whileLoop = false
            var errorCount = 0

            do{

                try {
                    delay(delayTime)

                    when{
                        remoteMode ->{
                            val dataSnapshot = getFirebaseData()
                            if ( dataSnapshot!= null) {
                                val dataFirebase = dataSnapshot.getValue<List<DataDbModel>>()

                                if(dataFirebase.isNullOrEmpty()){
                                    Log.d("HCS_RefreshDataWorker","Firebase NO data")
                                } else{
                                    dataDao.insertValue(dataFirebase)
                                    Log.d("HCS_RefreshDataWorker","Write to DB from Firebase")
                                }

                            }else{
                                Log.d("HCS_RefreshDataWorker","Firebase Empty Snapshot")
                            }

                            whileLoop = false
                        }

                        serverMode ->{
                            val dataFromApiServer = runApiService()
                            dataDao.insertValue(dataFromApiServer)
                            myRef.setValue(dataFromApiServer)
                            Log.d("HCS_RefreshDataWorker","Write to DB from Network")
                            whileLoop = false
                        }
                        !remoteMode && !serverMode ->{
                                dataDao.insertValue(runApiService())
                                delay(periodTime*1000-delayTime)
                            whileLoop = true

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
                    resultWork = Result.failure()
                }else{
                    resultWork = Result.success()
                }

                Log.d("HCS_RefreshDataWorker", "errorCount = $errorCount, whileLoop = $whileLoop")

            }while (whileLoop)


            return resultWork
        }

    private suspend fun getFirebaseData():DataSnapshot?{
        return try{
            val data = myRef.get().await()
            data
        } catch (e : Exception){
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