package com.example.homecontrolssystemv01.data.workers

import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
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

class PeriodicDataWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val contextWorker= context

    private val apiService = ApiFactory.apiService

    private val dataDao = AppDatabase.getInstance(context).dataDao()

    private val myRef = Firebase.database(MainRepositoryImpl.FIREBASE_URL).getReference(
        MainRepositoryImpl.FIREBASE_PATH
    )

    private val notId = -1

    private val mapper = DataMapper()

    private val delayTime:Long = 2000//milliSeconds
    private val limitErrorCount = 5//кол неудачных попыток, после чего результат - ошибка

    fun notification (progress: String): Notification {
        val intent = WorkManager.getInstance(contextWorker).createCancelPendingIntent(id)
        return NotificationCompat.Builder(contextWorker,notId.toString())
            .setContentTitle("Title change")
            .setContentText(progress)
            .addAction(android.R.drawable.ic_delete,"cancel", intent)
            .build()

    }

    fun createForegroundInfo(progress: String): ForegroundInfo{
        return ForegroundInfo(notId, notification(progress))
    }


        override suspend fun doWork(): Result {






            delay(delayTime)

            var resultWork: Result
            var whileLoop = false//цикл делаем если LOCAL или при ошибке
            var errorCount = 0

            do{

                try {
                    //delay(delayTime)//немного ждем, так как бывает вылетает ошибка
                            val dataFromApiServer = runApiService()
                            dataDao.insertValue(dataFromApiServer)
                            myRef.setValue(dataFromApiServer)
                            Log.d("HCS_PeriodicDataWorker","Write to DB/Firebase from Network")
                            dataDao.insertMessage(MessageDbModel(Date().time,0,0,"Периодическое обновление данных"))
                            whileLoop = false
                    errorCount = 0

                } catch (e: Exception) {
                    Log.d("HCS_PeriodicDataWorker", e.toString())
                    delay(delayTime)
                    whileLoop = true
                    errorCount += 1
                    //resultWork = Result.success()
                }

                if (errorCount>limitErrorCount) {
                    whileLoop = false
                    Log.d("HCS_PeriodicDataWorker", "errorCount = $errorCount")
                    dataDao.insertMessage(MessageDbModel(Date().time,0,2,"Ошибка загрузки данных"))
                    resultWork = Result.failure()
                }else{
                    resultWork = Result.success()
                }



            }while (whileLoop)


            return resultWork
        }



    private suspend fun runApiService():List<DataDbModel>{

        val jsonContainer = apiService.getData()
        val dataDtoList = mapper.mapJsonContainerToListValue(jsonContainer)
        Log.d("HCS_PeriodicDataWorker",dataDtoList[0].value.toString())

        val dataDbModelList = dataDtoList.map {
            mapper.valueDtoToDbModel(it)
        }

        return dataDbModelList
    }



    companion object {

        const val NAME_PERIODIC = "RefreshDataWorker_PERIODIC"



        fun makeRequestPeriodic(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<PeriodicDataWorker>(20,
                TimeUnit.MINUTES)
                .setConstraints(makeConstraints())
                //.setInputData(modeToData(serverMode,remoteMode))
                .build()
        }



//        private fun modeToData(serverMode: Boolean, remoteMode:Boolean): Data {
//            return Data.Builder()
//                .putBoolean(NAME_SERVER_MODE,serverMode)
//                .putBoolean(NAME_REMOTE_MODE,remoteMode)
//                .build()
//        }

        private fun makeConstraints (): Constraints{
            return Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

        }

    }
}