package com.example.homecontrolssystemv01.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
//<!-- type: 1-BOOL, 2-INT, 3-Real, 4-String, 5-DTL, 6-Word -->
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

    @SerializedName("type")
    @Expose
    val type: Int

)
