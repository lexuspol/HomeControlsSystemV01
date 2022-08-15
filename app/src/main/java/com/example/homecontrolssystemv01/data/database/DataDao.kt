package com.example.homecontrolssystemv01.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface DataDao {
    @Query("SELECT * FROM network_data_list")
    fun getValueList(): LiveData<List<DataDbModel>>

    @Query("SELECT * FROM data_setting_list")
    fun getSettingList(): LiveData<List<DataSettingDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertValue(value: List<DataDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataSetting(dataSetting: DataSettingDbModel)
}