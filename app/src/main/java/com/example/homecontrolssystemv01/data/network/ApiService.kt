package com.example.homecontrolssystemv01.data.network

import com.example.homecontrolssystemv01.data.network.model.DataJsonContainerDto
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("data.json")
    suspend fun getData(): DataJsonContainerDto

    @PUT("data.json")
    suspend fun buttonLightChild(
        @Query("\"transmitDate\".buttonLightChild")value:String): DataJsonContainerDto

    @PUT("data.json")
    suspend fun buttonLightSleep(
        @Query("\"transmitDate\".buttonLightSleep")value:String): DataJsonContainerDto

    @PUT("data.json")
    suspend fun buttonLightCinema(
        @Query("\"transmitDate\".buttonLightCinema")value:String): DataJsonContainerDto

    @PUT("data.json")
    suspend fun buttonLightOutdoor(
        @Query("\"transmitDate\".buttonLightOutdoor")value:String): DataJsonContainerDto

    //@PUT("data.json?\"transmitDate\".buttonLightChild=1")
   // suspend fun buttonLightChild(): DataJsonContainerDto

   // @PUT("data.json?\"transmitDate\".buttonLightSleep=1")
   // suspend fun buttonLightSleep(): DataJsonContainerDto

   // @PUT("data.json?\"transmitDate\".buttonLightCinema=1")
   // suspend fun buttonLightCinema(): DataJsonContainerDto

    @PUT("data.json")
    suspend fun setMeterWater(
        @Query("\"transmitDate\".meterWater")value:String): DataJsonContainerDto

    @PUT("data.json")
    suspend fun setMeterElectricity(
        @Query("\"transmitDate\".meterElectricity")value:String): DataJsonContainerDto

    @PUT("data.json")
    suspend fun setBatteryPer(
        @Query("\"transmitDate\".batteryPer")value:Float): DataJsonContainerDto

    @PUT("data.json")
    suspend fun setMainDeviceName(
        @Query("\"transmitDate\".mainDeviceName")value:String): DataJsonContainerDto

    @PUT("data.json")
    suspend fun buttonWicketUnlock(
        @Query("\"transmitDate\".buttonWicketUnlock")value:String): DataJsonContainerDto

    @PUT("data.json")
    suspend fun buttonGateGarageSBS(
        @Query("\"transmitDate\".buttonGateGarageSBS")value:String): DataJsonContainerDto

    @PUT("data.json")
    suspend fun buttonGateSlidingSBS(
        @Query("\"transmitDate\".buttonGateSlidingSBS")value:String): DataJsonContainerDto

    @PUT("data.json")
    suspend fun buttonSoundOff(
        @Query("\"transmitDate\".buttonSoundOff")value:String): DataJsonContainerDto

    //@PUT("data.json?\"transmitDate\".buttonWicketUnlock=1")
    //suspend fun buttonWicketUnlock(): DataJsonContainerDto

    //@PUT("data.json?\"transmitDate\".buttonGateGarageSBS=1")
   // suspend fun buttonGateGarageSBS(): DataJsonContainerDto

   // @PUT("data.json?\"transmitDate\".buttonGateSlidingSBS=1")
   // suspend fun buttonGateSlidingSBS(): DataJsonContainerDto

}