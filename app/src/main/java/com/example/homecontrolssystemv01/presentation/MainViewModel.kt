package com.example.homecontrolssystemv01.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.homecontrolssystemv01.data.repository.DataRepositoryImpl
import com.example.homecontrolssystemv01.domain.*


class MainViewModel(application: Application): AndroidViewModel(application)
{

    private val repository = DataRepositoryImpl(application)
    private val loadDataUseCase = LoadDataUseCase(repository)
    private val getDataList = GetDataListUseCase(repository)
    private val closeConnect = CloseConnectUseCase(repository)

    fun getData():List<Data>{
        return getDataList()
    }

init {
    loadDataUseCase(Parameters(Mode.CLIENT,"AlexWiFi"))
}

    override fun onCleared() {
        super.onCleared()
        closeConnect()
    }

}