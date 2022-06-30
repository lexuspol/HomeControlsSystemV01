package com.example.homecontrolssystemv01.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.homecontrolssystemv01.domain.Data
import com.example.homecontrolssystemv01.domain.Parameters

object DataList {

    var movieListResponse:List<DataDbModel> by mutableStateOf(listOf())
    var parameters = mutableStateOf(Parameters())
    var ssidState = mutableStateOf("")

}