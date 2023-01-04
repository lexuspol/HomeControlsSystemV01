package com.example.homecontrolssystemv01.domain.model.shop

import com.example.homecontrolssystemv01.DataID

data class ShopItem(

    var itemId:Int = UNDEFINED_ID,
    var itemName:String = " ",
    var groupId:Int = UNDEFINED_ID,
    var countString: String = " ",
    var enabled: Boolean = true

){

    companion object{

        const val UNDEFINED_ID = 0


    }



}
