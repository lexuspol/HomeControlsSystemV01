package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.domain.model.*
import com.example.homecontrolssystemv01.ui.theme.Purple700
import com.example.homecontrolssystemv01.util.giveDataById
import com.example.homecontrolssystemv01.util.stringLimittoFlout
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun DataListScreen(
    modifier:Modifier,
    listDataContainer:MutableList<DataContainer>,
    connectInfo:MutableState<ConnectInfo>,
    onSettingChange: (DataSetting) -> Unit,
    onControl: (ControlInfo) -> Unit,
    onLoadData:() -> Unit
){

    val color = when(connectInfo.value.modeConnect){
        ModeConnect.SERVER -> Color.Gray
        ModeConnect.LOCAL -> Color.White
        ModeConnect.REMOTE -> Color.Yellow
        ModeConnect.STOP -> Color.Red
    }


    if (connectInfo.value.modeConnect == ModeConnect.LOCAL) {


        var refreshing by remember { mutableStateOf(false) }

        var timeRem by remember {
            mutableStateOf(giveDataById(listDataContainer,-1).data.value.toString())
        }
        val time = giveDataById(listDataContainer, -1).data.value.toString()

        if (refreshing) {
            //Log.d("HCS_time=","$time")
            //Log.d("HCS_timeRem=","$timeRem")
            if (time == timeRem) {
                refreshing = true
            } else {
                refreshing = false
                timeRem = time
            }

            //Log.d("HCS_refreshing=","$refreshing")
        }


        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = refreshing),
            onRefresh = {
                onLoadData()
                refreshing = true
            }
        ) {
            LazyColumnCreate(modifier,listDataContainer, connectInfo,onSettingChange, onControl)

        }
    }else{
        LazyColumnCreate(modifier,listDataContainer, connectInfo,onSettingChange, onControl)
    }

//    if (loadingIsComplete){
//
//    } else {
//        CustomLinearProgressBar()
//    }


}

@Composable
fun LazyColumnCreate(
    modifier:Modifier,
    listDataContainer:MutableList<DataContainer>,
    connectInfo:MutableState<ConnectInfo>,
    onSettingChange: (DataSetting) -> Unit,
    onControl: (ControlInfo) -> Unit
)

{
    var allList by remember { mutableStateOf(false)}

    Column(modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Box(modifier = Modifier.weight(6f)){
            LazyColumn {
                items(listDataContainer){ container ->

                    if (allList){
                        if (container.id > 0) DataRow(container,onSettingChange,onControl)
                    }else{
                        if (container.setting.visible) DataRow(container,onSettingChange,onControl)
                    }


                }
            }//lazyColumn
        }

        CardSettingElement {

            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,


                    ) {
                    Text(
                        text = "${giveDataById(listDataContainer, -1,).data.value}",
                        // Modifier.padding(start = 5.dp),
                        style = MaterialTheme.typography.subtitle1
                    )
                    Text(
                        text = connectInfo.value.modeConnect.name,
                        // Modifier.padding(start = 5.dp),
                        style = MaterialTheme.typography.subtitle1
                    )
                    Button(
                        onClick = { allList = !allList },
                        // Modifier.padding(end = 5.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Purple700)
                    ) {
                        Text(text = "All list", style = MaterialTheme.typography.subtitle1)
                    }
                }

            }
        }
        //Spacer(modifier = Modifier.size(20.dp))


    }

}





@Composable
fun DataRow(dataContainer: DataContainer,
            onSettingChange: (DataSetting) -> Unit,
            onValueChange: (ControlInfo) -> Unit) {

    val data = dataContainer.data
    val setting = dataContainer.setting

    val showDialog = remember { mutableStateOf(false)}
    Card(
        modifier = Modifier
            .padding(8.dp, 4.dp)
            .fillMaxWidth(),
            // .background(Purple500)
   //         .height(50.dp),
        shape = RoundedCornerShape(8.dp), elevation = 4.dp,

        //contentColor = Purple500,
        //backgroundColor = Purple500
    ) {
        if (showDialog.value){
            MyAlertDialog(data,
                setting,
                showDialog = showDialog.value,
                onDismiss = {showDialog.value = false},
                onSettingChange,
            onValueChange)
            }


        Surface(
 //           modifier = Modifier.background(Purple500),
            color = Purple700
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clickable {
                        showDialog.value = true
                    }

            ) {
                Text(
                    text = data.description,
                    modifier = Modifier.weight(4f),
                    style = MaterialTheme.typography.subtitle1,
                    color = if (setting.visible) Color.Yellow else Color.White
                )
                Text(
                    text = data.value.toString(),
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    color = if (setting.limitMode) Color.Yellow else Color.White
                )
                Text(
                    text = data.unit,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.subtitle1,
                    color = if (setting.limitMode) Color.Yellow else Color.White
                )
            }

        }
    }
}



