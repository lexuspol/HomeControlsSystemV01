package com.example.homecontrolssystemv01.data.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.homecontrolssystemv01.data.database.AppDatabase
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.network.ApiFactory
import com.example.homecontrolssystemv01.domain.model.ControlInfo


class ControlDataWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val apiService = ApiFactory.apiService
    private val dataDao = AppDatabase.getInstance(context).dataDao()

    private val mapper = DataMapper()

    private var inputDataMap = workerParameters.inputData.keyValueMap

        override suspend fun doWork(): Result {




            try {

                val controlInfo = ControlInfo(
                inputDataMap[ID] as Int,
                inputDataMap[VALUE] as String,
                inputDataMap[TYPE] as Int
                )


                Log.d("HCS_ControlDataWorker","CONTROL = $controlInfo")
                val jsonContainer = when(controlInfo.id){
                    //0-> apiService.getData()
                    23-> apiService.buttonLightSleep()
                    24-> apiService.buttonLightChild()
                    37-> apiService.setMeterElectricity(controlInfo.value)
                    else -> {apiService.getData()}
                }


                val dataDtoList = mapper.mapJsonContainerToListValue(jsonContainer)

                Log.d("HCS_RefreshDataWorker",dataDtoList[0].value.toString())

                val dataDbModelList = dataDtoList.map {
                    mapper.valueDtoToDbModel(it)
                }
                dataDao.insertValue(dataDbModelList)

            } catch (e: Exception) {
                Log.d("HCS_RefreshDataWorker", e.toString())
            }

            return Result.success()
    }

    companion object {


        const val NAME_WORKER_CONTROL = "ControlDataWorker_ONE_TIME"

        const val ID = "id"
        const val VALUE = "value"
        const val TYPE = "type"

        fun makeRequestOneTime(controlInfo: ControlInfo): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<ControlDataWorker>()
                .setConstraints(makeConstraints())
                .setInputData(modeToData(controlInfo))
                .build()
        }

        private fun modeToData(controlInfo: ControlInfo): Data {
            return Data.Builder()
                .putAll(mapOf(
                    ID to controlInfo.id,
                    VALUE to controlInfo.value,
                    TYPE to controlInfo.type
                ))
                .build()
        }

        private fun makeConstraints (): Constraints{
            return Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

        }

    }
}