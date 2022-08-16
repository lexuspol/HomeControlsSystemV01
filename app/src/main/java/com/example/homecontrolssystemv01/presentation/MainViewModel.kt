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
import com.example.homecontrolssystemv01.data.repository.MainRepositoryImpl
import com.example.homecontrolssystemv01.domain.BatteryMonitor
import com.example.homecontrolssystemv01.domain.model.*
import com.example.homecontrolssystemv01.domain.useCase.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class MainViewModel(application: Application): AndroidViewModel(application) {



    private val aplic = application

    private val repository = MainRepositoryImpl(aplic)
    private val loadData = LoadDataUseCase(repository)

    private val getDataList = GetDataListUseCase(repository)
    private val getMessageList = GetMessageListUseCase(repository)

    private val deleteMessage = DeleteMessageUseCase(repository)

    private val closeConnect = CloseConnectUseCase(repository)
    private val getSsidList = GetListSsidUseCase(repository)
    private val getConnectInfo = GetConnectInfoUseCase(repository)
    private val putControl = PutControlUseCase(repository)
    private val putDataSetting = PutSettingUseCase(repository)
    private val getDataSetting = GetSettingListUseCase(repository)

    private val getBatteryInfo = BatteryMonitor(aplic)

    private var _connectSetting = ConnectSetting()

    private val sharedPref = aplic.getSharedPreferences("myPref", Context.MODE_PRIVATE)

    private fun createConnectSetting(): ConnectSetting {
        return ConnectSetting(_connectSetting.ssid,_connectSetting.serverMode
        )
    }

    fun putControlUI(controlInfo: ControlInfo){
        putControl(controlInfo)
    }

    fun putDataSettingUI(dataSetting:DataSetting){
        putDataSetting(dataSetting)
    }

    fun loadDataUI(){
        loadData(createConnectSetting())
    }

    fun getDataSettingUI():LiveData<List<DataSetting>> = getDataSetting()

    fun getDataListUI(): LiveData<List<Data>> = getDataList()
    fun getMessageListUI(): LiveData<List<Message>> = getMessageList()

    fun deleteMessageUI(time:Long){

        viewModelScope.launch {
            deleteMessage(time)
        }

    }

    fun getConnectSettingUI(): ConnectSetting = _connectSetting

    fun getConnectInfoUI():MutableState<ConnectInfo> = getConnectInfo()

    fun getBatteryInfoUI():String = getBatteryInfo.getBatteryPct().toString()

    fun setDataSetting(connectSetting: ConnectSetting){
        _connectSetting = connectSetting
        with(sharedPref.edit()) {
            putString(KEY_SSID,_connectSetting.ssid)
            putBoolean(KEY_MODE,_connectSetting.serverMode)
            apply()
        }
        loadData(createConnectSetting())
    }

    fun getSsidListForRadioButton():RadioButtonList{
        val ssidList = mutableListOf(ConnectSetting().ssid)
        ssidList.addAll(getSsidList())
        if (ssidList.indexOf(_connectSetting.ssid)==-1) {
            ssidList.add(_connectSetting.ssid)
        }
        return RadioButtonList(
            //KeySetting.SSID_KEY,
            ssidList,
            ssidList.indexOf(_connectSetting.ssid))
    }

   // private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    //val isLoading: State<Boolean> get() = _isLoading

    private val _selectedTab: MutableState<Int> = mutableStateOf(0)
    val selectedTab: State<Int> get() = _selectedTab

    fun selectTab(@StringRes tab: Int) {_selectedTab.value = tab}

    init {
        readPref()
        loadData(createConnectSetting())
    }
    private fun readPref() {
        _connectSetting.ssid = sharedPref.getString(KEY_SSID, ConnectSetting().ssid).toString()
        _connectSetting.serverMode = sharedPref.getBoolean(KEY_MODE,false)

        val auth = Firebase.auth
         if (auth.currentUser ==null) {
             Log.d("HCS_MainViewModel","Firebase.auth == null")
             auth.signInWithEmailAndPassword("geroi@tut.by", "Qwerty12")
         }
    }

    override fun onCleared() {
        super.onCleared()
        closeConnect()
    }

    companion object{
        const val KEY_SSID = "SSID"
        const val KEY_MODE = "MODE"
    }


}