package com.example.homecontrolssystemv01.domain.model.setting

import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.domain.enum.LoggingType

data class LogSetting(
    var logId:Int = UNDEFINED_ID,
    var logKey:String = UNDEFINED_KEY,

    var type: LoggingType = LoggingType.UNDEFINED,
   // val separator:Char = CONST_SEPARATOR
){
    companion object{
        const val UNDEFINED_ID = 0
        const val UNDEFINED_KEY = ""
     //   const val CONST_SEPARATOR = '-'
    }
}
