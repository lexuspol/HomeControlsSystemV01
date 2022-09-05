package com.example.homecontrolssystemv01.data.repository

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.work.*
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.data.database.AppDatabase
import com.example.homecontrolssystemv01.data.database.DataDbModel
import com.example.homecontrolssystemv01.data.database.MessageDbModel
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.data.workers.ControlDataWorker
import com.example.homecontrolssystemv01.data.workers.PeriodicDataWorker
import com.example.homecontrolssystemv01.data.workers.RefreshDataWorker
import com.example.homecontrolssystemv01.domain.DataRepository
import com.example.homecontrolssystemv01.domain.model.*
import com.example.homecontrolssystemv01.domain.model.Data
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*


class MainRepositoryImpl (private val application: Application): DataRepository {

    private var wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val workManager = WorkManager.getInstance(application)

    private val dataDao = AppDatabase.getInstance(application).dataDao()
   // private val dataSettingDao = AppDatabase.getInstance(application).dataSetting()

    private  val mapper = DataMapper()
    private val intentFilter = IntentFilter()

    private val listDescription = application.resources.getStringArray(R.array.data)

    private var _connectSetting = ConnectSetting()

    //
    var _connectInfo:MutableState<ConnectInfo> = mutableStateOf(ConnectInfo())

    //запускаем бродкаст, он следит за состоянием сети, если сеть изменилась, то вызывается метод
    private val wifiScanReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val ssidFromWiFi = wifiManager.connectionInfo.ssid  // метод устарел, но другой очень муторный
            if (_connectInfo.value.ssidConnect == ssidFromWiFi){                                    //!!!!
                //Log.d("HCS_BroadcastReceiver","$ssidFromWiFi double")
            } else{

                //startLoad(ssidFromWiFi)
                selectDataSource(ssidFromWiFi)
            }
        }
    }

    private val myRef = Firebase.database(FIREBASE_URL).getReference(FIREBASE_PATH)

    //создаем слушателя для Firebase, в другом месте сложно, так как запись в базу происходит в карутине
    //запускаем слушателя в loadData
    private val valueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            val dataFirebase = snapshot.getValue<List<DataDbModel>>()

            if (dataFirebase != null) {
               // Log.d("HCS_FIREBASE", dataFirebase[0].value.toString())

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


    //////////////////////////////////





//    val request: NetworkRequest = NetworkRequest.Builder()
//        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
//        .build()
//
//    val connectivityManager: ConnectivityManager =
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            application.getSystemService(ConnectivityManager::class.java)
//        } else {
//            TODO("VERSION.SDK_INT < M")
//        }
//
//    val networkCallback = object : ConnectivityManager.NetworkCallback() {
//
//        override fun onAvailable(network: Network) {
//            super.onAvailable(network)
//        }
//
//        override fun onCapabilitiesChanged(
//            network: Network,
//            networkCapabilities: NetworkCapabilities
//        ) {
//            val wifiInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//
//                if (networkCapabilities.transportInfo != null) {
//                    networkCapabilities.transportInfo as WifiInfo
//                }else{
//                    Log.d("HCS_connectivity","wifiInfo = null")
//                    null
//                }
//
//            } else {
//                TODO("VERSION.SDK_INT < Q")
//            }
//
//            val ssid = wifiInfo?.ssid
//
//            if (ssid != null) {
//
//                if (_connectInfo.value.ssidConnect == wifiInfo.ssid){                                    //!!!!
//                    Log.d("HCS_BroadcastReceiver","${wifiInfo.ssid} double")
//                } else{
//                    //startLoad(ssidFromWiFi)
//                    selectDataSource(wifiInfo.ssid)
//                }
//
//
//            }else{
//                Log.d("HCS_connectivity","ssid = null")
//            }
//
//
//
//
//        }
//    }





    /////////////////////////////////////////////////




override fun getDataList(): LiveData<List<Data>> {

    return Transformations.map(dataDao.getValueList()) { list ->
        list.map {
            mapper.mapDataToEntity(it, listDescription)
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

    override suspend fun putMessageList(listMessage: List<Message>) {
//        Log.d("HCS_MainRepositoryImpl","putMessageList")
//        //dataDao.insertMessageList(listMessage.map { mapper.mapEntityToMessage(it) })
//       val info =  workManager.getWorkInfosForUniqueWork(ControlDataWorker.NAME_WORKER_CONTROL).await()
//        info.forEach {
//            Log.d("HCS_MainRepositoryImpl","Worker ${it.id} - state ${it.state.name}")
//        }
    }

    //узнать про исключения room
    override suspend fun deleteMessage(time: Long) {
        if (time==0L){
            dataDao.deleteAllMessage()

            dataDao.insertMessage(
                MessageDbModel(
                    Date().time,
                    0,
                    0,
                    "Все сообщения удалены")
            )

        }else{
            dataDao.deleteMessage(time)
        }

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
        selectDataSource(wifiManager.connectionInfo.ssid)

        //dataDao.deleteMessage()



       // _connectInfo.value.ssidConnect = ""//обнуляем сеть

        //Log.d("HCS_fromMainViewModel","Server_Mode = ${_connectSetting.serverMode}, " +
        //        "Ssid = ${_connectSetting.ssid}")

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
       //     startConnectivity()
     //   }else{
            //intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            //application.registerReceiver(wifiScanReceiver, intentFilter)
     //   }
    }

//    fun startConnectivity(){
//
//
//
//        //connectivityManager.requestNetwork(request, networkCallback); // For request
//        connectivityManager.registerNetworkCallback(request, networkCallback); // For listen
//        Log.w("HCS_connectivity", "registerNetworkCallback" )
//
//    }

    override fun closeConnect() {
        application.unregisterReceiver(wifiScanReceiver)
    }

    override fun getSsidList():MutableList<String>{
        return wifiManager.scanResults.map { it.SSID.toString() } as MutableList<String>
    }

    override fun putControl(controlInfo: ControlInfo) {

        if (_connectInfo.value.modeConnect == ModeConnect.LOCAL){

            workManager.enqueueUniqueWork(
                ControlDataWorker.NAME_WORKER_CONTROL,
                ExistingWorkPolicy.REPLACE,
                ControlDataWorker.makeRequestOneTime(controlInfo))
        }else{
            Log.d("HCS_MainRepositoryImpl","CONTROL - NOT, MODE - ${_connectInfo.value.modeConnect}")
        }
    }

    override suspend fun putDataSetting(dataSetting:DataSetting) {

       dataDao.insertDataSetting(mapper.settingToDbModel(dataSetting))



//        workManager.enqueueUniqueWork(
//            SettingDataWorker.NAME_WORKER_SETTING,
//            ExistingWorkPolicy.REPLACE,
//            SettingDataWorker.makeRequestOneTime(dataSetting)
//        )
    }


    private fun selectDataSource(ssidFromReceiver:String){

        if (ssidFromReceiver == _connectSetting.ssid){
            _connectInfo.value = ConnectInfo(ssidFromReceiver,ModeConnect.LOCAL)
            myRef.removeEventListener(valueEventListener)
            startLocal(false)


        }else{
            _connectInfo.value = ConnectInfo(ssidFromReceiver,ModeConnect.REMOTE)
            myRef.addValueEventListener(valueEventListener)
        }

    }



//    private fun startLoad(ssidFromWiFi:String){
//
//        //val ssidFromParameters = "\"${_connectSetting.ssid}\""
//        val ssidFromParameters = _connectSetting.ssid
//
//        val connectInfo = ConnectInfo(ssidFromWiFi)
//
//         when{
//                (ssidFromWiFi == ssidFromParameters)&&_connectSetting.serverMode -> {
//                    connectInfo.modeConnect = ModeConnect.SERVER
//                }
//                (ssidFromWiFi == ssidFromParameters)&&!_connectSetting.serverMode -> {
//                    connectInfo.modeConnect = ModeConnect.LOCAL
////                    val status = workManager.getWorkInfosByTag(RefreshDataWorker.NAME_PERIODIC).get()
////                    if (status.isEmpty()) {
////                        Log.d("HCS_BroadcastReceiver","state work = pusto")
////                    }else{
////                        status[0].state.name
////                        Log.d("HCS_BroadcastReceiver","state work = ${status.toString()}")
////                    }
//                }
//                (ssidFromWiFi != ssidFromParameters)&&!_connectSetting.serverMode -> {
//                    connectInfo.modeConnect = ModeConnect.REMOTE
//                }
//                else -> connectInfo.modeConnect = ModeConnect.STOP
//            }
//
//        Log.d("HCS_BroadcastReceiver", "ssid = ${connectInfo.ssidConnect}, mode - ${connectInfo.modeConnect.name}")
//
//        _connectInfo.value = connectInfo
//        //createWorker()
//    }



//    private fun createWorker(){
//
//        myRef.removeEventListener(valueEventListener)
//
//        when (_connectInfo.value.modeConnect) {
//            ModeConnect.SERVER -> {
//                workManager.cancelUniqueWork(RefreshDataWorker.NAME_ONE_TIME)
//
//                workManager.enqueueUniquePeriodicWork(
//                    RefreshDataWorker.NAME_PERIODIC,
//                    ExistingPeriodicWorkPolicy.KEEP,
//                    RefreshDataWorker.makeRequestPeriodic(_connectSetting.serverMode,false)
//                )
//                //Log.d("HCS_WorkManager","Mode.SERVER - loadDataPeriodic")
//
//            }
//            ModeConnect.LOCAL -> {
//
//                startLocal(false)
//                //Log.d("HCS_WorkManager","Mode.LOCAL - loadDataOneTime")
//
//            }
//            ModeConnect.REMOTE -> {
//                workManager.cancelUniqueWork(RefreshDataWorker.NAME_ONE_TIME)
//                workManager.cancelUniqueWork(RefreshDataWorker.NAME_PERIODIC)
//                myRef.addValueEventListener(valueEventListener)
//                //Log.d("HCS_WorkManager","Mode.REMOTE - loadDataOneTime")
//
//            }
//            ModeConnect.STOP -> {
//                workManager.cancelUniqueWork(RefreshDataWorker.NAME_ONE_TIME)
//                workManager.cancelUniqueWork(RefreshDataWorker.NAME_PERIODIC)
//                //Log.d("HCS_WorkManager","Mode.STOP")
//            }
//        }
//
//
//    }

    private fun startLocal(remoteMode:Boolean){

        if (_connectSetting.serverMode && !remoteMode){
            workManager.enqueueUniquePeriodicWork(
                PeriodicDataWorker.NAME_PERIODIC,
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicDataWorker.makeRequestPeriodic()
            )
        }else{
            workManager.cancelUniqueWork(PeriodicDataWorker.NAME_PERIODIC)
        }

           // workManager.cancelUniqueWork(RefreshDataWorker.NAME_PERIODIC)
            workManager.enqueueUniqueWork(
                RefreshDataWorker.NAME_ONE_TIME,
                ExistingWorkPolicy.KEEP,
                RefreshDataWorker.makeRequestOneTime(_connectSetting.serverMode,remoteMode)
            )

    }



    companion object{
        const val FIREBASE_URL =
            "https://homesystemcontrolv01-default-rtdb.asia-southeast1.firebasedatabase.app"
        const val FIREBASE_PATH = "data"
    }


}
