package com.example.homecontrolssystemv01.domain.enum

enum class LogKey(val type: LoggingType) {

    LOGGING_1(LoggingType.LOGGING_PERIODIC),
    LOGGING_2(LoggingType.LOGGING_PERIODIC),
    LOGGING_3(LoggingType.LOGGING_PERIODIC),
    LOGGING_LAST_DAY_1(LoggingType.LOGGING_ONE_DAY),
    LOGGING_LAST_DAY_2(LoggingType.LOGGING_ONE_DAY),
    LOGGING_LAST_DAY_3(LoggingType.LOGGING_ONE_DAY)


}