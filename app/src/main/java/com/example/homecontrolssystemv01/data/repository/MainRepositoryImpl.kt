package com.example.homecontrolssystemv01.data.repository

import android.app.Application
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.work.*
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.data.database.AppDatabase
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.workers.PeriodicDataWorker
import com.example.homecontrolssystemv01.data.workers.RefreshDataWorker
import com.example.homecontrolssystemv01.domain.DataRepository
import com.example.homecontrolssystemv01.domain.model.*
import com.example.homecontrolssystemv01.domain.model.data.DataModel
import com.example.homecontrolssystemv01.domain.model.logging.LogItem
import com.example.homecontrolssystemv01.domain.model.message.Message
import com.example.homecontrolssystemv01.domain.model.setting.ConnectSetting
import com.example.homecontrolssystemv01.domain.model.setting.DataSetting
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainRepositoryImpl(private val application: Application) : DataRepository {

    private val infoDevice = "${Build.MODEL} (${Build.ID})"

    private val workManager = WorkManager.getInstance(application)

    private val dataDao = AppDatabase.getInstance(application).dataDao()

    private val mapper = DataMapper()

    private val listDescription = application.resources.getStringArray(R.array.data)
    private val alarmMessage = application.resources.getStringArray(R.array.alarmMessage)
    private val listResources = listOf(listDescription, alarmMessage)
    private val dataFormat = application.resources.getString(R.string.data_format)

    private var _connectSetting = ConnectSetting()

    var _connectInfo: MutableState<ConnectInfo> = mutableStateOf(ConnectInfo())

    private val myRef = Firebase.database(FIREBASE_URL)//.getReference(FIREBASE_PATH)
    private var addRemoteListener = false

    private var addLogListener = false
    private var addLogListenerId = ""

    private val logMapMS = mutableStateOf<Map<String, LogItem>>(mapOf())
    private val logIdListMS = mutableStateOf<List<String>>(listOf())

    //создаем слушателя для Firebase, в другом месте сложно, так как запись в базу происходит в карутине
    //запускаем слушателя в loadData
    private val valueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            val dataFirebaseID = snapshot.child("id").getValue<Int>() ?: 0
            val dataFirebaseValue = snapshot.child("value").getValue<String>() ?: "0"
            if (dataFirebaseID != 0) {
                // Log.d("HCS_FIREBASE", "start worker value = $dataFirebaseValue")
                putControl(ControlInfo(dataFirebaseID, dataFirebaseValue, 0, true))
                removeValueControlRemote()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w("HCS_FIREBASE_ERROR", "Failed to read value.", error.toException())
        }
    }


    private fun getEventListener(key: String): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    when (key) {
                        "id" -> readLogIdList(snapshot)
                        "value" -> readLogValue(snapshot)
                    }
                } catch (e: Exception) {
                    Toast.makeText(application, "Remote base error", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("HCS_FIREBASE_ERROR", "Failed to read value.", error.toException())
            }
        }
    }

    fun readLogIdList(snapshot: DataSnapshot) {
        val listIndex = mutableListOf<String>()
        snapshot.children.forEach { id ->
            val idLog = id.key
            if (idLog != null) {
                listIndex.add(idLog)
            }
        }
        logIdListMS.value = listIndex.toList()
    }

    fun readLogValue(snapshot: DataSnapshot) {
        val keySnapshot = snapshot.key.toString()
        //Log.d("HCS","snapshot.key = ${snapshot.key}")
        val valueLog = snapshot.getValue<Map<String, String>>()

        if (!valueLog.isNullOrEmpty()) {
            val valueList = mutableListOf<Pair<Long, String>>()
            val logMap = mutableMapOf<String, LogItem>()
            valueLog.forEach { map ->
                try {
                    valueList.add(Pair(map.key.toLong(), map.value))
                } catch (e: Exception) {
                    Log.w("HCS_FIREBASE_ERROR", "Error cast long value", e)
                }
            }
            valueList.sortByDescending { it.first }
            logMap[keySnapshot] = LogItem(valueList.toList())
            logMapMS.value = logMap.toMap()
        }
    }


    override fun getDataList(): LiveData<List<DataModel>> {
        return Transformations.map(dataDao.getValueList()) { list ->
            list.map {
                mapper.mapDataToEntity(it, listResources, dataFormat, true)
            }
        }
    }

    override fun getMessageList(): LiveData<List<Message>> {
        return Transformations.map(dataDao.getMessageList()) { list ->
            list.map {
                mapper.mapMessageToEntity(it)
            }
        }
    }

    override suspend fun putMessage(message: Message) {
        dataDao.insertMessage(mapper.mapEntityToMessage(message))
    }

    //узнать про исключения room
    override suspend fun deleteMessage(id: Int) {
        if (id == 0) dataDao.deleteAllMessage() else dataDao.deleteMessage(id)
    }

    override suspend fun deleteData(id: Int) {
        dataDao.deleteData(id)
    }

    override fun getLogMap(idKey: String): MutableState<Map<String, LogItem>> {
        Log.d("HCS", "fun getLogMap")
        if (addLogListener && idKey != "" && addLogListenerId != idKey) {
            myRef.getReference(FIREBASE_PATH_LOG).child(idKey)
                .addListenerForSingleValueEvent(getEventListener("value"))

            addLogListenerId = idKey
            addLogListener = true
        }
        return logMapMS
    }

    override fun getLogIdList(): MutableState<List<String>> {
        //myRef.getReference(FIREBASE_PATH_LOG).addListenerForSingleValueEvent(logIdEventListener)
        myRef.getReference(FIREBASE_PATH_LOG).addListenerForSingleValueEvent(getEventListener("id"))
        Log.d("HCS", "fun getLogIdList()")
        return logIdListMS
    }

    override fun deleteLogItem(idKey: String) {
        myRef.getReference(FIREBASE_PATH_LOG).child(idKey).removeValue()
    }

    override fun getDataSetting(): LiveData<List<DataSetting>> {
        return Transformations.map(dataDao.getSettingList()) { list ->
            list.map {
                mapper.settingDbModelToEntity(it)
            }
        }
    }

    override fun getDataConnect(): MutableState<ConnectInfo> = _connectInfo

    override fun loadData(connectSetting: ConnectSetting) {

        _connectSetting = connectSetting

        //  Log.d("HCS","listLogSetting = ${connectSetting.listLogSetting}")

        startRefreshDataWorker(_connectSetting.ssid, 0, "0", _connectSetting.cycleMode, false)

        if (_connectSetting.serverMode) {
            startPeriodicDataWorker()

            if (addRemoteListener) {

                //удаляем чтобы не весела команда
                removeValueControlRemote()

                //любое устройство в режиме сервера устанавливает слушателя,
                //и при удаленном управлении запускает воркер
                //но в воркере прверка на главное устройство
                //только главное устройство может записать команду в контроллер

                myRef.getReference(FIREBASE_PATH_CONTROL).addValueEventListener(valueEventListener)
                //Log.d("HCS","addListener")

                addRemoteListener = false
            }

        } else {
            workManager.cancelUniqueWork(PeriodicDataWorker.NAME_PERIODIC)
            myRef.getReference(FIREBASE_PATH_CONTROL).removeEventListener(valueEventListener)
        }

    }


    private fun startRefreshDataWorker(
        ssidSetting: String,
        idControl: Int,
        valueControl: String,
        cycleMode: Boolean,
        remoteControl: Boolean
    ) {
        try {

            workManager.enqueueUniqueWork(
                RefreshDataWorker.NAME_ONE_TIME,
                ExistingWorkPolicy.REPLACE,
                RefreshDataWorker.makeRequestOneTime(
                    ssidSetting,
                    idControl, valueControl, cycleMode, infoDevice, remoteControl
                )
            )

        } catch (e: Exception) {
            Toast.makeText(application, "Worker error", Toast.LENGTH_LONG).show()
        }
    }

    private fun startPeriodicDataWorker() {
        workManager.enqueueUniquePeriodicWork(
            PeriodicDataWorker.NAME_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicDataWorker.makeRequestPeriodic(
                _connectSetting.ssid,
                infoDevice,
                _connectSetting.listLogSetting
            )
        )
    }

    override fun closeConnect() {
        workManager.cancelUniqueWork(RefreshDataWorker.NAME_ONE_TIME)
    }

    override fun putControl(controlInfo: ControlInfo) {
        startRefreshDataWorker(
            _connectSetting.ssid,
            controlInfo.id,
            controlInfo.value,
            _connectSetting.cycleMode,
            controlInfo.remoteControl
        )
    }

    override suspend fun putDataSetting(dataSetting: DataSetting) {

        try {
            dataDao.insertDataSetting(mapper.settingToDbModel(dataSetting))
        } catch (e: Exception) {
            Log.d("HCS_putDataSetting", "Error Data Base")
        }

    }

    private fun removeValueControlRemote() {
        myRef.getReference(FIREBASE_PATH_CONTROL).setValue(ControlRemote())
    }

    init {
        addRemoteListener = true// разобраться нужно ли это делать тут или сразу объявить в поле
        addLogListener = true
    }

    companion object {
        const val FIREBASE_URL =
            "https://homesystemcontrolv01-default-rtdb.asia-southeast1.firebasedatabase.app"
        const val FIREBASE_PATH = "data"
        const val FIREBASE_PATH_CONTROL = "controlRemove"
        const val FIREBASE_PATH_LOG = "logging"
    }

}
