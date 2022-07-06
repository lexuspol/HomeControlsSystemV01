package com.example.homecontrolssystemv01.domain

import androidx.compose.runtime.MutableState
import com.example.homecontrolssystemv01.domain.model.Data


interface DataRepository {

   fun getDataList (): List<Data>

   fun loadData(mode: String,ssidSet:String)

   fun closeConnect()

   fun getSsid():MutableState<String>

   fun getSsidList():MutableList<String>


}