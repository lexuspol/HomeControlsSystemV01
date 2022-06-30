package com.example.homecontrolssystemv01.data.workers

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import androidx.work.*
import com.example.homecontrolssystemv01.data.DataList
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.domain.Parameters
import kotlinx.coroutines.delay


import java.util.concurrent.TimeUnit

class RefreshDataWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val mapper = DataMapper()

    private val mode = workerParameters.inputData.getString(NAME_DATA_MODE)


        override suspend fun doWork(): Result {

            while (true) {

                try {

                    val ssid = DataList.ssidState.value
                    Log.d("HCS_RefreshDataWorker", "SSID - $ssid")


                } catch (e: Exception) {
                    Log.d("HCS_RefreshDataWorker", e.toString())
                }

                delay(10000)

            }



    }

    companion object {

        const val NAME_PERIODIC = "RefreshDataWorker_PERIODIC"
        const val NAME_ONE_TIME = "RefreshDataWorker_ONE_TIME"
        const val NAME_DATA_MODE = "MODE"


        fun makeRequestPeriodic(parameters: Parameters): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<RefreshDataWorker>(15,
                TimeUnit.MINUTES)
                .setConstraints(makeConstraints())
                .setInputData(modeToData(parameters))
                .build()
        }

        fun makeRequestOneTime(parameters: Parameters): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RefreshDataWorker>()
                .setConstraints(makeConstraints())
                .setInputData(modeToData(parameters))
                .build()
        }

        private fun modeToData(parameters: Parameters): Data {
            return Data.Builder()
                .putString(NAME_DATA_MODE,parameters.mode.name)
                .build()
        }

        private fun makeConstraints (): Constraints{
            return Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

        }

    }
}