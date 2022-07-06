package com.example.homecontrolssystemv01.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object DataList {

    var movieListResponse:List<DataDbModel> by mutableStateOf(listOf())
    var ssidState = mutableStateOf("")


}