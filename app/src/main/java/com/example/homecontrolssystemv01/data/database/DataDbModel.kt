package com.example.homecontrolssystemv01.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "network_data_list")//имя таблицы
data class DataDbModel(
    @PrimaryKey (autoGenerate = true)
    val id:Int=0,
    val value:String?="",
    val name:String?="",
    val type:Int=0
)
