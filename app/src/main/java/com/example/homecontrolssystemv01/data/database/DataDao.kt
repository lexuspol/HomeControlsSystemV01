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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertValue(value: List<DataDbModel>)
}