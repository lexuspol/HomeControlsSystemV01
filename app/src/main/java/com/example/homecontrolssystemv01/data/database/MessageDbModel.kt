package com.example.homecontrolssystemv01.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_list")//имя таблицы
data class MessageDbModel(
    @PrimaryKey
    val time:Long,
    val id:Int = 0,
    val type:Int = 0,//0 - статус, 1 - предупрежедние, 2 - авария
    val description:String = "",
    val visible:Boolean = true
)
