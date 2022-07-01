package com.example.homecontrolssystemv01.domain


interface DataRepository {

   fun getDataList (): List<Data>

   fun loadData(parameters:Parameters)

   fun closeConnect()

   fun getSsidList():MutableList<String>


}