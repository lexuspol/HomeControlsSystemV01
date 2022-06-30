package com.example.homecontrolssystemv01.data.mapper



import com.example.homecontrolssystemv01.data.DataDbModel
import com.example.homecontrolssystemv01.domain.Data


class DataMapper {

    fun mapDataToEntity(snapshot:DataDbModel) = Data(
        id = snapshot.id,
        value = if (snapshot.name == "lastTimeUpdate") convertDateServerToDateUI(snapshot.value) else snapshot.value,
        name = snapshot.name

    )

    private fun convertDateServerToDateUI(date:String?):String{
        if (date == null) return ""
        return "${date.substring(4,14)}  ${date.substring(15,20)}"
    }



}