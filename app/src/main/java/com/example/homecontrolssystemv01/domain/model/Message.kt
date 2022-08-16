package com.example.homecontrolssystemv01.domain.model

data class Message(

    val time:Long, //Date().time
    val type:Int = 0,//0 - статус, 1 - предупрежедние, 2 - авария
    val description:String = "",
    val visible:Boolean = true


)
