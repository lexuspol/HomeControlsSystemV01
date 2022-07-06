package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.domain.model.Data
import com.example.homecontrolssystemv01.presentation.MainViewModel
import com.example.homecontrolssystemv01.presentation.RadioButtonList
import com.example.homecontrolssystemv01.presentation.enums.DataSetting

@Composable
fun SettingData(
    modifier: Modifier = Modifier,
    listSsid:RadioButtonList,
    dataSetting: DataSetting,
    onValueChange: (DataSetting) -> Unit

){

    Column {
        MySwitch(dataSetting,onValueChange)
        Spacer(modifier = Modifier.size(20.dp))
        Text(text = "Домашняя SSID WIFI сеть",
            style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(16.dp))
        MyRadioButton(listSsid,dataSetting,onValueChange)
    }
}

@Composable
fun MySwitch(dataSetting: DataSetting,
             onValueChange: (DataSetting) -> Unit) {
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