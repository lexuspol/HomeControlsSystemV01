package com.example.homecontrolssystemv01.domain.model

data class DataSetting(

    val id:Int=0,
    val visible:Boolean = false,
    val limitMode:Boolean = false,
    val limitMax:Float = 0F,
    val limitMin:Float = 0F,
    val setCounter:Long = 0L,
    val controlMode:Int = 0
)
