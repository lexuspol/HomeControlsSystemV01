package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.domain.model.ControlInfo
import com.example.homecontrolssystemv01.domain.model.DataContainer
import com.example.homecontrolssystemv01.ui.theme.Purple700
import com.example.homecontrolssystemv01.util.giveDataById

@Composable
fun ControlListScreen(
    listDataContainer:MutableList<DataContainer>,
    loadingIsComplete:Boolean,
    onControl: (ControlInfo) -> Unit){

  //  if (loadingIsComplete){

        val lightSleepOn = giveDataById(listDataContainer,23)
        val lightChildOn = giveDataById(listDataContainer,24)

        Column() {
            ButtonLight(lightSleepOn, id = 23, onControl)
            ButtonLight(lightChildOn, id = 24, onControl)
        }
//
//    } else {
//        CustomLinearProgressBar()
//    }

}

@Composable
fun ButtonLight(dataContainer: DataContainer,id:Int,onValueChange: (ControlInfo) -> Unit){
    Button(
        onClick = {
            onValueChange(ControlInfo(id))
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
            tint = colorLight(dataContainer.data.value.toString())
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = dataContainer.data.description,
            color = colorLight(dataContainer.data.value.toString()),
        style = MaterialTheme.typography.h6


        )
    }
}



fun colorLight(value: String):Color{
    return if (value == "1") Color.Yellow else Color.White
}