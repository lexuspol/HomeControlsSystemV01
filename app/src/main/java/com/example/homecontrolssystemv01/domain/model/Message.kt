package com.example.homecontrolssystemv01.domain.model

data class Message(

    val time:Long, //Date().time // -1 - update data
    val id:Int = 0,
    val type:Int = 0,
    //0 - системные, 1 - предупрежедние, 2 - авария
    // минус - скрытые

    val description:String = "",
    val visible:Boolean = true


)
