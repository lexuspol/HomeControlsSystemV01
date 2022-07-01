package com.example.homecontrolssystemv01.data.network

import com.example.homecontrolssystemv01.data.network.model.DataJsonContainerDto
import retrofit2.http.GET


interface ApiService {


    @GET("data.json")
    suspend fun getData(): DataJsonContainerDto


}