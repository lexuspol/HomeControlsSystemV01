package com.example.homecontrolssystemv01.data.repository

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.work.*
import androidx.work.WorkManager
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.data.ConnectSetting
import com.example.homecontrolssystemv01.data.database.AppDatabase
import com.example.homecontrolssystemv01.data.database.DataDbModel
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.workers.ControlDataWorker
import com.example.homecontrolssystemv01.data.workers.RefreshDataWorker
import com.example.homecontrolssystemv01.domain.model.Data
import com.example.homecontrolssystemv01.domain.DataRepository
import com.example.homecontrolssystemv01.domain.model.DataConnect
import com.example.homecontrolssystemv01.domain.model.ModeConnect
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class DataRepositoryImpl (private val application: Application): DataRepository {

    private var wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val workManager = WorkManager.getInstance(application)

    private val dataDao = AppDatabase.getInstance(application).dataDao()

    private  val mapper = DataMapper()
    private val intentFilter = IntentFilter()

    private val listDescription = application.resources.getStringArray(R.array.data)

    private var _connectSetting = ConnectSetting()

    //
    var _dataConnect:MutableState<DataConnect> = mutableStateOf(DataConnect())

    //запускаем бродкаст, он следит за состоянием сети, если сеть изменилась, то вызывается метод
    private val wifiScanReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val ssidFromWiFi = wifiManager.connectionInfo.ssid  // метод устарел, но другой очень муторный
            if (_dataConnect.value.ssidConnect == ssidFromWiFi){                                    //!!!!
                //Log.d("HCS_BroadcastReceiver","$ssidFromWiFi double")
            } else{
                startLoad(ssidFromWiFi)
            }
        }
    }

    private val myRef = Firebase.database(FIREBASE_URL).getReference(FIREBASE_PATH)

    //создаем слушателя для Firebase, в бругом месте сложно, так как запись в базу происходит в карутине
    //запускаем слуателя в loadData
    private val valueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            val dataFirebase = snapshot.getValue<List<DataDbModel>>()

            if (dataFirebase != null) {
                Log.d("HCS_FIREBASE", dataFirebase[0].value.toString())

                startLocal(true)


            } else {
                Log.d("HCS_FIREBASE_ERROR", "Data = null")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w("HCS_FIREBASE_ERROR", "Failed to read value.", error.toException())
        }
    }

override fun getDataList(): LiveData<List<Data>> {
    return Transformations.map(dataDao.getValueList()){
        it.map{
            mapper.mapDataToEntity(it,listDescription)
        }
    }
}

    override fun getDataConnect(): MutableState<DataConnect> = _dataConnect

    override fun loadData(connectSetting:ConnectSetting) {

        _connectSetting = connectSetting
        _dataConnect.value.ssidConnect = ""//обнуляем сеть

        //Log.d("HCS_fromMainViewModel","Server_Mode = ${_connectSetting.serverMode}, " +
        //        "Ssid = ${_connectSetting.ssid}")

        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        application.registerReceiver(wifiScanReceiver, intentFilter)

    }

    override fun closeConnect() {
        application.unregisterReceiver(wifiScanReceiver)
    }

    override fun getSsidList():MutableList<String>{
        return wifiManager.scanResults.map { it.SSID.toString() } as MutableList<String>
    }

    override fun putControl(controlMode:Int) {
        workManager.enqueueUniqueWork(
            ControlDataWorker.NAME_WORKER_CONTROL,
            ExistingWorkPolicy.REPLACE,
            ControlDataWorker.makeRequestOneTime(controlMode))
    }

    private fun startLoad(ssidFromWiFi:String){

        val ssidFromParameters = "\"${_connectSetting.ssid}\""

        val dataConnect = DataConnect(ssidFromWiFi)

         when{
                (ssidFromWiFi == ssidFromParameters)&&_connectSetting.serverMode -> {
                    dataConnect.modeConnect = ModeConnect.SERVER
                }
                (ssidFromWiFi == ssidFromParameters)&&!_connectSetting.serverMode -> {
                    dataConnect.modeConnect = ModeConnect.LOCAL
//                    val status = workManager.getWorkInfosByTag(RefreshDataWorker.NAME_PERIODIC).get()
//                    if (status.isEmpty()) {
//                        Log.d("HCS_BroadcastReceiver","state work = pusto")
//                    }else{
//                        status[0].state.name
//                        Log.d("HCS_BroadcastReceiver","state work = ${status.toString()}")
//                    }
                }
                (ssidFromWiFi != ssidFromParameters)&&!_connectSetting.serverMode -> {
                    dataConnect.modeConnect = ModeConnect.REMOTE
                }
                else -> dataConnect.modeConnect = ModeConnect.STOP
            }

        Log.d("HCS_BroadcastReceiver", "ssid = ${dataConnect.ssidConnect}, mode - ${dataConnect.modeConnect.name}")

        _dataConnect.value = dataConnect
        createWorker()
    }



    private fun createWorker(){

        myRef.removeEventListener(valueEventListener)

        when (_dataConnect.value.modeConnect) {
            ModeConnect.SERVER -> {
                workManager.cancelUniqueWork(RefreshDataWorker.NAME_ONE_TIME)

                workManager.enqueueUniquePeriodicWork(
                    RefreshDataWorker.NAME_PERIODIC,
                    ExistingPeriodicWorkPolicy.KEEP,
                    RefreshDataWorker.makeRequestPeriodic(_connectSetting.serverMode,false)
                )
                //Log.d("HCS_WorkManager","Mode.SERVER - loadDataPeriodic")

            }
            ModeConnect.LOCAL -> {

                startLocal(false)
                //Log.d("HCS_WorkManager","Mode.LOCAL - loadDataOneTime")

            }
            ModeConnect.REMOTE -> {
                workManager.cancelUniqueWork(RefreshDataWorker.NAME_ONE_TIME)
                workManager.cancelUniqueWork(RefreshDataWorker.NAME_PERIODIC)
                myRef.addValueEventListener(valueEventListener)
                //Log.d("HCS_WorkManager","Mode.REMOTE - loadDataOneTime")

            }
            ModeConnect.STOP -> {
                workManager.cancelUniqueWork(RefreshDataWorker.NAME_ONE_TIME)
                workManager.cancelUniqueWork(RefreshDataWorker.NAME_PERIODIC)
                //Log.d("HCS_WorkManager","Mode.STOP")
            }
        }


    }

    private fun startLocal(remoteMode:Boolean){
            workManager.cancelUniqueWork(RefreshDataWorker.NAME_PERIODIC)
            workManager.enqueueUniqueWork(
                RefreshDataWorker.NAME_ONE_TIME,
                ExistingWorkPolicy.REPLACE,
                RefreshDataWorker.makeRequestOneTime(_connectSetting.serverMode,remoteMode)
            )

    }

    companion object{
        const val FIREBASE_URL =
            "https://homesystemcontrolv01-default-rtdb.asia-southeast1.firebasedatabase.app"
        const val FIREBASE_PATH = "data"
    }


}
