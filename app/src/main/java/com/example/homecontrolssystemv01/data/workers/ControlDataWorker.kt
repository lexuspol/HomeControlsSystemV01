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

class ControlDataWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val apiService = ApiFactory.apiService

    private val mapper = DataMapper()

    private var controlMode = workerParameters.inputData.getInt(NAME_DATA_CONTROL_MODE,0)

        override suspend fun doWork(): Result {

            Log.d("HCS_ControlDataWorker","CONTROL = $controlMode")

            try {

                val jsonContainer = when(controlMode){
                    0-> apiService.getData()
                    23-> apiService.buttonLightSleep()
                    24-> apiService.buttonLightChild()
                    else -> {apiService.getData()}
                }

                val dataDtoList = mapper.mapJsonContainerToListValue(jsonContainer)

                Log.d("HCS_RefreshDataWorker",dataDtoList[0].value.toString())

                //val dataDbModelList = dataDtoList.map {
                //    mapper.valueDtoToDbModel(it)
               // }

                //DataList.movieListResponse = dataDbModelList

            } catch (e: Exception) {
                Log.d("HCS_RefreshDataWorker", e.toString())
            }

            return Result.success()
    }

    companion object {

        const val NAME_WORKER_CONTROL = "ControlDataWorker_ONE_TIME"
        const val NAME_DATA_CONTROL_MODE = "Control_MODE"

        fun makeRequestOneTime(controlMode:Int): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<ControlDataWorker>()
                .setConstraints(makeConstraints())
                .setInputData(modeToData(controlMode))
                .build()
        }

        private fun modeToData(controlMode:Int): Data {
            return Data.Builder()
                .putInt(NAME_DATA_CONTROL_MODE,controlMode)
                .build()
        }

        private fun makeConstraints (): Constraints{
            return Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

        }

    }
}