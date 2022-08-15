package com.example.homecontrolssystemv01.data.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.homecontrolssystemv01.data.database.AppDatabase
import com.example.homecontrolssystemv01.data.database.DataSettingDbModel
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.domain.model.DataSetting


class SettingDataWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val dataDao = AppDatabase.getInstance(context).dataDao()

    private var inputDataMap = workerParameters.inputData.keyValueMap

    override suspend fun doWork(): Result {

            try {
                val dataSetting = DataSettingDbModel(
                    inputDataMap[ID] as Int,
                    inputDataMap[VISIBLE] as Boolean,
                    inputDataMap[LIMIT_MODE] as Boolean,
                    inputDataMap[LIMIT_MAX] as Float,
                    inputDataMap[LIMIT_MIN] as Float,
                    inputDataMap[SET_COUNTER]  as Long
                )
                dataDao.insertDataSetting(dataSetting)
                Log.d("HCS_SettingDataWorker", "write setting $dataSetting")

            } catch (e: Exception) {
                Log.d("HCS_SettingDataWorker", e.toString())
            }
        return Result.success()
    }

        companion object {

            const val NAME_WORKER_SETTING = "SettingDataWorker_ONE_TIME"
            const val ID = "id"
            const val VISIBLE = "visible"
            const val LIMIT_MODE = "limitMode"
            const val LIMIT_MAX = "limitMax"
            const val LIMIT_MIN = "limitMin"
            const val SET_COUNTER = "setCounter"

            fun makeRequestOneTime(dataSetting: DataSetting): OneTimeWorkRequest {
                return OneTimeWorkRequestBuilder<SettingDataWorker>()
                    .setConstraints(makeConstraints())
                    .setInputData(modeToData(dataSetting))
                    .build()
            }

            private fun modeToData(dataSetting: DataSetting): Data {
                return Data.Builder()
                    .putAll(mapOf(
                        ID to dataSetting.id,
                    VISIBLE to dataSetting.visible,
                        LIMIT_MODE to dataSetting.limitMode,
                        LIMIT_MAX to dataSetting.limitMax,
                        LIMIT_MIN to dataSetting.limitMin,
                        SET_COUNTER to dataSetting.setCounter
                    ))
                    .build()
            }

            private fun makeConstraints(): Constraints {
                return Constraints.Builder()
                    //.setRequiredNetworkType(NetworkType.UNMETERED)
                    .build()

            }

        }
    }
