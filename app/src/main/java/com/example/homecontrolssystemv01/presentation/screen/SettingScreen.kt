package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.domain.enum.ControlValue
import com.example.homecontrolssystemv01.domain.model.*
import com.example.homecontrolssystemv01.domain.model.data.DataModel
import com.example.homecontrolssystemv01.domain.model.message.ModeConnect
import com.example.homecontrolssystemv01.presentation.RadioButtonList
import com.example.homecontrolssystemv01.domain.model.setting.ConnectSetting
import com.example.homecontrolssystemv01.domain.model.setting.SystemSetting


@Composable
fun SettingScreen(
    connectSetting: ConnectSetting,
    systemSetting: SystemSetting,
    dataList:List<DataModel>?,
    setConnectSetting: (ConnectSetting) -> Unit,
    setSystemSetting: (SystemSetting) -> Unit,
    onControl: (ControlInfo) -> Unit,
    pressOnBack: () -> Unit = {}
){


    var ssid = ""
    var connectMode = ""
    var mainDeviceName = ""
    var infoDevice = ""

    var stateSoundOff = ""
    var stateSoundOffUnit = ""

    if (!dataList.isNullOrEmpty()){


        dataList.forEach { dataModel->

            when(dataModel.id){
                DataID.SSID.id -> ssid = dataModel.value.toString()
                DataID.connectMode.id -> connectMode = dataModel.value.toString()
                DataID.mainDeviceName.id -> {
                    mainDeviceName = dataModel.value.toString()
                }
                DataID.deviceInfo.id -> infoDevice = dataModel.value.toString()
                DataID.stateSoundOff.id -> {
                    stateSoundOff = dataModel.value.toString()
                    stateSoundOffUnit = dataModel.unit
                }

            }

        }
    }

    var enableControl = connectMode== ModeConnect.LOCAL.name


    Scaffold (
        backgroundColor = MaterialTheme.colors.primarySurface,
 //       modifier = Modifier.padding(10.dp),
        topBar = { AppBarSetting(pressOnBack)})
    {
            padding ->
        //val modifierScaffold = Modifier.padding(padding)
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colors.primarySurface)
                .fillMaxSize()
                .padding(10.dp)
        ) {

            item {

                CardSettingElement {
                    val modifierSet = Modifier.padding(5.dp)
                    Column(
                        modifier = modifierSet,
                        horizontalAlignment = Alignment.Start
                    ) {

                        Text(
                            text = "Имя текущей сети - $ssid",
                            modifier = modifierSet,
                            style = MaterialTheme.typography.body1
                        )
                        Text(
                            text = "Имя локальной сети - ${connectSetting.ssid}",
                            modifier = modifierSet,
                            style = MaterialTheme.typography.body1
                        )
                        Button(
                            onClick = {
                                connectSetting.ssid = ssid
                                setConnectSetting(connectSetting)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                           // colors = ButtonDefaults.buttonColors(backgroundColor = Purple700),

                            ) {
                            Text(
                                text = "Сделать текущую сеть локальной",
                                style = MaterialTheme.typography.body1
                            )
                        }

                    }
                }

            }

            item {
                CardSettingElement {
                    val modifierSet = Modifier.padding(5.dp)
                    Column(
                        modifier = modifierSet,
                        horizontalAlignment = Alignment.Start
                    ) {

                        Text(
                            text = "Имя устройства - $infoDevice",
                            modifier = modifierSet,
                            style = MaterialTheme.typography.body1
                        )
                        Text(
                            text = "Имя главного устройства - $mainDeviceName",
                            modifier = modifierSet,
                            style = MaterialTheme.typography.body1
                        )
                        Button(
                            onClick = {
                                onControl(ControlInfo(DataID.mainDeviceName.id,infoDevice))
                 //               enableControl = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            enabled = enableControl,
                           // colors = ButtonDefaults.buttonColors(backgroundColor = Purple700),

                            ) {
                            Text(
                                text = "Сделать устройство главным",
                                style = MaterialTheme.typography.body1
                            )
                        }

                    }
                }

            }

            item {
                CardSettingElement {

                    Column() {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            val checkedState = remember { mutableStateOf(connectSetting.serverMode) }

                            Text(text = "Запись данных на удаленный сервер",
                                Modifier.weight(4f),
                                style = MaterialTheme.typography.body1)

                            Switch(
                                checked = checkedState.value,
                                onCheckedChange = { state ->
                                    checkedState.value = state
                                    connectSetting.serverMode = state
                                    setConnectSetting(connectSetting)
                                },
                                Modifier.weight(1f)
                            )

                        }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp),

                            verticalAlignment = Alignment.CenterVertically,
                            //horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            val checkedState = remember { mutableStateOf(connectSetting.cycleMode) }

                            Text(text = "Циклическое обновление локальных данных",
                                Modifier.weight(4f),
                                style = MaterialTheme.typography.body1)

                            Switch(

                                checked = checkedState.value,
                                onCheckedChange = { state ->
                                    checkedState.value = state
                                    connectSetting.cycleMode = state
                                    setConnectSetting(connectSetting)
                                },
                                Modifier.weight(1f)
                            )

                        }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            val checkedStateShowDetail = remember { mutableStateOf(systemSetting.showDetails) }

                            Text(text = "Отображение детальной информации",
                                Modifier.weight(4f),
                                style = MaterialTheme.typography.body1)

                            Switch(
                                checked = checkedStateShowDetail.value,
                                onCheckedChange = { state ->
                                    checkedStateShowDetail.value = state
                                    systemSetting.showDetails = state
                                    setSystemSetting(systemSetting)
                                },
                                Modifier.weight(1f)
                            )

                        }
                    }
                }

                CardSettingElement {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        val stateOff = stateSoundOff == stateSoundOffUnit.substringBefore('/')



                        Text(text = "Отключить звуковое оповещение",
                            Modifier.weight(4f),
                            style = MaterialTheme.typography.body1)

                        Switch(
                            checked = stateOff ,
                            onCheckedChange = {
                                    onControl(ControlInfo(DataID.buttonSoundOff.id,
                                        if (stateOff) ControlValue.SOUND_ON.value else ControlValue.SOUND_OFF.value))
                            //    enableControl = false

                            },
                            Modifier.weight(1f),
                            enabled = enableControl
                        )
                    }

                }
            }




            }

        }

}

@Composable
fun MyListData(listData:List<DataModel>) {
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
fun MyRadioButton (radioButtonList: RadioButtonList,
                   connectSetting: ConnectSetting,
                   onValueChange: (ConnectSetting) -> Unit){

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
                            connectSetting.ssid = text
                            onValueChange(connectSetting)
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
        //backgroundColor = Purple200,
        title = {Text(stringResource(R.string.setting))},
        navigationIcon = {
            IconButton(onClick = {pressOnBack()}) {
                Icon(Icons.Filled.ArrowBack, null)
            }
        })

}