package com.example.homecontrolssystemv01.domain

import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import com.example.homecontrolssystemv01.domain.model.*
import com.example.homecontrolssystemv01.domain.model.data.DataModel
import com.example.homecontrolssystemv01.domain.model.logging.LoggingValue
import com.example.homecontrolssystemv01.domain.model.logging.LoggingID
import com.example.homecontrolssystemv01.domain.model.message.Message
import com.example.homecontrolssystemv01.domain.model.setting.ConnectSetting
import com.example.homecontrolssystemv01.domain.model.setting.DataSetting


interface DataRepository {

   fun getDataList (): LiveData<List<DataModel>>

   fun getDataConnect(): MutableState<ConnectInfo>

   fun loadData(connectSetting: ConnectSetting)

   fun closeConnect()

   fun putControl(controlInfo: ControlInfo)

   fun getDataSetting():LiveData<List<DataSetting>>

   fun getMessageList():LiveData<List<Message>>

   suspend fun putDataSetting(dataSetting: DataSetting)
   suspend fun putMessage(message: Message)

   suspend fun deleteMessage(id:Int)

   suspend fun deleteData(id:Int)
   fun getLoggingValue(logKey:LoggingID): MutableState<LoggingValue>
   fun getLoggingIdList(): MutableState<List<LoggingID>>
   fun deleteLoggingValue(logKey:LoggingID)

}