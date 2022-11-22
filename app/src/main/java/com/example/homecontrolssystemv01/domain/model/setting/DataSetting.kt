package com.example.homecontrolssystemv01.domain.model.setting

data class DataSetting(

    val id:Int=0,
    val description:String = "",
    val visible:Boolean = false,
    val limitMode:Boolean = false,
    val limitMax:Float = 0F,
    val limitMin:Float = 0F,
    var unit:String = ""

)
