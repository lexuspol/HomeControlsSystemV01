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
import com.example.homecontrolssystemv01.data.database.DataDbModel
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
    private val mapper = DataMapper()

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
        return list.toList()
    }

    //удаление лишних логов
    private val logRemovedEventListenerRev1: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            val maxIndex = 200
           // Log.d("HCS", "id = ${snapshot.key} count = ${snapshot.childrenCount}")
            val listIndex = mutableListOf<String>()

            if (snapshot.childrenCount > maxIndex) {
                snapshot.children.forEachIndexed { index, dataSnapshot ->
                    if (index < (snapshot.childrenCount - maxIndex)) {
                        listIndex.add(dataSnapshot.key.toString())
                    }
                }
            }
            listIndex.forEach {
                snapshot.ref.child(it).removeValue()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w("HCS_FIREBASE_ERROR", "Failed to read shop value.", error.toException())
        }
    }

    override suspend fun doWork(): Result {

        try {

            val ssid = wifiManager.connectionInfo.ssid

            if (ssid == ssidSetting) {

                val jsonContainer = apiService.getData()
                val dataDtoList = mapper.mapJsonContainerToListValue(jsonContainer)
                val dataDbModelList = dataDtoList.map {
                    mapper.valueDtoToDbModel(it)
                }
                myRef.getReference(MainRepositoryImpl.FIREBASE_PATH).setValue(dataDbModelList)

                val mainDeviceName =
                    dataDbModelList.find { it.id == DataID.mainDeviceName.id }?.value

                if (mainDeviceName == infoDevice) {
                    apiService.setBatteryPer(getBatteryPct())
                    val timeFromApiServer = mapper.convertDateServerToDateUI(dataDbModelList.find {
                        it.id == DataID.lastTimeUpdate.id
                    }?.value, dataFormat)

                    val timeLong = convertStringTimeToLong(timeFromApiServer, dataFormat)
                    if (timeLong != -1L) {
                        writeLogToRemoveServer(dataDbModelList, timeLong)
                    }
                }
                insertMessage(_context, dataDao, 1007)
            }

        } catch (e: Exception) {
            Log.d("HCS_PeriodicDataWorker", e.toString())
            insertMessage(_context, dataDao, 1006)
        }
        return Result.success()
    }

    private fun writeLogToRemoveServer(dataDbModelList: List<DataDbModel>, timeLong: Long) {

        val loggingPeriodicIdList = mutableListOf<Int>()
        val loggingLastDayIdList = mutableListOf<Int>()
        val calendar = Calendar.getInstance()
        calendar.time = Date(timeLong)
        val calendarDay = calendar.get(Calendar.DAY_OF_MONTH)

        //разделяем на типы логи
        logSettingList.forEach {
            when (LogKey.valueOf(it.logKey).type) {
                LoggingType.LOGGING_PERIODIC -> loggingPeriodicIdList.add(it.logId)
                LoggingType.LOGGING_ONE_DAY -> loggingLastDayIdList.add(it.logId)
                else -> {}
            }
        }

        dataDbModelList.forEach { data ->

            val dataType = DataType.values().find { it.dataTypeNumber == data.type }
            val idPath = data.id.toString()

            if (dataType != null) {

                if (loggingPeriodicIdList.contains(data.id)) {

                    val periodicRef = myRef.getReference(MainRepositoryImpl.FIREBASE_PATH_LOG)
                        .child(idPath)
                        .child(LoggingType.LOGGING_PERIODIC.name)
                        .child(dataType.name)

                    periodicRef.child(timeLong.toString()).setValue(data.value.toString())
                    periodicRef.addListenerForSingleValueEvent(logRemovedEventListenerRev1)

                }

                if (loggingLastDayIdList.contains(data.id) && logDay != calendarDay) {

                    val oneDayRef = myRef.getReference(MainRepositoryImpl.FIREBASE_PATH_LOG)
                        .child(idPath)
                        .child(LoggingType.LOGGING_ONE_DAY.name)
                        .child(dataType.name)

                    oneDayRef.child(timeLong.toString()).setValue(data.value.toString())
                    oneDayRef.addListenerForSingleValueEvent(logRemovedEventListenerRev1)
                }
            }

        }

        sharedPref.edit().putInt(KEY_LOG_DAY, calendarDay).apply()
       // Log.d("HCS", "logDay2 = $logDay, calendarDay2 = $calendarDay")
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