package com.example.homecontrolssystemv01.domain.model

import com.example.homecontrolssystemv01.domain.model.message.ModeConnect

data class ConnectInfo(
    var ssidConnect:String = "",
    var modeConnect: ModeConnect = ModeConnect.STOP,
    //val ssidPref:String = "ssidPref"
)




