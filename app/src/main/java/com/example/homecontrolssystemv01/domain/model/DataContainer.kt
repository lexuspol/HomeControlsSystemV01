package com.example.homecontrolssystemv01.domain.model

import com.example.homecontrolssystemv01.domain.model.setting.DataSetting

data class DataContainer(
    val id:Int,
    val dataModel:DataModel,
    val setting: DataSetting

)
