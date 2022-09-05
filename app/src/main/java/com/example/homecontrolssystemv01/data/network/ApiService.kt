package com.example.homecontrolssystemv01.data.network

import com.example.homecontrolssystemv01.data.network.model.DataJsonContainerDto
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {


    @GET("data.json")
    suspend fun getData(): DataJsonContainerDto

    @PUT("data.json?\"transmitDate\".buttonLightChild=1")
    suspend fun buttonLightChild(): DataJsonContainerDto

    @PUT("data.json?\"transmitDate\".buttonLightSleep=1")
    suspend fun buttonLightSleep(): DataJsonContainerDto

    @PUT("data.json")
    suspend fun setMeterElectricity(@Query("\"transmitDate\".meterElectricity")value:String): DataJsonContainerDto


}