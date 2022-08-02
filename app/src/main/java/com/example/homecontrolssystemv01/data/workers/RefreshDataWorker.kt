package com.example.homecontrolssystemv01.data.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.homecontrolssystemv01.data.FirebaseFactory
import com.example.homecontrolssystemv01.data.database.AppDatabase
import com.example.homecontrolssystemv01.data.database.DataDbModel
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.network.ApiFactory
import kotlinx.coroutines.delay


import java.util.concurrent.TimeUnit

class RefreshDataWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val apiService = ApiFactory.apiService

    private val dataDao = AppDatabase.getInstance(context).dataDao()

    private val mapper = DataMapper()

    private val serverMode = workerParameters.inputData.getBoolean(NAME_SERVER_MODE,false)
    private val remoteMode = workerParameters.inputData.getBoolean(NAME_REMOTE_MODE,false)

        override suspend fun doWork(): Result {

            var resultWork: Result

            try {

                if (remoteMode) {
                    remoteData()
                } else {
                    dataDao.insertValue(apiServiceAndDatabase())
                }

                if (serverMode) {
                    dataDao.insertValue(apiServiceAndDatabase())
                    FirebaseFactory.setDataToFirebase(apiServiceAndDatabase())
                } else {
                    while (true) {
                        delay(30000)
                        apiServiceAndDatabase()
                    }
                }

                resultWork = Result.success()

            } catch (e: Exception) {
                Log.d("HCS_RefreshDataWorker", e.toString())
                resultWork = Result.retry()
            }

            return resultWork
        }


//            try {
//                do{
//
//                    if (serverMode){
//                        FirebaseFactory.setDataToFirebase(apiServiceAndDatabase())
//                    }else{
//                        if (remoteMode){
//                            Log.d("HCS_RefreshDataWorker", "e.toString()")
//                        }else {
//                            apiServiceAndDatabase()
//                            delay(10000)
//                        }
//                    }
//
//                    resultWork = Result.success()
//
//                }while (!serverMode)
//
//
//
//            } catch (e: Exception){
//                Log.d("HCS_RefreshDataWorker", e.toString())
//                resultWork = Result.retry()
//            }





    private suspend fun apiServiceAndDatabase():List<DataDbModel>{

        val jsonContainer = apiService.getData()
        val dataDtoList = mapper.mapJsonContainerToListValue(jsonContainer)
        Log.d("HCS_RefreshDataWorker",dataDtoList[0].value.toString())
        val dataDbModelList = dataDtoList.map {
            mapper.valueDtoToDbModel(it)
        }

        return dataDbModelList
    }

    private fun remoteData(){
        Log.d("HCS_RefreshDataWorker","Firebase write")
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