@Composable
private fun MyAlertDialog(data: Data,
                          setting:DataSetting,
                  showDialog: Boolean,
                  onDismiss: () -> Unit,
                  onSettingChange: (DataSetting) -> Unit,
                  onValueChange: (ControlInfo) -> Unit
){


    val checkedStateVisible = remember { mutableStateOf(setting.visible)}
    val checkedStateLimit = remember { mutableStateOf(setting.limitMode)}
    val checkedStateSetCounter = remember { mutableStateOf(false)}

    var textLimitMax by remember { mutableStateOf(setting.limitMax.toString()) }
    var textLimitMin by remember { mutableStateOf(setting.limitMin.toString()) }
    val errorStateMax = remember { mutableStateOf(false)}
    val errorStateMin = remember { mutableStateOf(false)}

    var textSetCount by remember { mutableStateOf(data.value.toString()) }
    val errorStateSetCount = remember { mutableStateOf(false)}




        if(showDialog){
        AlertDialog(
            onDismissRequest = onDismiss
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                //openDialog.value = false
            ,
            title = {
                Text(text = data.description)
            },
            text = {
                Column() {
                    Row() {
                        Switch(checked = checkedStateVisible.value, onCheckedChange = {
                            checkedStateVisible.value = it
                        })
                        Text(
                        text = "Visible",
                        //style = MaterialTheme.typography.body1.merge(),
                        //modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    if (data.type==3) {
                        Row() {
                            Switch(checked = checkedStateLimit.value, onCheckedChange = {
                                checkedStateLimit.value = it
                            })
                            Text(
                                text = "Limit mode",
                                //style = MaterialTheme.typography.body1.merge(),
                                //modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                        OutlinedTextField(
                            value = textLimitMax,
                            onValueChange = {
                                errorStateMax.value = it.toFloatOrNull()==null
                                textLimitMax = it.replace(",",".",false)
                                            },
                            enabled = checkedStateLimit.value,
                            label = {Text(text = "Max")},
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon = {
                                if (errorStateMax.value) Icon(Icons.Filled.Warning, contentDescription = "Error", tint = Color.Red)
                            }
                        )
                        OutlinedTextField(
                            value = textLimitMin,
                            onValueChange = {
                                errorStateMin.value = it.toFloatOrNull()==null
                                textLimitMin = it.replace(",",".",false)
                            },
                            enabled = checkedStateLimit.value,
                                    label = {Text(text = "Min")},
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    trailingIcon = {
                                if (errorStateMin.value) Icon(Icons.Filled.Warning, contentDescription = "Error", tint = Color.Red)
                            }
                        )
                    }
                    if (data.type==2) {
                        Row() {
                            Switch(checked = checkedStateSetCounter.value, onCheckedChange = {
                                checkedStateSetCounter.value = it
                            })
                            Text(
                                text = "Set Counter",
                                //style = MaterialTheme.typography.body1.merge(),
                                //modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                        OutlinedTextField(
                            value = textSetCount,
                            onValueChange = {
                                errorStateSetCount.value = it.toLongOrNull()==null
                                textSetCount = it
                            },
                            enabled = checkedStateSetCounter.value,
                            label = {Text(text = "Set")},
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon = {
                                if (errorStateSetCount.value) Icon(Icons.Filled.Warning, contentDescription = "Error", tint = Color.Red)
                            }
                        )


                    }

                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismiss()
                        onSettingChange(DataSetting(
                            id = data.id,
                            description = data.description,
                            visible = checkedStateVisible.value,
                            limitMode = checkedStateLimit.value,
                            limitMax = if (!errorStateMax.value) stringLimittoFlout(textLimitMax) else setting.limitMax,
                            limitMin = if (!errorStateMin.value) stringLimittoFlout(textLimitMin) else setting.limitMin,
                           // setCounter = if(!errorStateSetCount.value&&data.type==2) textSetCount.toLong() else setting.setCounter,
                            //controlMode = if(checkedStateSetCounter.value) data.id else 0
                        ))

                        if (!errorStateSetCount.value&&data.type==2){
                            onValueChange(
                                ControlInfo(
                                id = data.id,
                                    value = textSetCount,
                                    type = data.type
                                ))
                        }

                    }

                    ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(

                    onClick = {
                        onDismiss()
                        //onSettingChange(DataSetting(data.id,false))
                    }
                        //openDialog.value = false
                    ) {
                    Text("CANCEL")
                }
            }
        )
    }

}






@Preview(showBackground = true)
@Composable
fun Test(){
    //DataRow(Data(23,"value","stateDoor",1,"description"))
}
