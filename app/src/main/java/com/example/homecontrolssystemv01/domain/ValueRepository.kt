package com.example.homecontrolssystemv01.domain

import androidx.compose.runtime.MutableState
import com.example.homecontrolssystemv01.data.ConnectSetting
import com.example.homecontrolssystemv01.domain.model.Data
import com.example.homecontrolssystemv01.domain.model.DataConnect


interface DataRepository {

   fun getDataList (): List<Data>

   fun getDataConnect(): MutableState<DataConnect>

   fun loadData(connectSetting: ConnectSetting)

   fun closeConnect()

   fun getSsid():MutableState<String>

   fun getSsidList():MutableList<String>


}