package com.example.homecontrolssystemv01.presentation

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.homecontrolssystemv01.data.repository.DataRepositoryImpl
import com.example.homecontrolssystemv01.domain.*


class MainViewModel(application: Application): AndroidViewModel(application)
{

    private val repository = DataRepositoryImpl(application)
    private val loadDataUseCase = LoadDataUseCase(repository)
    private val getDataList = GetDataListUseCase(repository)
    private val closeConnect = CloseConnectUseCase(repository)
    private val getssidList = GetListSsidUseCase(repository)

    val parametrs = Parameters()

    val sharedPref = application.getSharedPreferences("myPref", Context.MODE_PRIVATE)


    fun setParam(indexPref:Int, value:String){
        Log.d("HCS_ViewModel", "valPref = $indexPref, value = $value")
        when(indexPref){
            1 -> parametrs.ssidSet = value
            2 -> parametrs.mode = value
        }

    }

    fun savePref(){
        with(sharedPref.edit()){
            putString("ssid",parametrs.ssidSet)
            putString("mode",parametrs.mode)
            apply()
        }
        loadDataUseCase(parametrs)
    }

fun readPref(){
    parametrs.ssidSet = sharedPref.getString("ssid","").toString()
    parametrs.mode = sharedPref.getString("mode",Mode.NO_MODE.name).toString()
}



    fun getData():List<Data>{
        return getDataList()
    }

    fun getSsid():MutableList<String>{
        return getssidList()
    }

init {
    readPref()
    loadDataUseCase(parametrs)

}

    override fun onCleared() {
        super.onCleared()
        closeConnect()
    }

}