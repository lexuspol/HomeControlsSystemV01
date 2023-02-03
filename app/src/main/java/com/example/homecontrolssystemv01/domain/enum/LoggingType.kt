package com.example.homecontrolssystemv01.domain.enum

enum class LoggingType(val separator:Char) {
    LOGGING_PERIODIC('-'),
    LOGGING_ONE_DAY('-'),
    UNDEFINED('-')
}