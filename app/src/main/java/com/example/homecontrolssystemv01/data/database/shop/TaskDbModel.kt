package com.example.homecontrolssystemv01.data.database.shop

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_list")//имя таблицы
data class TaskDbModel(
    @PrimaryKey
    var itemId:Int = UNDEFINED_ID,
    var itemName:String = " ",
    var groupId:Int = UNDEFINED_ID,
    var details: String = " ",
    var enabled: Boolean = true

) {

    companion object {

        const val UNDEFINED_ID = 0


    }
}

