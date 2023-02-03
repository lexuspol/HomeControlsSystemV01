package com.example.homecontrolssystemv01.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_list")//имя таблицы
data class ShopDbModel(
    @PrimaryKey
    var itemId:Int = UNDEFINED_ID,
    var itemName:String = " ",
    var groupId:Int = UNDEFINED_ID,
    var countString: String = " ",
    var enabled: Boolean = true

) {

    companion object {

        const val UNDEFINED_ID = 0


    }
}

