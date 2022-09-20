package com.example.homecontrolssystemv01.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_list")//имя таблицы
data class MessageDbModel(
    val time:Long,
    @PrimaryKey
    val id:Int,
    val type:Int = 0,//0 - статус, 1 - предупрежедние, 2 - авария
    val description:String = "",
    val visible:Boolean = true
)
