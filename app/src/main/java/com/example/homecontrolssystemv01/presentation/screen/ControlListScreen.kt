package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.domain.model.ControlInfo
import com.example.homecontrolssystemv01.domain.model.DataContainer
import com.example.homecontrolssystemv01.ui.theme.Purple700
import com.example.homecontrolssystemv01.util.giveDataById

@Composable
fun ControlListScreen(
    modifier:Modifier,
    listDataContainer:MutableList<DataContainer>,
    onControl: (ControlInfo) -> Unit){

  //  if (loadingIsComplete){

    val lightLivingOn = giveDataById(listDataContainer,22)
    val lightSleepOn = giveDataById(listDataContainer,23)
    val lightChildOn = giveDataById(listDataContainer,24)
    val lightCinemaOn = giveDataById(listDataContainer,25)
    val lightSecondFloorOn = giveDataById(listDataContainer,26)


        Column(modifier = modifier
        ) {
            CardSettingElement {
                Column() {

                        ButtonLight(lightLivingOn, id = 22, onControl)
                        ButtonLight(lightSleepOn, id = 23, onControl)


                        ButtonLight(lightChildOn, id = 24, onControl)
                        ButtonLight(lightCinemaOn, id = 25, onControl)

                    ButtonLight(lightSecondFloorOn, id = 26, onControl)
                }

                //калитка
                //ворота в гараж
                //вотора откатные
            }

        }
//
//    } else {
//        CustomLinearProgressBar()
//    }

}

@Composable
fun ButtonLight(dataContainer: DataContainer,id:Int,onValueChange: (ControlInfo) -> Unit){

    var colorButton = Purple700
    var colorText = Color.White

    if (dataContainer.data.value.toString() == "1") {
        colorButton = Color.Yellow
        colorText = Purple700
    }

    Button(
        onClick = {
            onValueChange(ControlInfo(id))
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = colorButton)

    ) {
        Icon(
            Icons.Filled.Done,
            contentDescription = null,
            modifier = Modifier
                .size(ButtonDefaults.IconSize),
            tint = colorText
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = dataContainer.data.description,
            color = colorText,
        style = MaterialTheme.typography.h6


        )
    }
}



