package com.example.homecontrolssystemv01.presentation

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.homecontrolssystemv01.data.ConnectSetting
import com.example.homecontrolssystemv01.data.repository.DataRepositoryImpl
import com.example.homecontrolssystemv01.presentation.enums.KeySetting
import com.example.homecontrolssystemv01.domain.model.Data
import com.example.homecontrolssystemv01.domain.model.DataConnect
import com.example.homecontrolssystemv01.domain.useCase.*
import com.example.homecontrolssystemv01.presentation.enums.DataSetting


class MainViewModel(application: Application): AndroidViewModel(application) {

    private val repository = DataRepositoryImpl(application)
    private val loadData = LoadDataUseCase(repository)
    private val getDataList = GetDataListUseCase(repository)
    private val closeConnect = CloseConnectUseCase(repository)
    private val getSsidList = GetListSsidUseCase(repository)
    private val getDataConnect = GetDataConnectUseCase(repository)


    private var _dataSetting = DataSetting()

    private val sharedPref = application.getSharedPreferences("myPref", Context.MODE_PRIVATE)

    private fun createConnectSetting():ConnectSetting{
        return ConnectSetting(_dataSetting.ssid,_dataSetting.serverMode
        )
    }

    fun getDataListUI(): List<Data> {
        _isLoading.value = getDataList().isEmpty() // переделать
        return getDataList()
    }

    fun getDataSettingUI():DataSetting{return _dataSetting}

    fun getDataConnectUI():MutableState<DataConnect>{return getDataConnect()}

    fun setDataSetting(dataSetting: DataSetting){
        _dataSetting = dataSetting
        with(sharedPref.edit()) {
            putString(KEY_SSID,_dataSetting.ssid)
            putBoolean(KEY_MODE,_dataSetting.serverMode)
            apply()
        }
        loadData(createConnectSetting())
    }

    fun getSsidListForRadioButton():RadioButtonList{
        val ssidList = mutableListOf(DataSetting().ssid)
        ssidList.addAll(getSsidList())
        if (ssidList.indexOf(_dataSetting.ssid)==-1) {
            ssidList.add(_dataSetting.ssid)
        }
        return RadioButtonList(
            KeySetting.SSID_KEY,
            ssidList,
            ssidList.indexOf(_dataSetting.ssid))
    }

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _selectedTab: MutableState<Int> = mutableStateOf(0)
    val selectedTab: State<Int> get() = _selectedTab

    fun selectTab(@StringRes tab: Int) {_selectedTab.value = tab}

    init {
        readPref()
        loadData(createConnectSetting())
    }
    private fun readPref() {
        _dataSetting.ssid = sharedPref.getString(KEY_SSID, DataSetting().ssid).toString()
        _dataSetting.serverMode = sharedPref.getBoolean(KEY_MODE,false)
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