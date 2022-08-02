package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.domain.model.Data
import com.example.homecontrolssystemv01.domain.model.DataConnect
import com.example.homecontrolssystemv01.presentation.MainViewModel
import com.example.homecontrolssystemv01.presentation.RadioButtonList
import com.example.homecontrolssystemv01.presentation.enums.DataSetting
import com.example.homecontrolssystemv01.ui.theme.Purple200

@Composable
fun SettingScreen(
    listSsid:RadioButtonList,
    dataSetting:DataSetting,
    listData:List<Data>,
    dataConnect:MutableState<DataConnect>,
    onValueChange: (DataSetting) -> Unit,
    pressOnBack: () -> Unit = {}

){

    Scaffold (
        backgroundColor = MaterialTheme.colors.primarySurface,
 //       modifier = Modifier.padding(10.dp),
        topBar = { AppBarSetting(pressOnBack)}){
        val modifierScaffold = Modifier.padding(it)
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.primarySurface)
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row() {
                Text(text = "SSID WIFI ${dataConnect.value.ssidConnect}  ")
                Text(text = "Mode ${dataConnect.value.modeConnect.name}")
            }
            //Text(text = "Дата и время ${listData[0].value}")
            Spacer(modifier = Modifier.size(20.dp))
            MySwitch(dataSetting,onValueChange)
            Spacer(modifier = Modifier.size(20.dp))
            Text(text = "Домашняя SSID WIFI сеть",
                style = MaterialTheme.typography.h6,
 //               modifier = Modifier.padding(16.dp)
                               )
            MyRadioButton(listSsid,dataSetting,onValueChange)
            //MyListData(listData)

        }

    }








}

@Composable
fun MyListData(listData:List<Data>) {
    LazyColumn {
        items(listData) { data ->
            Column() {
                Text(text = data.description)
                Row() {
                    Text(text = data.id.toString())
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(text = data.name.toString())
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(text = data.value.toString())
                }
                Spacer(modifier = Modifier.size(10.dp))

            }

        }
    }
}

@Composable
fun MySwitch(dataSetting: DataSetting,
             onValueChange: (DataSetting) -> Unit)
{
    val checkedState = remember { mutableStateOf(true) }
    checkedState.value = dataSetting.serverMode

    Row(
        Modifier
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Switch(
            checked = checkedState.value ,
            onCheckedChange = {
                checkedState.value = it
                dataSetting.serverMode=it
                onValueChange(dataSetting)
            }

        )
        Text(
            text = "SERVER MODE",
            style = MaterialTheme.typography.body1.merge(),
            modifier = Modifier.padding(start = 16.dp)
        )

    }
}

@Composable
fun MyRadioButton (radioButtonList: RadioButtonList,
                   dataSetting: DataSetting,
                   onValueChange: (DataSetting) -> Unit){

    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(radioButtonList.list[radioButtonList.index])
    }
    Column(
//        Modifier.selectableGroup()
    ) {
        radioButtonList.list.forEach { text ->
            Row(
                Modifier
                    //.fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            onOptionSelected(text)
                            dataSetting.ssid = text
                            onValueChange(dataSetting)
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null
                    // null recommended for accessibility with screenreaders
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.body1.merge(),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }


    }

}

@Composable
fun AppBarSetting(pressOnBack: () -> Unit = {}){

    TopAppBar(
        elevation = 4.dp,
        backgroundColor = Purple200,
        title = {Text(stringResource(R.string.setting))},
        navigationIcon = {
            IconButton(onClick = {pressOnBack()}) {
                Icon(Icons.Filled.ArrowBack, null)
            }
        })

}