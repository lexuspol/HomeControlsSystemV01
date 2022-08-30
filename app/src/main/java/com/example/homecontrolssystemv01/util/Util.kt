package com.example.homecontrolssystemv01.util

import android.util.Log
import com.example.homecontrolssystemv01.data.database.MessageDbModel
import com.example.homecontrolssystemv01.domain.model.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

fun createDataContainer(listData: List<Data>?, listSetting:List<DataSetting>?):MutableList<DataContainer>{

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
    val dataContainer = DataContainer(id,Data(),DataSetting())

    listContainer.forEach {
        if (it.id == id) {
            return it
        }
    }

    return dataContainer
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

fun createMessageListLimit(dataList: List<Data>, settingList: List<DataSetting>):List<Message>{

    val listMessage:MutableList<Message> = mutableListOf()

    val listDataFloat:MutableList<Pair<Int,Float>> = mutableListOf()

    dataList.forEach {data->
        if (data.type == 3) {                              //3 - Real type
            if (data.value?.toFloatOrNull() != null){
                listDataFloat.add(Pair(data.id,data.value.toFloat()))
            }
        }
    }

    settingList.forEach { setting->
        if (setting.limitMode){
            listDataFloat.forEach { pair ->
                if (pair.first == setting.id){
                    when{
                        (pair.second>setting.limitMax)  -> {
                            listMessage.add(
                                Message(
                                    Date().time,
                                    setting.id,
                                    type = 1,
                                    "${setting.description}. Выше ${setting.limitMax} ${setting.unit}"))}
                        (pair.second<setting.limitMin)  -> {
                            listMessage.add(
                            Message(
                                Date().time,
                                setting.id,
                                type = 1,
                                "${setting.description}. Ниже ${setting.limitMin} ${setting.unit}"))}
                        }
                    }
                }
            }
        }
    return listMessage
    }

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
    return format.format(date)
}





