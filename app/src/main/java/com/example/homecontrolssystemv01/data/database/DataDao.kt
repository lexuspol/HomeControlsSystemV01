package com.example.homecontrolssystemv01.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface DataDao {
    @Query("SELECT * FROM network_data_list")
    fun getValueList(): LiveData<List<DataDbModel>>

    @Query("SELECT * FROM data_setting_list")
    fun getSettingList(): LiveData<List<DataSettingDbModel>>

    @Query("SELECT * FROM data_setting_list")
    fun getSettingListFlow(): Flow<List<DataSettingDbModel>>

    @Query("SELECT * FROM message_list")
    fun getMessageList(): LiveData<List<MessageDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertValue(value: List<DataDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataSetting(dataSetting: DataSettingDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageList(messageList: List<MessageDbModel>)

    @Query("DELETE FROM message_list")
    suspend fun deleteAllMessage()

    @Query("DELETE FROM message_list WHERE time=:time")
    suspend fun deleteMessage(time:Long)


}