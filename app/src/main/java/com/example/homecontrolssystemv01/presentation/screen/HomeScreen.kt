package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.domain.model.Data
import com.example.homecontrolssystemv01.domain.model.DataConnect
import com.example.homecontrolssystemv01.ui.theme.Purple200

@Composable
fun HomeDataScreen(
    modifier: Modifier = Modifier,
    listData:List<Data>,
    dataConnect:MutableState<DataConnect>

){
    Column(
        modifier = Modifier
            .padding(10.dp)
    ){
        if (listData.isNotEmpty()) {


            Text(text = "${listData[1].description}   ${listData[1].value} С",
                style = MaterialTheme.typography.h6)
            Text(text = "Входная дверь ${listData[2].value}",
                style = MaterialTheme.typography.h6)
            Text(text = "Дверь на террасу ${listData[3].value}",
                style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.size(20.dp))

            Row{
                Text(text = "Кухня  ")
                Text(text = "Спальная  ")
                Text(text = "Детская  ")
                Text(text = "Кинозал")
            }

            Row{
                Text(text = "${listData[13].value} С       ")
                Text(text = "${listData[15].value} С       ")
                Text(text = "${listData[17].value} С       ")
                Text(text = "${listData[19].value} С       ")
            }
            Row{
                Text(text = "${listData[14].value} %       ")
                Text(text = "${listData[16].value} %       ")
                Text(text = "${listData[18].value} %       ")
                Text(text = "${listData[20].value} %       ")
            }

        }
    }

}