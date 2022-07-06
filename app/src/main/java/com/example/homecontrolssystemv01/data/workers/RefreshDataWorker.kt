package com.example.homecontrolssystemv01.data.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.homecontrolssystemv01.data.DataList
import com.example.homecontrolssystemv01.data.FirebaseFactory
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.network.ApiFactory
import kotlinx.coroutines.delay


import java.util.concurrent.TimeUnit

class RefreshDataWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val apiService = ApiFactory.apiService

    private val mapper = DataMapper()

    private val serverMode = workerParameters.inputData.getBoolean(NAME_DATA_MODE,false)

        override suspend fun doWork(): Result {

            if (serverMode) workStart() else{
                while (true){
                    workStart()
                    delay(30000)
                }
            }

            return Result.success()
    }

    private suspend fun workStart(){

        try {

            val jsonContainer = apiService.getData()
            val dataDtoList = mapper.mapJsonContainerToListValue(jsonContainer)

            Log.d("HCS_RefreshDataWorker",dataDtoList[0].value.toString())

            val dataDbModelList = dataDtoList.map {
                mapper.valueDtoToDbModel(it)
            }

            DataList.movieListResponse = dataDbModelList

            if (serverMode){
                FirebaseFactory.setDataToFirebase(dataDbModelList)
            }


        } catch (e: Exception) {
            Log.d("HCS_RefreshDataWorker", e.toString())
        }

    }

    companion object {

        const val NAME_PERIODIC = "RefreshDataWorker_PERIODIC"
        const val NAME_ONE_TIME = "RefreshDataWorker_ONE_TIME"
        const val NAME_DATA_MODE = "MODE"


        fun makeRequestPeriodic(serverMode: Boolean): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<RefreshDataWorker>(15,
                TimeUnit.MINUTES)
                .setConstraints(makeConstraints())
                .setInputData(modeToData(serverMode))
                .build()
        }

        fun makeRequestOneTime(serverMode: Boolean): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RefreshDataWorker>()
                .setConstraints(makeConstraints())
                .setInputData(modeToData(serverMode))
                .build()
        }

        private fun modeToData(serverMode: Boolean): Data {
            return Data.Builder()
                .putBoolean(NAME_DATA_MODE,serverMode)
                .build()
        }

        private fun makeConstraints (): Constraints{
            return Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

        }

    }
}