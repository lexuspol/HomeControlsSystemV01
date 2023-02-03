package com.example.homecontrolssystemv01.data.mapper



import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.data.database.DataDbModel
import com.example.homecontrolssystemv01.data.database.DataSettingDbModel
import com.example.homecontrolssystemv01.data.database.MessageDbModel
import com.example.homecontrolssystemv01.data.database.ShopDbModel
import com.example.homecontrolssystemv01.data.network.model.DataDto
import com.example.homecontrolssystemv01.data.network.model.DataJsonContainerDto
import com.example.homecontrolssystemv01.domain.enum.DataType
import com.example.homecontrolssystemv01.domain.model.data.DataModel
import com.example.homecontrolssystemv01.domain.model.setting.DataSetting
import com.example.homecontrolssystemv01.domain.model.message.Message
import com.example.homecontrolssystemv01.domain.model.shop.ShopItem
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

    fun settingDbModelToEntity(dataSetting:DataSettingDbModel) = DataSetting(
        id = dataSetting.id,
        description = dataSetting.description,
        visible= dataSetting.visible,
        limitMode = dataSetting.limitMode,
        limitMax= dataSetting.limitMax,
        limitMin= dataSetting.limitMin,
        unit = dataSetting.unit

    )

    fun settingToDbModel(dataSetting: DataSetting) = DataSettingDbModel(
        id = dataSetting.id,
        description = dataSetting.description,
        visible= dataSetting.visible,
        limitMode = dataSetting.limitMode,
        limitMax= dataSetting.limitMax,
        limitMin= dataSetting.limitMin,
        unit = dataSetting.unit

    )

//




        fun mapDataToEntity(dataDb: DataDbModel,
                            listResourses:List<Array<String>>,
                            dataFormat:String,
                            converBool:Boolean
         //                   setting:List<DataSettingDbModel>,

        ): DataModel {
            var description= dataDb.name.toString()
            var unit= ""
            var valueBoolFor_1 = "1"
            var valueBoolFor_0 = "0"

            val  listDescription = listResourses[0]
            val  listAlarm = listResourses[1]


            listDescription.forEach { item ->
                if (item.substringBefore('|')==dataDb.id.toString()) {
                    description = item.substringAfter('|').substringBefore('^')
                    unit = item.substringAfter('^')
                    valueBoolFor_1 = unit.substringBefore('/')
                    valueBoolFor_0 = unit.substringAfter('/')
                }
            }

            return DataModel(
                id = dataDb.id,
                //value = if(dataDb.name == "lastTimeUpdate") convertDateServerToDateUI(dataDb.value) else dataDb.value,
                value = when(dataDb.type){

                    DataType.BOOL.dataTypeNumber ->{
                        if (converBool){
                            when(dataDb.value){
                                "1"->valueBoolFor_1
                                "0"->valueBoolFor_0
                                else -> dataDb.value
                            }
                        }else dataDb.value
                    }

                    DataType.DTL.dataTypeNumber->convertDateServerToDateUI(dataDb.value,dataFormat)
                    DataType.TIME.dataTypeNumber->convertTimeServerToTimeUI(dataDb.value)
                    DataType.WORD.dataTypeNumber->convertWordToByte(dataDb.value,16)
                    else -> dataDb.value
                },
                name = dataDb.name,
                type = dataDb.type,
                description = description,
                unit = unit,
                listString = when(dataDb.id){
                    DataID.alarmMessage.id -> listAlarm.toList()
                    else -> listOf()
                }
    )

        }

    private fun convertWordToByte(data: String?, len:Int): String? {

        return try {
            val int = data?.toInt()?:0
            String.format("%" + len + "s", int.toString(2)).replace(" ".toRegex(), "0")
        }catch (e:NumberFormatException){
            null
        }

    }

    fun convertDateServerToDateUI(date:String?,dataFormat: String):String{

        var dateReturn = "error time"

        if (date == null) return dateReturn

        //DTL#2022-11-20-11:22:45.209672

        val indexDateFirst = date.indexOf("#")
        val indexDataLast = date.lastIndexOf(".")
        val indexDataBetween = date.lastIndexOf("-")

        if (indexDateFirst==3&&indexDataBetween>indexDateFirst&&indexDataLast>indexDataBetween){
            dateReturn = date.substring(indexDateFirst+1,indexDataBetween) + " " +
                         date.substring(indexDataBetween+1,indexDataLast)
        }

        return dateReturn
    }

    fun convertTimeServerToTimeUI(time:String?):String{
        if (time == null) return "00:00"

        //T#2H_5M_8S_815MS
        //T#0MS
        val indexTimeFirst = time.indexOf("#")
        val index_H = time.indexOf("h")
        val index_M = time.indexOf("m")
        val index_S = time.indexOf("s")

        when{
            indexTimeFirst!=1||(index_S-index_M)<3 -> return "00:00"
            index_H>2&&index_M>5&&index_S>8 -> {
                return "${time.substring(indexTimeFirst+1,index_H)}h " +
                        "${time.substring(index_H+2,index_M)}m " +
                        "${time.substring(index_M+2,index_S)}s"
            }
            index_H>2&&index_S>5 -> {
                return "${time.substring(indexTimeFirst+1,index_H)}h " +
                        "${time.substring(index_H+2,index_S)}s"
            }
            index_M>2&&index_S>5 -> {
                return "${time.substring(indexTimeFirst+1,index_M)}m " +
                        "${time.substring(index_M+2,index_S)}s"
            }
            index_S>2 -> {
               return "${time.substring(indexTimeFirst+1,index_S)}s"
            }

            else ->return "00:00"

        }

    }

    fun mapMessageToEntity(message: MessageDbModel): Message {
        return  Message(
            time = message.time,
            id = message.id,
            type = message.type,
            description = message.description,
            visible = message.visible
        )
    }

    fun mapEntityToMessage(message: Message):MessageDbModel {
        return  MessageDbModel(
            time = message.time,
            id = message.id,
            type = message.type,
            description = message.description,
            visible = message.visible
        )


    }

    fun shopItemToDbModel(shopItem: ShopItem): ShopDbModel {
        return ShopDbModel(
                itemId = shopItem.itemId,
            itemName = shopItem.itemName,
            groupId = shopItem.groupId,
            countString = shopItem.countString,
            enabled = shopItem.enabled
                )
    }

    fun mapShopItemToEntity(shopDbModel: ShopDbModel):ShopDbModel {
        return ShopDbModel(
            itemId = shopDbModel.itemId,
            itemName = shopDbModel.itemName,
            groupId = shopDbModel.groupId,
            countString = shopDbModel.countString,
            enabled = shopDbModel.enabled
        )

    }


}