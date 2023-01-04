package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.domain.enum.DataType
import com.example.homecontrolssystemv01.domain.model.*
import com.example.homecontrolssystemv01.domain.model.data.DataContainer
import com.example.homecontrolssystemv01.domain.model.data.DataModel
import com.example.homecontrolssystemv01.domain.model.message.Message
import com.example.homecontrolssystemv01.domain.model.setting.DataSetting
import com.example.homecontrolssystemv01.domain.model.setting.SystemSetting
import com.example.homecontrolssystemv01.util.convertIntToBinaryString

import com.example.homecontrolssystemv01.util.giveDataById
import com.example.homecontrolssystemv01.util.stringLimittoFlout
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun DataListScreen(
    modifier:Modifier,
    listDataContainer:MutableList<DataContainer>,
    messageList:List<Message>?,
    systemSetting: SystemSetting,
    onSettingChange: (DataSetting) -> Unit,
    onControl: (ControlInfo) -> Unit,//запись счетчиков
    onLoadData:() -> Unit,
    deleteData: (Int) -> Unit
){

    val showDetails = systemSetting.showDetails


    var refreshing = false
    if (!messageList.isNullOrEmpty()){
        val description = messageList.find { it.id == DataID.completeUpdate.id }?.description
        refreshing = description == DataID.completeUpdate.name
    //записывам START во ViewModel. Сбразываем в Worker после обновления данных
    }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = refreshing),
            onRefresh = {
                onLoadData()
            }
        ) {
            LazyColumnCreate(
                modifier,listDataContainer,showDetails,
                onSettingChange, onControl,deleteData)
        }
}

@Composable
fun LazyColumnCreate(
    modifier:Modifier,
    listDataContainer:MutableList<DataContainer>,
    showDetails:Boolean,
    onSettingChange: (DataSetting) -> Unit,
    onControl: (ControlInfo) -> Unit,
    deleteData: (Int) -> Unit
)

