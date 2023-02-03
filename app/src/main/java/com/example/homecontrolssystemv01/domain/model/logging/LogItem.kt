package com.example.homecontrolssystemv01.domain.model.logging

import com.example.homecontrolssystemv01.DataID

data class LogItem(

    //var itemId:Int = UNDEFINED_ID,
    //var itemName:String = " ",
    var value:List<Pair<Long,String>> = listOf()

){

    companion object{

        const val UNDEFINED_ID = 0


    }



}
