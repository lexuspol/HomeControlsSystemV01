package com.example.homecontrolssystemv01.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.homecontrolssystemv01.domain.model.DataConnect

object DataList {

    var movieListResponse:List<DataDbModel> by mutableStateOf(listOf())
    var ssidState = mutableStateOf("")
    var dataConnect:MutableState<DataConnect> = mutableStateOf(DataConnect())


}