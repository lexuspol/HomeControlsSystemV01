package com.example.homecontrolssystemv01.presentation

import com.example.homecontrolssystemv01.presentation.enums.KeySetting

data class RadioButtonList(

    val keySetting: KeySetting,
    val list:MutableList<String>,
    val index:Int

)
