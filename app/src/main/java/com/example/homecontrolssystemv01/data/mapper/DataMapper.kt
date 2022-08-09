package com.example.homecontrolssystemv01.data.mapper



import com.example.homecontrolssystemv01.data.database.DataDbModel
import com.example.homecontrolssystemv01.data.network.model.DataDto
import com.example.homecontrolssystemv01.data.network.model.DataJsonContainerDto
import com.example.homecontrolssystemv01.domain.model.Data
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
        name = dataDto.name,
        type = dataDto.type
    )


        fun mapDataToEntity(dataDb: DataDbModel,listDescription:Array<String>):Data {
            var description:String = ""
            var unit:String = ""

            listDescription.forEach { item ->
                if (item.substringBefore('|')==dataDb.id.toString()) {
                    description = item.substringAfter('|').substringBefore('^')
                    unit = item.substringAfter('^')
                }
            }

            return Data(
                id = dataDb.id,
                value = if(dataDb.name == "lastTimeUpdate") convertDateServerToDateUI(dataDb.value) else dataDb.value,
                name = dataDb.name,
                type = dataDb.type,
                description = description,
                unit = unit)
        }






    private fun convertDateServerToDateUI(date:String?):String{
        if (date == null) return ""
        return "${date.substring(4,14)} ${date.substring(15,20)}"
    }



}