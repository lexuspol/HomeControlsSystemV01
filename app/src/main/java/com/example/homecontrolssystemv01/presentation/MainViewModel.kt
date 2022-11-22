package com.example.homecontrolssystemv01.presentation

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.data.repository.MainRepositoryImpl
import com.example.homecontrolssystemv01.domain.BatteryMonitor
import com.example.homecontrolssystemv01.domain.enum.MessageType
import com.example.homecontrolssystemv01.domain.model.*
import com.example.homecontrolssystemv01.domain.model.setting.ConnectSetting
import com.example.homecontrolssystemv01.domain.model.setting.DataSetting
import com.example.homecontrolssystemv01.domain.model.setting.SystemSetting
import com.example.homecontrolssystemv01.domain.useCase.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.*


class MainViewModel(application: Application): AndroidViewModel(application) {



    private val aplic = application

    private val repository = MainRepositoryImpl(aplic)
    private val loadData = LoadDataUseCase(repository)

    private val getDataList = GetDataListUseCase(repository)
    private val getMessageList = GetMessageListUseCase(repository)

    private val deleteMessage = DeleteMessageUseCase(repository)
    private val deleteData = DeleteDataUseCase(repository)
    private val putMessageList = PutMessageUseCase(repository)

    private val closeConnect = CloseConnectUseCase(repository)
    private val getConnectInfo = GetConnectInfoUseCase(repository)
    private val putControl = PutControlUseCase(repository)
    private val putDataSetting = PutSettingUseCase(repository)
    private val getDataSetting = GetSettingListUseCase(repository)

    private val getBatteryInfo = BatteryMonitor(aplic)

    private var _connectSetting = ConnectSetting()
    private var _systemSetting = SystemSetting()

    private val sharedPref = aplic.getSharedPreferences("myPref", Context.MODE_PRIVATE)


    private fun createConnectSetting(): ConnectSetting {
        return ConnectSetting(
            _connectSetting.ssid,
            _connectSetting.serverMode,
            _connectSetting.cycleMode
        )
    }

    fun putControlUI(controlInfo: ControlInfo){
        putControl(controlInfo)
    }

    fun putDataSettingUI(dataSetting: DataSetting){

        viewModelScope.launch {
            putDataSetting(dataSetting)
        }

    }

    fun resetCycleMode(){
        closeConnect()
    }

    fun loadDataUI(){
        loadData(createConnectSetting())
        putMessage(
            Message(Date().time,
                DataID.completeUpdate.id,
                MessageType.SYSTEM.int,
                DataID.completeUpdate.name)
        )
    }





    fun getDataSettingUI():LiveData<List<DataSetting>> = getDataSetting()

    fun getDataListUI(): LiveData<List<DataModel>> {
        return getDataList()
    }
    fun getMessageListUI(): LiveData<List<Message>> {

        val messageList = getMessageList()



       // Log.d("HCS_getMessageListUI","get")

        //putMessageListUI(listOf())//зачем

        return messageList

    }

    fun deleteMessageUI(id:Int){

        viewModelScope.launch {
            deleteMessage(id)
        }

    }

    fun deleteDataUI(id:Int){

        viewModelScope.launch {
            deleteData(id)
        }

    }


    private fun putMessage(message:Message){

       // Log.d("HCS_putMessageListUI","${list.isEmpty()}")
        viewModelScope.launch {
            putMessageList(message)
        }
    }

    fun getConnectSettingUI(): ConnectSetting = _connectSetting

    fun getSystemSettingUI(): SystemSetting = _systemSetting

    fun getConnectInfoUI():MutableState<ConnectInfo> = getConnectInfo()

    fun getBatteryInfoUI():String = getBatteryInfo.getBatteryPct().toString()

    fun setConnectSetting(connectSetting: ConnectSetting){
        _connectSetting = connectSetting
        with(sharedPref.edit()) {
            putString(KEY_SSID,_connectSetting.ssid)
            putBoolean(KEY_MODE_PERIODIC,_connectSetting.serverMode)
            putBoolean(KEY_MODE_CYCLE,_connectSetting.cycleMode)
            apply()
        }
        loadData(createConnectSetting())
    }

    fun setSystemSetting(systemSetting: SystemSetting){
        _systemSetting = systemSetting
        with(sharedPref.edit()) {
            putBoolean(KEY_SHOW_DETAILS,_systemSetting.showDetails)
            apply()
        }
    }


    private val _selectedTab: MutableState<Int> = mutableStateOf(0)
    val selectedTab: State<Int> get() = _selectedTab

    fun selectTab(@StringRes tab: Int) {_selectedTab.value = tab}

    init {
        readPref()
        //loadData(createConnectSetting())
    }
    private fun readPref() {
        _connectSetting.ssid = sharedPref.getString(KEY_SSID, ConnectSetting().ssid).toString()
        _connectSetting.serverMode = sharedPref.getBoolean(KEY_MODE_PERIODIC,false)
        _systemSetting.showDetails = sharedPref.getBoolean(KEY_SHOW_DETAILS,false)
        _connectSetting.cycleMode = sharedPref.getBoolean(KEY_MODE_CYCLE,false)

        val auth = Firebase.auth
         if (auth.currentUser ==null) {
             Log.d("HCS_MainViewModel","Firebase.auth == null")
             auth.signInWithEmailAndPassword("geroi@tut.by", "Qwerty12")
         }
    }

    override fun onCleared() {
        super.onCleared()
        //if (_connectSetting.cycleMode) {
          //  closeConnect()
       // }
    }

    companion object{
        const val KEY_SSID = "SSID"
        const val KEY_MODE_PERIODIC = "MODE_PERIODIC"
        const val KEY_MODE_CYCLE = "MODE_CYCLE"
        const val KEY_SHOW_DETAILS = "SHOW_DETAILS"
    }


}