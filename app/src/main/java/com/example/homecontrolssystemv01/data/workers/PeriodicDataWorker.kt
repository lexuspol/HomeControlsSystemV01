package com.example.homecontrolssystemv01.data.workers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.work.*
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.data.database.AppDatabase
import com.example.homecontrolssystemv01.data.database.MessageDbModel
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.network.ApiFactory
import com.example.homecontrolssystemv01.data.repository.MainRepositoryImpl
import com.example.homecontrolssystemv01.util.insertMessage
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.TimeUnit

class PeriodicDataWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val _context = context

    private var wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val apiService = ApiFactory.apiService

    private val dataDao = AppDatabase.getInstance(context).dataDao()

    private val myRef = Firebase.database(MainRepositoryImpl.FIREBASE_URL).getReference(
        MainRepositoryImpl.FIREBASE_PATH
    )

    private val ssidSetting = workerParameters.inputData.getString(NAME_SETTING_SSID)
    private val infoDevice = workerParameters.inputData.getString(NAME_INFO_DEVICE)

//    private val notificationManager =
//        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val mapper = DataMapper()

//    fun notification (time: String): Notification {
//
//        createNotificationChannel()
//
//        //val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
//        return NotificationCompat.Builder(applicationContext,CHANNEL_ID)
//            .setContentTitle("Time update")
//            .setContentText(time)
//            .setSmallIcon(R.drawable.ic_launcher_background)
//            //.addAction(,"cancel", intent)
//            .build()
//    }

//    private fun createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "name"
//                //getString(R.string.channel_name)
//            val descriptionText = "description"
//                //getString(R.string.channel_description)
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//            }
//            // Register the channel with the system
//            notificationManager.createNotificationChannel(channel)
//        }
//    }

//    private fun createForegroundInfo(progress: String): ForegroundInfo {
//        return ForegroundInfo(notId, notification(progress))
//    }

        override suspend fun doWork(): Result {

                try {

                    val ssid =  wifiManager.connectionInfo.ssid

                    if (ssid == ssidSetting){

                        val jsonContainer = apiService.getData()
                        val dataDtoList = mapper.mapJsonContainerToListValue(jsonContainer)
                        val dataDbModelList = dataDtoList.map {
                            mapper.valueDtoToDbModel(it)
                        }
                        myRef.setValue(dataDbModelList)

                        val mainDeviceName = dataDbModelList.find { it.id == DataID.mainDeviceName.id }?.value

                        if (mainDeviceName == infoDevice){
                            apiService.setBatteryPer(getBatteryPct())
                        }

                        insertMessage(_context,dataDao,1007)

                    }

                    //setForeground(createForegroundInfo("Download"))

                } catch (e: Exception) {
                    Log.d("HCS_PeriodicDataWorker", e.toString())
                    insertMessage(_context,dataDao,1006)
                }

            return Result.success()
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

        return (batteryPct ?: 0) as Float
    }

    companion object {

        const val NAME_PERIODIC = "RefreshDataWorker_PERIODIC"
        private const val NAME_SETTING_SSID = "SSID"
        const val NAME_INFO_DEVICE = "Info_Device"

        fun makeRequestPeriodic(ssidSetting:String,infoDevice:String): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<PeriodicDataWorker>(20,
                TimeUnit.MINUTES)
                .setConstraints(makeConstraints())
                //.setExpedited(OutOfQuotaPolicy.DROP_WORK_REQUEST)
                .setInputData(modeToData(ssidSetting,infoDevice))
                .build()
        }

        private fun modeToData(ssidSetting:String,infoDevice:String): Data {
            return Data.Builder()
                .putString(NAME_SETTING_SSID,ssidSetting)
                .putString(NAME_INFO_DEVICE,infoDevice)
                .build()
        }

        private fun makeConstraints (): Constraints{
            return Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.UNMETERED)
                .build()
        }

    }
}