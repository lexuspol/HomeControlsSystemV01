package com.example.homecontrolssystemv01.data.workers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.util.Log
import androidx.work.*
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.data.database.AppDatabase
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.network.ApiFactory
import com.example.homecontrolssystemv01.data.repository.MainRepositoryImpl
import com.example.homecontrolssystemv01.domain.enum.DataType
import com.example.homecontrolssystemv01.domain.enum.LogKey
import com.example.homecontrolssystemv01.domain.enum.LoggingType
import com.example.homecontrolssystemv01.domain.model.setting.LogSetting
import com.example.homecontrolssystemv01.util.convertStringTimeToLong
import com.example.homecontrolssystemv01.util.insertMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.TimeUnit

class PeriodicDataWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val _context = context

    private var wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    //используем шаред так как воркер каждый раз обновляет поля, следовательно в них нельзя хранить данные
    private val sharedPref =
        context.getSharedPreferences(NAME_SHARED_PREFERENCES, Context.MODE_PRIVATE)

    private val apiService = ApiFactory.apiService

    private val dataDao = AppDatabase.getInstance(context).dataDao()

    private val myRef = Firebase.database(MainRepositoryImpl.FIREBASE_URL)
    private val dataFormat = context.resources.getString(R.string.data_format)

    private val ssidSetting = workerParameters.inputData.getString(NAME_SETTING_SSID)
    private val infoDevice = workerParameters.inputData.getString(NAME_INFO_DEVICE)

    private val logSettingList = getLoggingValueList(workerParameters)

    private var logDay = 0

    private fun getLoggingValueList(workerParameters: WorkerParameters): List<LogSetting> {

        val list = mutableListOf<LogSetting>()
        LogKey.values().forEach {
            list.add(
                LogSetting(workerParameters.inputData.getInt(it.name, 0), it.name)
            )
        }
        //Log.d("HCS","запуск - getLoggingValueList")
        return list.toList()
    }

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


    private val logRemovedEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            val maxIndex = 150
            snapshot.children.forEach { id ->
                //Log.d("HCS","count = ${id.childrenCount}")
                val listIndex = mutableListOf<String>()

                if (id.childrenCount > maxIndex) {
                    id.children.forEachIndexed { index, dataSnapshot ->
                        if (index < (id.childrenCount - maxIndex)) {
                            listIndex.add(dataSnapshot.key.toString())
                        }
                    }
                }
                listIndex.forEach {
                    id.ref.child(it).removeValue()
                }
            }


        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w("HCS_FIREBASE_ERROR", "Failed to read shop value.", error.toException())
        }
    }

    override suspend fun doWork(): Result {

        val loggingPeriodicIdList = mutableListOf<Int>()
        val loggingLastDayIdList = mutableListOf<Int>()

        try {

            //разделяем на типы логи
            logSettingList.forEach {

                when (LogKey.valueOf(it.logKey).type) {
                    LoggingType.LOGGING_PERIODIC -> loggingPeriodicIdList.add(it.logId)
                    LoggingType.LOGGING_ONE_DAY -> loggingLastDayIdList.add(it.logId)
                    else -> {}
                }
            }


            // Log.d("HCS",loggingValueList.toString())

            val ssid = wifiManager.connectionInfo.ssid

            if (ssid == ssidSetting) {

                val jsonContainer = apiService.getData()
                val dataDtoList = mapper.mapJsonContainerToListValue(jsonContainer)
                val dataDbModelList = dataDtoList.map {
                    mapper.valueDtoToDbModel(it)
                }
                myRef.getReference(MainRepositoryImpl.FIREBASE_PATH).setValue(dataDbModelList)

                myRef.getReference(MainRepositoryImpl.FIREBASE_PATH_LOG)
                    .addListenerForSingleValueEvent(logRemovedEventListener)

                val mainDeviceName =
                    dataDbModelList.find { it.id == DataID.mainDeviceName.id }?.value

                if (mainDeviceName == infoDevice) {
                    apiService.setBatteryPer(getBatteryPct())
                    val timeFromApiServer = mapper.convertDateServerToDateUI(dataDbModelList.find {
                        it.id == DataID.lastTimeUpdate.id
                    }?.value, dataFormat)
                    val timeLong = convertStringTimeToLong(timeFromApiServer, dataFormat)

                    if (timeLong != -1L) {

                        val calendar = Calendar.getInstance()
                        calendar.time = Date(timeLong)

                        val calendarDay = calendar.get(Calendar.DAY_OF_MONTH)

                        // Log.d("HCS","logDay1 = $logDay, calendarDay1 = $calendarDay")

                        dataDbModelList.forEach {

                            if (loggingPeriodicIdList.contains(it.id)) {

                                //val type = DataType.INT

                                val path = "${it.id}" +
                                        "${LoggingType.LOGGING_PERIODIC.separator}" +
                                        LoggingType.LOGGING_PERIODIC.name

                                myRef.getReference(MainRepositoryImpl.FIREBASE_PATH_LOG)
                                    .child(path)
                                    .child(timeLong.toString())
                                    .setValue(it.value.toString())
                                //Log.d("HCS","период запись элемент с ID - ${it.id}")
                            }

                            if (loggingLastDayIdList.contains(it.id) && logDay != calendarDay) {

                                val path = "${it.id}" +
                                        "${LoggingType.LOGGING_PERIODIC.separator}" +
                                        LoggingType.LOGGING_ONE_DAY.name

                                myRef.getReference(MainRepositoryImpl.FIREBASE_PATH_LOG)
                                    .child(path)
                                    .child(timeLong.toString())
                                    .setValue(it.value.toString())
                            }

//                                    when(it.id){
//                                        100,101,124 -> {
//                                            myRef.getReference(MainRepositoryImpl.FIREBASE_PATH_LOG)
//                                                .child(it.id.toString())
//                                                .child(timeLong.toString())
//                                                .setValue(it.value.toString())
//                                        }
//                                    }
                        }

                        sharedPref.edit().putInt(KEY_LOG_DAY, calendarDay).apply()
                        // Log.d("HCS","logDay2 = $logDay, calendarDay2 = $calendarDay")
                    }

                    //

                }

                insertMessage(_context, dataDao, 1007)

            }

            //setForeground(createForegroundInfo("Download"))

        } catch (e: Exception) {
            Log.d("HCS_PeriodicDataWorker", e.toString())
            insertMessage(_context, dataDao, 1006)
        }

        return Result.success()
    }


    private fun getBatteryPct(): Float {
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
        const val NAME_SHARED_PREFERENCES = "PERIODIC_DATA_WORKER"
        const val KEY_LOG_DAY = "LOG_DAY"

        fun makeRequestPeriodic(
            ssidSetting: String,
            infoDevice: String,
            listLog: List<LogSetting>
        ): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<PeriodicDataWorker>(
                20,
                TimeUnit.MINUTES
            )
                .setConstraints(makeConstraints())
                //.setExpedited(OutOfQuotaPolicy.DROP_WORK_REQUEST)
                .setInputData(modeToData(ssidSetting, infoDevice, listLog))
                .build()
        }

        private fun modeToData(
            ssidSetting: String,
            infoDevice: String,
            listLog: List<LogSetting>
        ): Data {

            val builder = Data.Builder()
                .putString(NAME_SETTING_SSID, ssidSetting)
                .putString(NAME_INFO_DEVICE, infoDevice)
            listLog.forEach {
                builder.putInt(it.logKey, it.logId)
            }
            return builder.build()
        }

        private fun makeConstraints(): Constraints {
            return Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.UNMETERED)
                .build()
        }

    }

    init {
        logDay = sharedPref.getInt(KEY_LOG_DAY, 0)
    }
}