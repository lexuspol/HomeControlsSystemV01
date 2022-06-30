package com.example.homecontrolssystemv01.data.mapper



import com.example.homecontrolssystemv01.data.DataDbModel
import com.example.homecontrolssystemv01.data.network.model.DataDto
import com.example.homecontrolssystemv01.data.network.model.DataJsonContainerDto
import com.example.homecontrolssystemv01.domain.Data
import com.google.gson.Gson


class DataMapper {

    fun mapJsonContainerToListValue(jsonContainer: DataJsonContainerDto): List<DataDto> {
        val result = mutableListOf<DataDto>()
        val jsonArray = jsonContainer.json ?: return result
        for (json in jsonArray) {
            val data = Gson().fromJson(
                json,
                DataDto::class.java
            )
            result.add(data)
        }
        return result
    }

    fun valueDtoToDbModel(dataDto:DataDto) = DataDbModel(
        id = dataDto.id,
        value = dataDto.value,
        name = dataDto.name
    )


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