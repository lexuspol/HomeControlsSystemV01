package com.example.homecontrolssystemv01.util

import android.util.Log
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.data.mapper.insertMessage
import com.example.homecontrolssystemv01.domain.enum.DataType
import com.example.homecontrolssystemv01.domain.enum.MessageType
import com.example.homecontrolssystemv01.domain.model.*
import com.example.homecontrolssystemv01.domain.model.setting.DataSetting
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

fun createDataContainer(listData: List<DataModel>?, listSetting:List<DataSetting>?):MutableList<DataContainer>{

    val listContainer = mutableListOf<DataContainer>()

    listData?.forEach { data->
        var setting = DataSetting()
        listSetting?.forEach { dataSetting->
            if (data.id == dataSetting.id) setting = dataSetting
        }
        listContainer.add(DataContainer(data.id,data,setting))
    }
    return listContainer

}

fun giveDataById(listContainer: MutableList<DataContainer>,id:Int):DataContainer{
    val dataModelContainer = DataContainer(id,DataModel(), DataSetting())

    listContainer.forEach {
        if (it.id == id) {
            return it
        }
    }

    return dataModelContainer
}

//fun loadingIsComplete(dataList: List<Data>?,connectInfo:ConnectInfo, timeId:Int):Boolean{
//
//    var complete = false
//
//    val delayTime = when(connectInfo.modeConnect){
//        ModeConnect.LOCAL -> 5
//        ModeConnect.REMOTE -> 30
//        ModeConnect.SERVER -> 60
//        else -> 0
//    }
//
//
//    if (dataList.isNullOrEmpty()) {
//        complete = false
//    } else {
//        dataList.forEach { data->
//            if (data.id == timeId){
//                complete = difTime(data.value.toString())< delayTime*60*1000
//                return@forEach
//            }
//        }
//
//    }
//    return complete
//}

fun difTime(date:String):Long{

    var dif = 0L

    val currentDate = Date().time
    try{
        val serverDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("$date")?.time

        dif = if (serverDate != null){
            currentDate - serverDate
        }else{
            0L
        }
        //Log.d("HCS_HomeScreen", "Дата разница = $dif")
    }catch (e : Exception){
        Log.d("HCS_HomeScreen_Error", e.toString())
    }

    return dif

}

fun stringLimittoFlout(stringLimit:String):Float {

    return stringLimit.toFloatOrNull()?.times(100)?.roundToInt()?.div(100.0)?.toFloat() ?: 0F


}

fun countEnergy(count:UInt?):String {

    return if (count != null) {
        val countH = count.div(100u)
        val countL = count.rem(100u)
        if(countL< 10u) "$countH,0$countL" else "$countH,$countL"

    } else {
        "0"
    }
}

fun  visible(id:Int, settingList: List<DataSetting>?):Boolean{

    var visible = false

    if (settingList.isNullOrEmpty()){
    } else settingList.forEach {
        if (it.id==id) {
            visible = it.visible
        }
    }

    return visible

}

fun createMessageListLimit(dataModelList: List<DataModel>,
                           settingList: List<DataSetting>,
dataFormat: String):List<Message>{

   // var time = 0L

    val dateTime = dataModelList.find { it.id == DataID.lastTimeUpdate.id }?.value
    val dateTimeLong = if (!dateTime.isNullOrEmpty()) convertStringTimeToLong(dateTime,dataFormat) else -1L

    val listMessage:MutableList<Message> = mutableListOf()

    val listDataFloat:MutableList<Pair<Int,Float>> = mutableListOf()
    val listDataBool:MutableList<Pair<Int,Float>> = mutableListOf()

    dataModelList.forEach { data->
        when(data.type){
            DataType.REAL.int -> {
                if (data.value?.toFloatOrNull() != null){
                    listDataFloat.add(Pair(data.id,data.value.toFloat()))
            }}

            DataType.BOOL.int ->{
                if (data.value?.toFloatOrNull() != null){
                    listDataBool.add(Pair(data.id,data.value.toFloat()))
                }}
        }
    }

    settingList.forEach { setting->
        if (setting.limitMode){

            //Float
            listDataFloat.forEach { pair ->

                if (pair.first == setting.id){
                    when{
                        (pair.second>setting.limitMax)  -> {
                            listMessage.add(
                                Message(
 //                                   Date().time+time,
                                    dateTimeLong,
                                    setting.id,
                                    type = 1,
                                    "${setting.description}. Выше ${setting.limitMax} ${setting.unit}"))
                            //Log.d("HCS_Limit", "${setting.description}. Выше ${setting.limitMax} ${setting.unit}")
                        }

                        (pair.second<setting.limitMin)  -> {
                            listMessage.add(
                            Message(
 //                               Date().time+time,
                                dateTimeLong,
                                setting.id,
                                type = 1,
                                "${setting.description}. Ниже ${setting.limitMin} ${setting.unit}"))
                            //Log.d("HCS_Limit", "${setting.description}. Ниже ${setting.limitMin} ${setting.unit}")
                        }
                        }
                    }
//                time += 1
                }

            //Bool
            listDataBool.forEach { pair ->

                if (pair.first == setting.id){

                    val state = when{
                        setting.limitMax==1f&&pair.second==1f -> setting.unit.substringBefore('/')
                        setting.limitMin==1f&&pair.second==0f -> setting.unit.substringAfter('/')
                        else -> "no"
                    }

                    if (state!="no"){
                        listMessage.add(
                            Message(
//                                Date().time+time,
                                dateTimeLong,
                                setting.id,
                                type = 1,
                                description = "${setting.description}. Состояние - $state."
                            ))
                    }



//                    when{
//                        (setting.limitMax==1f&&pair.second==1f)  -> {
//                            listMessage.add(
//                                Message(
//                                    Date().time+time,
//                                    setting.id,
//                                    type = 1,
//                                    "${setting.description}. Состояние: ${setting.limitMax} ${setting.unit}"))
//                            //Log.d("HCS_Limit", "${setting.description}. Выше ${setting.limitMax} ${setting.unit}")
//                        }
//
//                        (setting.limitMin==1f&&pair.second==0)  -> {
//                            listMessage.add(
//                                Message(
//                                    Date().time+time,
//                                    setting.id,
//                                    type = 1,
//                                    "${setting.description}. Состояние: ${setting.limitMin} ${setting.unit}"))
//                            //Log.d("HCS_Limit", "${setting.description}. Ниже ${setting.limitMin} ${setting.unit}")
//                        }
//                    }
                }
 //               time += 1
            }
            }
        }

    //message контролируется в UI - SwipeRefreshState
    //добавление данного сообщения означает окончание обновления
    listMessage.add(
        Message(
            dateTimeLong,
            DataID.completeUpdate.id,
            MessageType.SYSTEM.int,
            description = DataID.completeUpdate.name + " OK"
        ))

    return listMessage
    }

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
    return format.format(date)
}


fun convertStringTimeToLong(time:String,dataFormat:String):Long{
    return try {
        SimpleDateFormat(dataFormat).parse(time)?.time?:-1L
    }catch (e:NullPointerException){
        -1L
    }catch (e:IllegalArgumentException){
        -1L
    }catch (e:ParseException){
        -1L
    }
}






