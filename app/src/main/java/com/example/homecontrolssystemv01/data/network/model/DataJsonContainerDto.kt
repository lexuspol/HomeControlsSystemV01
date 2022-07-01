package com.example.homecontrolssystemv01.data.network.model

import com.google.gson.JsonArray
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DataJsonContainerDto(
    @SerializedName("master")
    @Expose
    val json: JsonArray? = null
)
