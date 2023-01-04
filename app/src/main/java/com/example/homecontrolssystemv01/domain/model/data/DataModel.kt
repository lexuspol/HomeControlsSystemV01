package com.example.homecontrolssystemv01.domain.model.data

data class DataModel(
val id:Int=0,
val value:String?="",
val name:String?="",
val type:Int=0,
var description:String = "",
var unit:String = "",
val listString: List<String> = listOf()
)
