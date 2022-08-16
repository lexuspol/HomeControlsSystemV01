package com.example.homecontrolssystemv01.domain

import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import com.example.homecontrolssystemv01.domain.model.*


interface DataRepository {

   fun getDataList (): LiveData<List<Data>>

   fun getDataConnect(): MutableState<ConnectInfo>

   fun loadData(connectSetting: ConnectSetting)

   fun closeConnect()

   fun getSsidList():MutableList<String>

   fun putControl(controlInfo: ControlInfo)

   fun putDataSetting(dataSetting:DataSetting)
   fun getDataSetting():LiveData<List<DataSetting>>

   fun getMessageList():LiveData<List<Message>>

   suspend fun deleteMessage(time:Long)


}