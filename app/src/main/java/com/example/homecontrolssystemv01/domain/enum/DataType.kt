package com.example.homecontrolssystemv01.domain.enum

enum class DataType(val dataTypeNumber: Int) {

    UNDEFINED(0),
    INT(1),
    REAL(2),
    DINT(3),
    TIME(4),//Server - UDInt - ms
    DTL(5),
    STRING(6),
    BOOL(7),
    WORD(8),

}