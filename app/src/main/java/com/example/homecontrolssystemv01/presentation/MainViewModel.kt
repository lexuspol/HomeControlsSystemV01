package com.example.homecontrolssystemv01.presentation

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.lifecycle.AndroidViewModel
import com.example.homecontrolssystemv01.data.repository.DataRepositoryImpl
import com.example.homecontrolssystemv01.presentation.enums.KeySetting
import com.example.homecontrolssystemv01.presentation.enums.Mode
import com.example.homecontrolssystemv01.domain.model.Data
import com.example.homecontrolssystemv01.domain.useCase.*


class MainViewModel(application: Application): AndroidViewModel(application) {

    private val repository = DataRepositoryImpl(application)
    private val loadDataUseCase = LoadDataUseCase(repository)
    private val getDataList = GetDataListUseCase(repository)
    private val closeConnect = CloseConnectUseCase(repository)
    private val getSsidList = GetListSsidUseCase(repository)
    private val getSsid = GetSsidUseCase(repository)

    private var _mode = Mode.STOP.name
    private var _ssid = "NO_WiFI"

    private val sharedPref = application.getSharedPreferences("myPref", Context.MODE_PRIVATE)


    fun setParam(key: KeySetting, value: String) {
        //Log.d("HCS_ViewModel", "valPref = $indexPref, value = $value")
        when (key) {
            KeySetting.SSID_KEY -> _ssid = value
            KeySetting.MODE_KEY -> _mode = value
        }

    }

    fun savePref() {
        with(sharedPref.edit()) {
            putString(KeySetting.SSID_KEY.name, _ssid)
            putString(KeySetting.MODE_KEY.name, _mode)
            apply()
        }
        loadDataUseCase(_mode, _ssid)
    }

    fun readPref() {
        _ssid = sharedPref.getString(KeySetting.SSID_KEY.name, "").toString()
        _mode = sharedPref.getString(KeySetting.MODE_KEY.name, Mode.STOP.name).toString()
    }


    fun getData(): List<Data> {
        return getDataList()
    }

//    fun getSsidList(): MutableList<String> {
//        val ssidListFromRepository = getSsidList()
//        if (ssidListFromRepository.indexOf(_ssid)==-1) {
//            ssidListFromRepository.add(_ssid)
//        }
//        var index=ssidListFromRepository.indexOf(_ssid)
//
//
//
//        return
//    }

    fun getSsidForRadioButton():RadioButtonList{
        val ssidList = mutableListOf("NO_WIFI")
        ssidList.addAll(getSsidList())
        if (ssidList.indexOf(_ssid)==-1) {
            ssidList.add(_ssid)
        }
        return RadioButtonList(
            KeySetting.SSID_KEY,
            ssidList,
            ssidList.indexOf(_ssid))
    }

    fun getMode():RadioButtonList{
        val modeList = mutableListOf<String>()
        Mode.values().map {
            modeList.add(it.name)
        }

        return RadioButtonList(
            KeySetting.MODE_KEY,
            modeList,
            if (modeList.indexOf(_mode) == -1) 0 else modeList.indexOf(_mode)
        )

    }

    fun getSsidFromText(): MutableState<String> {
        return getSsid()
    }


    init {
        readPref()
        loadDataUseCase(_mode, _ssid)

    }

    override fun onCleared() {
        super.onCleared()
        closeConnect()
    }


}