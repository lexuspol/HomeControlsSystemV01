package com.example.homecontrolssystemv01.data.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
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

    private val _context = context

    private val apiService = ApiFactory.apiService

    private val dataDao = AppDatabase.getInstance(context).dataDao()

    private val myRef = Firebase.database(MainRepositoryImpl.FIREBASE_URL).getReference(
        MainRepositoryImpl.FIREBASE_PATH
    )

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val notId = 1

    private val idTime = -1//id дата время с JSON

    private val mapper = DataMapper()

    private val delayTime:Long = 2000//milliSeconds
    private val limitErrorCount = 5//кол неудачных попыток, после чего результат - ошибка


    fun notification (time: String): Notification {

        createNotificationChannel()

        //val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
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



    private fun createForegroundInfo(progress: String): ForegroundInfo {
        return ForegroundInfo(notId, notification(progress))
    }


        override suspend fun doWork(): Result {

            delay(delayTime)

            var resultWork: Result
            var whileLoop = false//цикл при ошибке
            var errorCount = 0


            do{

                try {

                    setForeground(createForegroundInfo("Download"))

                    //write to base
                    val dataFromApiServer = runApiService()
                    dataDao.insertValue(dataFromApiServer)
                    myRef.setValue(dataFromApiServer)

                    //Log
                    val timeFromApiServer = mapper.convertDateServerToDateUI(dataFromApiServer.find {
                        it.id == idTime
                    }?.value)
                    Log.d("HCS_PeriodicDataWorker","timePeriodic = $timeFromApiServer")

                    val batteryPer = getBatteryPct()

                    Log.d("HCS_PeriodicDataWorker","batteryPer = $batteryPer")
                    apiService.setBatteryPer(batteryPer)

                    //Message
                    dataDao.insertMessage(
                        MessageDbModel(
                            Date().time,
                            0,
                            0,
                            "Периодическое обновление данных"))


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
                    errorCount = 0
                    Log.d("HCS_PeriodicDataWorker", "errorCount = $errorCount")

                    dataDao.insertMessage(
                        MessageDbModel(
                            Date().time,
                            0,
                            2,
                            "Ошибка периодической загрузки данных"))

                    resultWork = Result.success()
                }else{
                    resultWork = Result.success()
                }

            }while (whileLoop)


            return resultWork
        }

    private fun getBatteryPct():Float {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            _context.registerReceiver(null, ifilter)
        }
        val batteryPct: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }
        //Log.d("HCS_fromMainViewModel","battery = $batteryPct %")

        return (batteryPct ?: 0) as Float
    }





    private suspend fun runApiService():List<DataDbModel>{
        val jsonContainer = apiService.getData()
        val dataDtoList = mapper.mapJsonContainerToListValue(jsonContainer)
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