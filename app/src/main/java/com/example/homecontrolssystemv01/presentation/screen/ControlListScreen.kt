package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.domain.enum.ControlValue
import com.example.homecontrolssystemv01.domain.model.*
import com.example.homecontrolssystemv01.domain.model.data.DataContainer
import com.example.homecontrolssystemv01.domain.model.message.ModeConnect
//import com.example.homecontrolssystemv01.ui.theme.Purple700
import com.example.homecontrolssystemv01.util.giveDataById

@Composable
fun ControlListScreen(
    modifier:Modifier,
    listDataContainer:MutableList<DataContainer>,
    onControl: (ControlInfo) -> Unit){

    val localState = giveDataById(listDataContainer,DataID.connectMode.id).dataModel.value == ModeConnect.LOCAL.name
    val page = remember { mutableStateOf(1) }

   // val listDataLight = listof(DataLightControl)

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
                      //  colors = ButtonDefaults.buttonColors(backgroundColor = Purple700)

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
                      //  colors = ButtonDefaults.buttonColors(backgroundColor = Purple700)

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

    Column() {

        ButtonLight(giveDataById(listDataContainer,DataID.lightSleepState.id),
            giveDataById(listDataContainer,DataID.buttonLightSleep.id),
            localState,onControl)

        ButtonLight(giveDataById(listDataContainer,DataID.lightChildState.id),
            giveDataById(listDataContainer,DataID.buttonLightChild.id),
            localState,onControl)

        ButtonLight(giveDataById(listDataContainer,DataID.lightCinemaState.id),
            giveDataById(listDataContainer,DataID.buttonLightCinema.id),
            localState,onControl)

        ButtonLight(giveDataById(listDataContainer,DataID.lightOutdoorState.id),
            giveDataById(listDataContainer,DataID.buttonLightOutdoor.id),
            localState,onControl)

    }
}

@Composable
fun PageBox2(listDataContainer:MutableList<DataContainer>, localState:Boolean,
             onControl: (ControlInfo) -> Unit){

    Column() {

        ButtonGate(giveDataById(listDataContainer,DataID.wicketUnlock.id),
            giveDataById(listDataContainer,DataID.buttonWicketUnlock.id),
            localState,true, onControl,false)

        ButtonGate(giveDataById(listDataContainer,DataID.garageGateOpen.id),
            giveDataById(listDataContainer,DataID.buttonGateGarageSBS.id),
            localState,false, onControl)

        ButtonGate(giveDataById(listDataContainer,DataID.slidingGateOpen.id),
            giveDataById(listDataContainer,DataID.buttonGateSlidingSBS.id),
            localState,false, onControl)
    }

}

@Composable
fun ButtonLight(dataState: DataContainer, dataControl: DataContainer, localState:Boolean, onValueChange: (ControlInfo) -> Unit){


    val colorOn = Color.Yellow
    val colorOff = MaterialTheme.colors.primary
    var colorText = MaterialTheme.colors.primary

    var colorButton = MaterialTheme.colors.background

   val enableButton = remember { mutableStateOf(true)}

    val int = when(dataState.dataModel.value.toString()){
        dataState.dataModel.unit.substringBefore('/') -> 1 //on
        dataState.dataModel.unit.substringAfter('/') -> 2 //off
        else -> 0}

    val stateLightInt =
       remember { mutableStateOf(int  )}

    if (int == stateLightInt.value){
        if (int == 1)  colorText = colorOn
        if (int == 2)  colorText = colorOff
    }



    val showDialog = remember { mutableStateOf(false)}
    AlertDialogLocalState (showDialog.value,onDismiss =  {showDialog.value = false})

    //Log.d ("HCS", "rem int = ${stateLightInt.value}  int = $int")

    Button(
        onClick = {
            //блокировка управления
            if (localState){

                onValueChange(ControlInfo(dataControl.id,ControlValue.ON.value))

                when (int) {
                    1 -> {
                        stateLightInt.value = 2
                        colorText = colorOff
                    }
                    2 -> {
                        stateLightInt.value = 1
                        colorText = colorOn
                    }
                }

                //enableButton.value = false
            }else{
                showDialog.value = true
            }
                  },
     //   enabled = enableButton.value,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = colorButton)

    ) {
//        Icon(
//            Icons.Filled.Star,
//            contentDescription = null,
//            modifier = Modifier.size(ButtonDefaults.IconSize),
//           tint = colorText
//        )
//        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = dataControl.dataModel.description,
         color = colorText,
        style = MaterialTheme.typography.h6
        )
    }
}

