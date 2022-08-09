package com.example.homecontrolssystemv01.domain.model



data class DataLimit(

    val id:Int,
    val typeAlarm: Boolean,
    var min: Float,
    var max: Float,
    var descriptorMin: String,
    var descriptorMax: String

)
