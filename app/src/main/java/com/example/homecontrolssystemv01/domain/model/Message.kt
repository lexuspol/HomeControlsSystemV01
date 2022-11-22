package com.example.homecontrolssystemv01.domain.model

import com.example.homecontrolssystemv01.domain.enum.MessageType

data class Message(

    val time:Long, //Date().time // -1 - update data
    val id:Int = 0,
    val type:Int = MessageType.SYSTEM.int,

    val description:String = "",
    val visible:Boolean = true


)