@Composable
fun ButtonGate(dataState: DataContainer, dataControl: DataContainer,
               localState:Boolean,
               permitRemoteControl: Boolean,
               onValueChange: (ControlInfo) -> Unit,
               showCheck:Boolean = true){

    var colorButton = MaterialTheme.colors.background
    var colorText = MaterialTheme.colors.primary
    val enableButton = remember { mutableStateOf(true)}

//    if (dataState.dataModel.value.toString()==ControlValue.ON.value){
//        colorButton = Color.Green
//        colorText = Purple700
//    }

    if (dataControl.dataModel.value.toString() == ControlValue.GATE_CHECK.value){
        colorButton = Color.Green//если есть команда, то зеленый
        //colorText = MaterialTheme.colors.background
        enableButton.value = true
    }


    val showDialog = remember { mutableStateOf(false)}
    AlertDialogLocalState (showDialog.value,onDismiss =  {showDialog.value = false})

    val showDialogGate = remember { mutableStateOf(false)}
    AlertDialogGateState (dataControl,showDialogGate.value,
        onDismiss =  {showDialogGate.value = false},onValueChange)

    Button(
        onClick = {
            //блокировка управления
                if (localState || permitRemoteControl){
                    onValueChange(ControlInfo(dataControl.id,ControlValue.GATE_START.value))
                    enableButton.value = false

                    if(showCheck) {
                        showDialogGate.value = true
                    }

                }else{
                    showDialog.value = true
                }
        },
        enabled = enableButton.value,
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
        Text(text = dataControl.dataModel.description,
            color = colorText,
            style = MaterialTheme.typography.h6
        )
    }
}


@Composable
fun AlertDialogGateState(dataControl: DataContainer,
                         showDialog: Boolean,
                         onDismiss: () -> Unit,
                         onValueChange: (ControlInfo) -> Unit)
 {

     val checkState = dataControl.dataModel.value==ControlValue.GATE_CHECK.value
     //var isAnimated by remember { mutableStateOf(checkState)}
     //val colorAnim = remember { Animatable(Color.DarkGray) }
     //LaunchedEffect(isAnimated) {
       //  colorAnim.animateTo(if (isAnimated) Color.Green else Color.Red, animationSpec = tween(2000))
    // }

    if (showDialog){
        AlertDialog(onDismissRequest = onDismiss,
            title = {Text(dataControl.dataModel.description)},
            text = {
                Column() {
                    Text("Управление в режиме Step-By-Step")
//                    Text(
//                        when(){
//                            checkState -> "Управление разрешено"
//
//
//                        }
//                    )
//
//                    if (checkState) Text("Управление разрешено") else{
//                        Text("Проверка возможности управления")
//                    }
                }
//                Box(Modifier
//                    .fillMaxWidth()
//                    .background(colorAnim.value)) {
//
//
//                }


            },
            buttons = {

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = {
                            onValueChange(ControlInfo(dataControl.id,ControlValue.GATE_RUN.value))
                                  onDismiss()},

                    enabled = checkState) {
                        Text("Машина")
                    }

                    Button(
                        onClick = {
                            onValueChange(ControlInfo(dataControl.id,ControlValue.GATE_PART_OPEN.value))
                            onDismiss()},
                        enabled = checkState) {
                        Text("Пешеход")
                    }
                }

            }
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



