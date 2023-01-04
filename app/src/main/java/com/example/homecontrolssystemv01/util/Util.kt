package com.example.homecontrolssystemv01.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.data.database.DataDao
import com.example.homecontrolssystemv01.data.database.MessageDbModel
import com.example.homecontrolssystemv01.domain.enum.DataType
import com.example.homecontrolssystemv01.domain.enum.MessageType
import com.example.homecontrolssystemv01.domain.model.data.DataContainer
import com.example.homecontrolssystemv01.domain.model.data.DataModel
import com.example.homecontrolssystemv01.domain.model.message.Message
import com.example.homecontrolssystemv01.domain.model.setting.DataSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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

fun giveDataById(listContainer: MutableList<DataContainer>, id:Int): DataContainer {
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


suspend fun insertMessage(context: Context, dataDao: DataDao, idMessage:Int){

    try {

//        if (idMessage == DataID.completeUpdate.id){
//
//            dataDao.insertMessage(MessageDbModel(Date().time,
//                DataID.completeUpdate.id,
//                MessageType.SYSTEM.int
//                ,DataID.completeUpdate.name + " OK"))
//
//        }else{

        val messageListRes = context.resources.getStringArray(R.array.message)

        messageListRes.forEach { messageItemRes->

            val idRes = messageItemRes.substringBefore('|').toInt()

            if (idRes == idMessage){

                val descriptionRes =messageItemRes.substringAfter('|').substringBefore('^')
                val typeRes = messageItemRes.substringAfter('^').toInt()

                dataDao.insertMessage(MessageDbModel(Date().time,idMessage,typeRes,descriptionRes))

                return@forEach
            }
        }
    }catch (e:Exception){
        Log.d("HCS_insertMessage", e.toString())
        toastMessage(context, "Error insert message to base")
    }
}

suspend fun toastMessage(context: Context, message:String){
    coroutineScope {
        launch(Dispatchers.Main){
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}







fun createMessageListLimit(
    dataModelList: List<DataModel>,
    settingList: List<DataSetting>,
    dataFormat: String,
    alarmMessageDescription: Array<String>
):List<Message>{

   // var time = 0L

    val dateTime = dataModelList.find { it.id == DataID.lastTimeUpdate.id }?.value
    val dateTimeLong = if (!dateTime.isNullOrEmpty()) convertStringTimeToLong(dateTime,dataFormat) else -1L

    val listMessage:MutableList<Message> = mutableListOf()

    val listDataFloat:MutableList<Pair<Int,Float>> = mutableListOf()
    val listDataBool:MutableList<Pair<Int,Float>> = mutableListOf()

    val dataAlarmMessage = dataModelList.find { it.id == DataID.alarmMessage.id }
    val alarmMessageSetting = settingList.find { it.id == DataID.alarmMessage.id }




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
                                    "${setting.description}. Выше ${setting.limitMax} ${setting.unit}")
                            )
                            //Log.d("HCS_Limit", "${setting.description}. Выше ${setting.limitMax} ${setting.unit}")
                        }

                        (pair.second<setting.limitMin)  -> {
                            listMessage.add(
                            Message(
 //                               Date().time+time,
                                dateTimeLong,
                                setting.id,
                                type = 1,
                                "${setting.description}. Ниже ${setting.limitMin} ${setting.unit}")
                            )
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
                            )
                        )
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


    if (alarmMessageSetting != null){

        val alarmWord = createAlarmWord(dataAlarmMessage,alarmMessageSetting)

        if (alarmWord !=null){
            alarmWord.forEachIndexed { index, bit ->
                if (bit == '1') {
                    listMessage.add(
                        Message(
                            dateTimeLong,
                            createAlarmId(DataID.alarmMessage.id,index),
                            type = MessageType.ALARM.int,
                            description = alarmMessageDescription[index]
                        )
                    )
                }
            }
        }else {

            listMessage.add(
                Message(
                    dateTimeLong,
                    DataID.alarmMessage.id,
                    type = MessageType.WARNING.int,
                    description = alarmMessageDescription.last()
                )
            )
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
        )
    )

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



fun createAlarmWord(data: DataModel?, setting:DataSetting?):String?{

    val len = 16

return if (data !=null && setting !=null){

    val alarmWord = CharArray(len)

    val alarmSettingString = convertIntToBinaryString(setting.limitMax.toInt(),len)

    if (alarmSettingString.isNotEmpty()){
        alarmSettingString.forEachIndexed { index, bit ->
            if (bit == '1' && (data.value?.get(index) ?: '0') == '1') {
                alarmWord[index] = '1'
            }else alarmWord[index] = '0'
        }
        return String(alarmWord)
    }else return null
}else null
}

fun convertIntToBinaryString(int:Int, len:Int):String{
    return try {
        String.format("%" + len + "s", int.toString(2)).replace(" ".toRegex(), "0")
    }catch (e:IllegalArgumentException ){
        ""
    }
}

fun createAlarmId(id:Int, index:Int):Int{

    return try {
        ("$id$index").toInt()
    }catch (e:NumberFormatException){
        id
    }

}