{

    val lastIndexData = 999

    var allList by remember { mutableStateOf(false)}

    Column(modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Box(modifier = Modifier.weight(6f)){
            LazyColumn {
                items(listDataContainer){ container ->



                    if (allList){
                        if (showDetails) {
                            DataRow(modifier,container,onSettingChange,onControl,deleteData,true)
                        }else {
                            if (container.id in 0..lastIndexData) {
                                DataRow(modifier,container,onSettingChange,onControl,deleteData)
                            }
                        }
                    }else{
                        if (container.setting.visible) {
                            DataRow(modifier,container,onSettingChange,onControl,deleteData)
                        }

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
                        text = "${giveDataById(listDataContainer, DataID.lastTimeUpdate.id,).dataModel.value}",
                        // Modifier.padding(start = 5.dp),
                        style = MaterialTheme.typography.subtitle1
                    )
                    Text(
                        text = "${giveDataById(listDataContainer, DataID.connectMode.id,).dataModel.value}",
                        // Modifier.padding(start = 5.dp),
                        style = MaterialTheme.typography.subtitle1
                    )
                    Button(
                        onClick = {
                            allList = !allList
                                  },
                        // Modifier.padding(end = 5.dp),
                      //  colors = ButtonDefaults.buttonColors(backgroundColor = Purple700)
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
fun DataRow(
    modifier:Modifier,
    dataContainer: DataContainer,
    onSettingChange: (DataSetting) -> Unit,
    onValueChange: (ControlInfo) -> Unit,
    deleteData: (Int) ->Unit,
    showDetails:Boolean = false){

    val data = dataContainer.dataModel
    val setting = dataContainer.setting

    val showDialog = remember { mutableStateOf(false)}

    val colorBorder = if (setting.visible || setting.limitMode) Color.Yellow else MaterialTheme.colors.background

    Card(
        modifier = Modifier
            .padding(8.dp, 4.dp)
            .fillMaxWidth(),
            // .background(Purple500)
   //         .height(50.dp),
        shape = RoundedCornerShape(8.dp), elevation = 4.dp,
        border = BorderStroke(1.dp, colorBorder)

        //contentColor = Purple500,
        //backgroundColor = Purple500
    ) {
        if (showDialog.value){
            MyAlertDialog(modifier,data,
                setting,
                showDialog = showDialog.value,
                onDismiss = {showDialog.value = false},
                onSettingChange,
            onValueChange,
                deleteData,
            showDetails)
            }


        Surface(
 //           modifier = Modifier.background(Purple500),
         //   color = Purple700
        ) {
            
            Column(
                Modifier
                    .padding(10.dp)
                    .clickable {
                        showDialog.value = true
                    }) {
                Row(
                    Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = data.description,
                        modifier = Modifier.weight(4f),
                        style = MaterialTheme.typography.subtitle1,
                        //color = if (setting.visible) Color.Yellow else Color.White
                    )
                    Text(
                        text = when(data.type){
                            DataType.STRING.int -> "  "
                            DataType.DTL.int -> "  "
                            DataType.WORD.int -> "  "
                            else -> data.value.toString()
                        },
                        modifier = Modifier.weight(2f),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                       // color = if (setting.limitMode) Color.Yellow else Color.White
                    )
                    Text(
                        text = if (data.type!=DataType.BOOL.int) data.unit else "  ",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.subtitle1,
                       // color = if (setting.limitMode) Color.Yellow else Color.White
                    )


                }
                if (data.type == DataType.STRING.int||
                    data.type == DataType.DTL.int||
                    data.type == DataType.WORD.int)  {
                    Row(){
                        Text(
                            text = data.value.toString(),
                            style = MaterialTheme.typography.subtitle2,
                        )
                    }
                }



            }



        }
    }
}



@Composable
private fun MyAlertDialog(
    modifier:Modifier,
    dataModel: DataModel,
    setting: DataSetting,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSettingChange: (DataSetting) -> Unit,
    onValueChange: (ControlInfo) -> Unit,
    deleteData: (Int) ->Unit,
    showDetails: Boolean
) {

    val len = 16//количество bit в сообщении

    val checkedStateVisible = remember { mutableStateOf(setting.visible) }
    val checkedStateLimit = remember { mutableStateOf(setting.limitMode) }
    val checkedStateSetCounter = remember { mutableStateOf(false) }

    var textLimitMax by remember { mutableStateOf(setting.limitMax.toString()) }
    var textLimitMin by remember { mutableStateOf(setting.limitMin.toString()) }
    val errorStateMax = remember { mutableStateOf(false) }
    val errorStateMin = remember { mutableStateOf(false) }

    var textSetCount by remember { mutableStateOf(dataModel.value.toString()) }
    val errorStateSetCount = remember { mutableStateOf(false) }

    val checkedStateDeleteData = remember { mutableStateOf(false) }

    val checkedStateWarning_0 = remember { mutableStateOf(setting.limitMin == 1f) }
    val checkedStateWarning_1 = remember { mutableStateOf(setting.limitMax == 1f) }

    val switchRem =

        remember { mutableStateOf(convertIntToBinaryString(setting.limitMax.toInt(), len)) }

    if (showDialog) {

    Box() {



                    AlertDialog(

                        onDismissRequest = onDismiss
                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                        // button. If you want to disable that functionality, simply use an empty
                        // onCloseRequest.
                        //openDialog.value = false
                        ,

                        title = {

                            if (dataModel.type == DataType.STRING.int ||
                                dataModel.type == DataType.DTL.int ||
                                dataModel.type == DataType.WORD.int
                            ) {
                                Column() {
                                    Text(text = dataModel.description)
                                    Text(text = dataModel.value.toString())
                                }
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = dataModel.description)
                                    Text(text = dataModel.value.toString())
                                    Text(text = if (dataModel.type != DataType.BOOL.int) dataModel.unit else "  ")
                                }
                            }

                        },
                        text = {
                            Column() {

                                if (showDetails) {
                                    Box(
                                        modifier = Modifier
                                            // .fillMaxWidth()
                                            .padding(10.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                            //   .fillMaxWidth()
                                            //                               .padding(10.dp)
                                        ) {

                                            Row(
                                                //Modifier.padding(5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = "ID: ${dataModel.id} ")

                                                Checkbox(
                                                    checked = checkedStateDeleteData.value,
                                                    onCheckedChange = { checkedStateDeleteData.value = it },
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = Color(0xff, 0xb6, 0xc1),
                                                        checkmarkColor = Color.Red
                                                    )
                                                )
                                                Text(text = "Delete Data")
                                            }

                                            Text(text = "Name: ${dataModel.name}")
                                            Text(text = "Type: ${dataModel.type}")


                                        }
                                    }


                                }



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

                                if (dataModel.type == DataType.BOOL.int) {

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

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Warning")
                                        Checkbox(
                                            checked = checkedStateWarning_1.value,
                                            enabled = checkedStateLimit.value,
                                            onCheckedChange = {
                                                checkedStateWarning_0.value = false
                                                checkedStateWarning_1.value = it
                                            }
                                        )
                                        Text(text = "=${dataModel.unit.substringBefore('/')}")
                                        Checkbox(
                                            checked = checkedStateWarning_0.value,
                                            enabled = checkedStateLimit.value,
                                            onCheckedChange = {
                                                checkedStateWarning_1.value = false
                                                checkedStateWarning_0.value = it
                                            }
                                        )
                                        Text(text = "=${dataModel.unit.substringAfter('/')}")


                                    }
                                }



                                if (dataModel.type == DataType.REAL.int) {
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
                                            errorStateMax.value = it.toFloatOrNull() == null
                                            textLimitMax = it.replace(",", ".", false)
                                        },
                                        enabled = checkedStateLimit.value,
                                        label = { Text(text = "Max") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        trailingIcon = {
                                            if (errorStateMax.value) Icon(
                                                Icons.Filled.Warning,
                                                contentDescription = "Error",
                                                tint = Color.Red
                                            )
                                        }
                                    )
                                    OutlinedTextField(
                                        value = textLimitMin,
                                        onValueChange = {
                                            errorStateMin.value = it.toFloatOrNull() == null
                                            textLimitMin = it.replace(",", ".", false)
                                        },
                                        enabled = checkedStateLimit.value,
                                        label = { Text(text = "Min") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        trailingIcon = {
                                            if (errorStateMin.value) Icon(
                                                Icons.Filled.Warning,
                                                contentDescription = "Error",
                                                tint = Color.Red
                                            )
                                        }
                                    )
                                }
                                if (dataModel.type == DataType.DINT.int) {
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
                                            errorStateSetCount.value = it.toLongOrNull() == null
                                            textSetCount = it
                                        },
                                        enabled = checkedStateSetCounter.value,
                                        label = { Text(text = "Set") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        trailingIcon = {
                                            if (errorStateSetCount.value) Icon(
                                                Icons.Filled.Warning,
                                                contentDescription = "Error",
                                                tint = Color.Red
                                            )
                                        }
                                    )


                                }

                                //WORD
                                if (dataModel.type == DataType.WORD.int && dataModel.listString.isNotEmpty()) {

                                    val listMessageDescription = dataModel.listString
                                    val lastIndex = listMessageDescription.size - 1
                                    //последнее сообщение - это общее системное сообщение


                                    // val switch = setting.limitMax.toInt()
                                    // val switchBin = convertIntToBinaryString(switch,16)

                                    LazyColumn(Modifier.fillMaxHeight(0.8f)) {
                                        itemsIndexed(
                                            dataModel.listString.subList(
                                                0,
                                                lastIndex
                                            )
                                        ) { index, string ->
                                            Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                            ) {

                                                Text(
                                                    text = string,
                                                    //style = MaterialTheme.typography.body1.merge(),
                                                    //modifier = Modifier.padding(start = 16.dp)
                                                )

                                                Switch(
                                                    checked = if (switchRem.value.isNotEmpty()) switchRem.value[index] == '1' else false,
                                                    onCheckedChange = { booleon ->
                                                        val char = if (booleon) '1' else '0'
                                                        val sb = StringBuilder(switchRem.value).also {
                                                            it.setCharAt(
                                                                index,
                                                                char
                                                            )
                                                        }
                                                        switchRem.value = sb.toString()
                                                    },
                                                    enabled = switchRem.value.isNotEmpty()
                                                )

                                            }
                                        }
                                    }


                                }

                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    onDismiss()

                                    var limitMax = setting.limitMax

                                    when (dataModel.type) {
                                        DataType.REAL.int -> {
                                            if (!errorStateMax.value) limitMax =
                                                stringLimittoFlout(textLimitMax)
                                        }
                                        DataType.BOOL.int -> {
                                            limitMax = if (checkedStateWarning_1.value) 1f else 0f
                                        }
                                        DataType.WORD.int -> {
                                            limitMax = switchRem.value.toInt(2).toFloat()// сделать проверку
                                        }
                                    }

                                    var limitMin = setting.limitMin

                                    when (dataModel.type) {
                                        DataType.REAL.int -> {
                                            if (!errorStateMin.value) limitMin =
                                                stringLimittoFlout(textLimitMin)
                                        }
                                        DataType.BOOL.int -> {
                                            limitMin = if (checkedStateWarning_0.value) 1f else 0f
                                        }
                                    }


                                    onSettingChange(
                                        DataSetting(
                                            id = dataModel.id,
                                            description = dataModel.description,
                                            visible = checkedStateVisible.value,
                                            limitMode = checkedStateLimit.value,
                                            limitMax = limitMax,
                                            limitMin = limitMin,
                                            unit = dataModel.unit
                                            // setCounter = if(!errorStateSetCount.value&&data.type==2) textSetCount.toLong() else setting.setCounter,
                                            //controlMode = if(checkedStateSetCounter.value) data.id else 0
                                        )
                                    )

                                    if (!errorStateSetCount.value && dataModel.type == DataType.DINT.int) {
                                        onValueChange(
                                            ControlInfo(
                                                id = dataModel.id,
                                                value = textSetCount,
                                                type = dataModel.type
                                            )
                                        )
                                    }

                                    if (checkedStateDeleteData.value) {

                                        deleteData(dataModel.id)

                                        // Log.d("HCS_TEST","delete = $checkedStateDeleteData")
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
                        },
                        //modifier = Modifier.padding(20.dp)
                    )
                }







}

}






@Preview(showBackground = true)
@Composable
fun Test(){


}
