package com.example.homecontrolssystemv01.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_setting_list")//имя таблицы
data class DataSettingDbModel(
    @PrimaryKey
    val id:Int=0,
    val visible:Boolean=true,
    val limitMode:Boolean = false,
    val limitMax:Float = 0F,
    val limitMin:Float = 0F,
    val setCounter:Long = 0L

)
