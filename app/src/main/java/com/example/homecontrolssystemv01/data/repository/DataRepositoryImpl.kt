package com.example.homecontrolssystemv01.data.repository

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.work.*
import androidx.work.WorkManager
import com.example.homecontrolssystemv01.data.ConnectSetting
import com.example.homecontrolssystemv01.data.DataList
import com.example.homecontrolssystemv01.data.FirebaseFactory
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.network.ApiFactory
import com.example.homecontrolssystemv01.data.workers.ControlDataWorker
import com.example.homecontrolssystemv01.data.workers.RefreshDataWorker
import com.example.homecontrolssystemv01.domain.model.Data
import com.example.homecontrolssystemv01.domain.DataRepository
import com.example.homecontrolssystemv01.domain.model.DataConnect
import com.example.homecontrolssystemv01.domain.model.ModeConnect

class DataRepositoryImpl (private val application: Application): DataRepository {


    private var wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val workManager = WorkManager.getInstance(application)
    private  val mapper = DataMapper()
    private val intentFilter = IntentFilter()

    private var _connectSetting = ConnectSetting()

    private val wifiScanReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val ssidFromWiFi = wifiManager.connectionInfo.ssid

            if (DataList.dataConnect.value.ssidConnect == ssidFromWiFi){
                Log.d("HCS_BroadcastReceiver","$ssidFromWiFi double")
            } else{
                startLoad(ssidFromWiFi)
            }
        }
    }

    override fun getDataList(): List<Data>{

        //DataList.movieListResponse.add

        return DataList.movieListResponse.map {
            mapper.mapDataToEntity(it)
        }
    }

    override fun getDataConnect(): MutableState<DataConnect> {
        return DataList.dataConnect
    }

    override fun loadData(connectSetting:ConnectSetting) {



        _connectSetting = connectSetting

        Log.d("HCS_fromMainViewModel","Server_Mode = ${_connectSetting.serverMode}, " +
                "Ssid = ${_connectSetting.ssid}")

        DataList.dataConnect.value.ssidConnect = ""

        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        application.registerReceiver(wifiScanReceiver, intentFilter)

    }

    override fun closeConnect() {
        application.unregisterReceiver(wifiScanReceiver)

    }

    override fun getSsid(): MutableState<String> = DataList.ssidState

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
                    workManager.cancelUniqueWork(RefreshDataWorker.NAME_ONE_TIME)
                    FirebaseFactory.removeEventListener()
                    createWorker()
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


                    workManager.cancelUniqueWork(RefreshDataWorker.NAME_PERIODIC)
                    FirebaseFactory.removeEventListener()
                    createWorker()
                }
                (ssidFromWiFi != ssidFromParameters)&&!_connectSetting.serverMode -> {
                    dataConnect.modeConnect = ModeConnect.REMOTE
                    workManager.cancelUniqueWork(RefreshDataWorker.NAME_ONE_TIME)
                    workManager.cancelUniqueWork(RefreshDataWorker.NAME_PERIODIC)
                    loadFirebase()
                }
                else -> dataConnect.modeConnect = ModeConnect.STOP
            }

        Log.d("HCS_BroadcastReceiver", "ssid = ${dataConnect.ssidConnect}, mode - ${dataConnect.modeConnect.name}")

        DataList.dataConnect.value = dataConnect
    }

    private fun loadFirebase() {
        FirebaseFactory.createEventListener()
        Log.d("HCS_BroadcastReceiver","lode Firebase")
    }

    private fun createWorker(){

        if (_connectSetting.serverMode){
            workManager.cancelUniqueWork(RefreshDataWorker.NAME_ONE_TIME)
            workManager.enqueueUniquePeriodicWork(
                RefreshDataWorker.NAME_PERIODIC,
                ExistingPeriodicWorkPolicy.REPLACE,
                RefreshDataWorker.makeRequestPeriodic(_connectSetting.serverMode)
            )
            Log.d("HCS_WorkManager","Mode.SERVER - loadDataPeriodic")
        } else{
            workManager.cancelUniqueWork(RefreshDataWorker.NAME_PERIODIC)
            workManager.enqueueUniqueWork(
                RefreshDataWorker.NAME_ONE_TIME,
                ExistingWorkPolicy.REPLACE,
                RefreshDataWorker.makeRequestOneTime(_connectSetting.serverMode)
            )
            Log.d("HCS_WorkManager","Mode.CLIENT - loadDataOneTime")

        }
    }

}
