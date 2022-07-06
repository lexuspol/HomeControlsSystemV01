package com.example.homecontrolssystemv01.data.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.homecontrolssystemv01.data.DataList
import com.example.homecontrolssystemv01.data.FirebaseFactory
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.network.ApiFactory
import com.example.homecontrolssystemv01.presentation.enums.Mode
import kotlinx.coroutines.delay


import java.util.concurrent.TimeUnit

class RefreshDataWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val apiService = ApiFactory.apiService

    private val mapper = DataMapper()

    private val mode = workerParameters.inputData.getString(NAME_DATA_MODE)


        override suspend fun doWork(): Result {
            when (mode) {
                Mode.SERVER.name -> workStart(mode)
                Mode.CLIENT.name -> {
                    while (true){
                        workStart(mode)
                        delay(30000)
                    }
                }
                else -> {

                    Log.d("HCS_RefreshDataWorker","mode not SERVER or CLIENT")

                }
            }

            return Result.success()
    }

    private suspend fun workStart(mode:String){

        try {

            val jsonContainer = apiService.getData()
            val dataDtoList = mapper.mapJsonContainerToListValue(jsonContainer)

            Log.d("HCS_RefreshDataWorker",dataDtoList[0].value.toString())

            val dataDbModelList = dataDtoList.map {
                mapper.valueDtoToDbModel(it)
            }

            DataList.movieListResponse = dataDbModelList

            if (mode == Mode.SERVER.name){
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


        fun makeRequestPeriodic(mode: String): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<RefreshDataWorker>(15,
                TimeUnit.MINUTES)
                .setConstraints(makeConstraints())
                .setInputData(modeToData(mode))
                .build()
        }

        fun makeRequestOneTime(mode: String): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RefreshDataWorker>()
                .setConstraints(makeConstraints())
                .setInputData(modeToData(mode))
                .build()
        }

        private fun modeToData(mode: String): Data {
            return Data.Builder()
                .putString(NAME_DATA_MODE,mode)
                .build()
        }

        private fun makeConstraints (): Constraints{
            return Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

        }

    }
}