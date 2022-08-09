package com.example.homecontrolssystemv01.presentation.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.domain.model.Data
import com.example.homecontrolssystemv01.domain.model.DataConnect
import com.example.homecontrolssystemv01.domain.model.ModeConnect
import com.example.homecontrolssystemv01.ui.theme.Purple200
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    listData: List<Data>?,
    batteryInfo:String,
    dataConnect:MutableState<DataConnect>

){

    val delayTime = when(dataConnect.value.modeConnect){
        ModeConnect.LOCAL -> 5
        ModeConnect.REMOTE -> 30
        else -> 0
    }

    if (listData.isNullOrEmpty()) {
        Text(
            text = "NO Data",
            style = MaterialTheme.typography.h6
        )
    }else {

        if(difTime(listData[0].value.toString())<delayTime*60*1000){
            HomeScreenData(listData)
        }else{
            Text(
                text = "Ждем",
                style = MaterialTheme.typography.h6
            )
        }
    }
    }

@Composable
fun HomeScreenData(listData:List<Data>){

    Column(
        modifier = Modifier
            .padding(10.dp)
    ){

            Text(
                text = "Время - ${listData[0].value}",
                style = MaterialTheme.typography.h6
            )
            Text(
                text = "${listData[1].description} - ${listData[1].value} С",
                style = MaterialTheme.typography.h6
            )
            Text(
                text = "Входная дверь - ${listData[2].value}",
                style = MaterialTheme.typography.h6
            )
            Text(
                text = "Дверь на террасу - ${listData[3].value}",
                style = MaterialTheme.typography.h6
            )
            Text(
                text = "Давление воды - ${listData[32].value} бар",
                style = MaterialTheme.typography.h6
            )
            Text(
                text = "Счетчик энергии - ${countEnergy(listData[37].value?.toUIntOrNull())} кВт",
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.size(20.dp))

            Row {
                Text(text = "Кухня  ")
                Text(text = "Спальная  ")
                Text(text = "Детская  ")
                Text(text = "Кинозал")
            }

            Row {
                Text(text = "${listData[13].value} С       ")
                Text(text = "${listData[15].value} С       ")
                Text(text = "${listData[17].value} С       ")
                Text(text = "${listData[19].value} С       ")
            }
            Row {
                Text(text = "${listData[14].value} %       ")
                Text(text = "${listData[16].value} %       ")
                Text(text = "${listData[18].value} %       ")
                Text(text = "${listData[20].value} %       ")
            }
        }

    }





@Composable
fun HomeScreenMessage(){



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

fun difTime(date:String):Long{

    var dif = 0L

    val currentDate = Date().time
    try{
        val serverDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("$date:00")?.time

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



