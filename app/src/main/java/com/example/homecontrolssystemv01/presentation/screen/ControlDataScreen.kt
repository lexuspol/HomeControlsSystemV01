package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.domain.model.Data
import com.example.homecontrolssystemv01.presentation.enums.DataSetting
import com.example.homecontrolssystemv01.ui.theme.Purple200
import com.example.homecontrolssystemv01.ui.theme.Purple700

@Composable
fun ControlDataScreen(listData:List<Data>,onValueChange: (Int) -> Unit){

    Column() {
        ButtonLight(listData = listData, index = 23, onValueChange)
        ButtonLight(listData = listData, index = 24, onValueChange)
    }
}

@Composable
fun ButtonLight(listData:List<Data>,index:Int,onValueChange: (Int) -> Unit){
    Button(
        onClick = {
            onValueChange(index)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Purple700)

    ) {
        Icon(
            Icons.Filled.Done,
            contentDescription = null,
            modifier = Modifier
                .size(ButtonDefaults.IconSize),
            tint = colorLight(listData[index].value.toString())
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = listData[index].description,
            color = colorLight(listData[index].value.toString()),
        style = MaterialTheme.typography.h6


        )
    }
}



fun colorLight(value: String):Color{
    return if (value == "1") Color.Yellow else Color.White
}