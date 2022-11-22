package com.example.homecontrolssystemv01.domain.enum

enum class MessageType(val int:Int) {

    HIDDEN(-1),
    SYSTEM(0),
    WARNING(1),
    ALARM(2),
    INACTIVE(3)

}