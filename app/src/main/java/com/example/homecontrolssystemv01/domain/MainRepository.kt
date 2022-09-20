package com.example.homecontrolssystemv01.domain

import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import com.example.homecontrolssystemv01.domain.model.*


interface DataRepository {

   fun getDataList (): LiveData<List<DataModel>>

   fun getDataConnect(): MutableState<ConnectInfo>

   fun loadData(connectSetting: ConnectSetting)

   fun closeConnect()


   fun putControl(controlInfo: ControlInfo)


   fun getDataSetting():LiveData<List<DataSetting>>

   fun getMessageList():LiveData<List<Message>>

   suspend fun putDataSetting(dataSetting:DataSetting)
   suspend fun putMessage(message:Message)

   suspend fun deleteMessage(time:Long)


}