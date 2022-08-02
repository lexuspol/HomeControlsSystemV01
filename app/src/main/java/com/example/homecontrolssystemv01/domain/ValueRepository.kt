package com.example.homecontrolssystemv01.domain

import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import com.example.homecontrolssystemv01.data.ConnectSetting
import com.example.homecontrolssystemv01.domain.model.Data
import com.example.homecontrolssystemv01.domain.model.DataConnect


interface DataRepository {

   fun getDataList (): LiveData<List<Data>>

   fun getDataConnect(): MutableState<DataConnect>

   fun loadData(connectSetting: ConnectSetting)

   fun closeConnect()

   fun getSsidList():MutableList<String>

   fun putControl(controlMode:Int)


}