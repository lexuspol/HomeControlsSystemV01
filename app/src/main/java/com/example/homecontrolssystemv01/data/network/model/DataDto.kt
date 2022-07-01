package com.example.homecontrolssystemv01.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DataDto(
    @SerializedName("ID")
    @Expose
    val id: Int,

    @SerializedName("value")
    @Expose
    val value: String?,

    @SerializedName("name")
    @Expose
    val name: String?,
)
