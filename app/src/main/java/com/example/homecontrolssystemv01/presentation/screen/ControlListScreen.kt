package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.domain.model.ConnectInfo
import com.example.homecontrolssystemv01.domain.model.ControlInfo
import com.example.homecontrolssystemv01.domain.model.DataContainer
import com.example.homecontrolssystemv01.domain.model.ModeConnect
import com.example.homecontrolssystemv01.ui.theme.Purple700
import com.example.homecontrolssystemv01.util.giveDataById

@Composable
fun ControlListScreen(
    modifier:Modifier,
    listDataContainer:MutableList<DataContainer>,
    connectInfo: MutableState<ConnectInfo>,
    onControl: (ControlInfo) -> Unit){

    val localState = giveDataById(listDataContainer,DataID.connectMode.id).dataModel.value == ModeConnect.LOCAL.name
    val page = remember { mutableStateOf(1) }

        Column(modifier = modifier,
                verticalArrangement = Arrangement.SpaceBetween
        ) {
                Box(
                    modifier = Modifier.weight(6f)
                ) {
                    when(page.value){
                        1-> PageBox1(
                            listDataContainer = listDataContainer,
                            localState = localState,
                            onControl = onControl)
                        2-> PageBox2(
                            listDataContainer = listDataContainer,
                            localState = localState,
                            onControl = onControl)
                    }
               }//box 1

            CardSettingElement {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {

                    Button(
                        onClick = {page.value = page.value-1},
                        enabled = page.value>1,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Purple700)

                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = "Back", style = MaterialTheme.typography.h6)
                    }

                    Button(
                        onClick = {page.value = page.value+1},
                        enabled = page.value<2,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Purple700)

                    ) {
                        Icon(
                            Icons.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = "Next",style = MaterialTheme.typography.h6)
                    }
                }
            }//box 2
        }
        }
}

@Composable
fun PageBox1(listDataContainer:MutableList<DataContainer>, localState:Boolean,
            onControl: (ControlInfo) -> Unit){

    val lightLivingOn = giveDataById(listDataContainer,22)
    val lightSleepOn = giveDataById(listDataContainer,23)
    val lightChildOn = giveDataById(listDataContainer,24)
    val lightCinemaOn = giveDataById(listDataContainer,25)
    val lightSecondFloorOn = giveDataById(listDataContainer,26)
    val lightOutdoor = giveDataById(listDataContainer,27)

    Column() {

        ButtonLight(lightLivingOn, id = 22, localState,onControl)
        ButtonLight(lightSleepOn, id = 23, localState,onControl)

        ButtonLight(lightChildOn, id = 24, localState,onControl)
        ButtonLight(lightCinemaOn, id = 25, localState,onControl)

        ButtonLight(lightSecondFloorOn, id = 26, localState,onControl)
        ButtonLight(lightOutdoor, id = 27, localState,onControl)

    }
}

@Composable
fun PageBox2(listDataContainer:MutableList<DataContainer>, localState:Boolean,
             onControl: (ControlInfo) -> Unit){
    val slidingGateOpen = giveDataById(listDataContainer,30)
    ButtonLight(slidingGateOpen, id = 30, localState,onControl)

    //калитка
    //ворота в гараж
    //вотора откатные
}

@Composable
fun ButtonLight(dataContainer: DataContainer,id:Int,localState:Boolean,onValueChange: (ControlInfo) -> Unit){



    var colorButton = Purple700
    var colorText = Color.White

    if (dataContainer.dataModel.value.toString() == "1") {
        colorButton = Color.Yellow
        colorText = Purple700
    }

    val showDialog = remember { mutableStateOf(false)}
    AlertDialogLocalState (showDialog.value,onDismiss =  {showDialog.value = false})

    Button(
        onClick = {
            //блокировка управления
            if (localState){
                onValueChange(ControlInfo(id))

                //colorButton = if (colorButton == Purple700) Color.Yellow else Purple700
                //colorText = if(colorText == Color.White) Purple700 else Color.White

            }else{
                showDialog.value = true
            }



                  },
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = colorButton)

    ) {
        Icon(
            Icons.Filled.Done,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize),
            tint = colorText
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = dataContainer.dataModel.description,
            color = colorText,
        style = MaterialTheme.typography.h6
        )
    }
}

@Composable
fun AlertDialogLocalState(showDialog: Boolean,onDismiss: () -> Unit){

    if (showDialog){
        AlertDialog(onDismissRequest = onDismiss,
            title = {Text("Команда откланена.")},
            text = {Text("Управление возможно только в режиме LOCAL") },
            confirmButton = {
                Button(onClick = { onDismiss() })
                {
                    Text("OK")
                }
            }
        )
    }

}



