package com.example.homecontrolssystemv01.data.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

import androidx.work.*
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.data.database.AppDatabase
import com.example.homecontrolssystemv01.data.database.DataDbModel
import com.example.homecontrolssystemv01.data.database.MessageDbModel
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.network.ApiFactory
import com.example.homecontrolssystemv01.data.repository.MainRepositoryImpl
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
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

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val notId = 1

    private val mapper = DataMapper()

    private val delayTime:Long = 2000//milliSeconds
    private val limitErrorCount = 5//кол неудачных попыток, после чего результат - ошибка


    fun notification (time: String): Notification {

        createNotificationChannel()

        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
        return NotificationCompat.Builder(applicationContext,CHANNEL_ID)
            .setContentTitle("Time update")
            .setContentText(time)
            .setSmallIcon(R.drawable.ic_launcher_background)
            //.addAction(,"cancel", intent)
            .build()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
                //getString(R.string.channel_name)
            val descriptionText = "description"
                //getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
    }



    fun createForegroundInfo(progress: String): ForegroundInfo {
        return ForegroundInfo(notId, notification(progress))
    }


        override suspend fun doWork(): Result {

            delay(delayTime)

            var resultWork: Result
            var whileLoop = false//цикл делаем если LOCAL или при ошибке
            var errorCount = 0


            do{

                try {

                    setForeground(createForegroundInfo("Загрузка"))
                    val dataFromApiServer = runApiService()
                    //delay(60000)
                    dataDao.insertValue(dataFromApiServer)
                    myRef.setValue(dataFromApiServer)
                    Log.d("HCS_PeriodicDataWorker","Write to DB/Firebase from Network")
                    dataDao.insertMessage(MessageDbModel(Date().time,0,0,"Периодическое обновление данных"))




//            download("12", "34", callback = {
//                setForeground(createForegroundInfo(it))
//            })

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
                    dataDao.insertMessage(MessageDbModel(Date().time,0,2,"Ошибка периодической загрузки данных"))
                    resultWork = Result.success()
                }else{
                    resultWork = Result.success()
                }



            }while (whileLoop)


            return resultWork
        }

    suspend fun download (input:String,
                          output:String,
                          callback:suspend (progress:String)->Unit) {

        val dataFromApiServer = runApiService()
        dataDao.insertValue(dataFromApiServer)
        myRef.setValue(dataFromApiServer)
        Log.d("HCS_PeriodicDataWorker","Write to DB/Firebase from Network")
        dataDao.insertMessage(MessageDbModel(Date().time,0,0,"Периодическое обновление данных"))
        callback(dataFromApiServer[0].value.toString())
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
        const val CHANNEL_ID = "CHANNEL_ID"



        fun makeRequestPeriodic(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<PeriodicDataWorker>(20,
                TimeUnit.MINUTES)
                .setConstraints(makeConstraints())
                //.setExpedited(OutOfQuotaPolicy.DROP_WORK_REQUEST)
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