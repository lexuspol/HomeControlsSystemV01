package com.example.homecontrolssystemv01.data.repository

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.util.Log
import androidx.work.*
import androidx.work.WorkManager
import com.example.homecontrolssystemv01.data.DataList
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.workers.RefreshDataWorker
import com.example.homecontrolssystemv01.domain.Data
import com.example.homecontrolssystemv01.domain.DataRepository
import com.example.homecontrolssystemv01.domain.Mode
import com.example.homecontrolssystemv01.domain.Parameters

class DataRepositoryImpl (
    private val application: Application
        ): DataRepository {

    private var wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val workManager = WorkManager.getInstance(application)
    private  val mapper = DataMapper()
    private val intentFilter = IntentFilter()
    private var _parameters = Parameters()

    private val wifiScanReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val ssidFromState = loadSSIDfromState()
            val ssidFromWiFi = wifiManager.connectionInfo.ssid

            if (ssidFromState == ssidFromWiFi){
                Log.d("HCS_BroadcastReceiver",ssidFromWiFi)
            } else{
                setSSIDtoSate(ssidFromWiFi)
                startLoad()
            }
        }
    }


    override fun getDataList(): List<Data>{
        return DataList.movieListResponse.map {
            mapper.mapDataToEntity(it)
        }
    }

    override fun loadData(parameters: Parameters) {

        _parameters = parameters

        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        application.registerReceiver(wifiScanReceiver, intentFilter)

        //проверить без этих методов
        //setSSIDtoSate(wifiManager.connectionInfo.ssid)
        //startLoad()

    }

    override fun closeConnect() {
        application.unregisterReceiver(wifiScanReceiver)
    }

    fun startLoad(){

        val ssidFromState = loadSSIDfromState()
        val ssidFromParameters = "\"${_parameters.ssidSet}\""

        if (ssidFromState == ssidFromParameters) {
            createWorker(_parameters)
        } else {
            loadFirebase()
        }

    }

    fun setSSIDtoSate(ssid:String){
        DataList.ssidState.value = ssid
    }

    fun loadSSIDfromState():String{
        return DataList.ssidState.value
    }

    private fun loadFirebase() {
        workManager.cancelUniqueWork(RefreshDataWorker.NAME_ONE_TIME)
        workManager.cancelUniqueWork(RefreshDataWorker.NAME_PERIODIC)
        Log.d("HCS_WorkManager","lode Firebase")
    }

    private fun createWorker(parameters: Parameters){

        when (parameters.mode) {
            Mode.SERVER -> {
                workManager.cancelUniqueWork(RefreshDataWorker.NAME_ONE_TIME)
                workManager.enqueueUniquePeriodicWork(
                    RefreshDataWorker.NAME_PERIODIC,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    RefreshDataWorker.makeRequestPeriodic(parameters)
                )
                Log.d("HCS_WorkManager","Mode.SERVER - loadDataPeriodic")
            }

            Mode.CLIENT -> {
                workManager.cancelUniqueWork(RefreshDataWorker.NAME_PERIODIC)
                workManager.enqueueUniqueWork(
                    RefreshDataWorker.NAME_ONE_TIME,
                    ExistingWorkPolicy.REPLACE,
                    RefreshDataWorker.makeRequestOneTime(parameters)
                )
                Log.d("HCS_WorkManager","Mode.CLIENT - loadDataOneTime")
            }
            Mode.NO_MODE->{
                Log.d("HCS_WorkManager","Mode.NO_MODE")
            }
        }

    }








}
