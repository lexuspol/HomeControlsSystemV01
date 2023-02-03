package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.presentation.screen.CardSettingElement
import com.example.homecontrolssystemv01.domain.enum.ControlValue
import com.example.homecontrolssystemv01.domain.enum.LogKey
import com.example.homecontrolssystemv01.domain.enum.LoggingType
import com.example.homecontrolssystemv01.domain.model.*
import com.example.homecontrolssystemv01.domain.model.data.DataModel
import com.example.homecontrolssystemv01.domain.model.message.ModeConnect
import com.example.homecontrolssystemv01.domain.model.setting.ConnectSetting
import com.example.homecontrolssystemv01.domain.model.setting.LogSetting
import com.example.homecontrolssystemv01.domain.model.setting.SystemSetting


@Composable
fun SettingScreen(
    connectSetting: ConnectSetting,
    systemSetting: SystemSetting,
    dataList:List<DataModel>?,
    //listLogSetting:List<LogSetting>,
    //getLoggingSetting:() -> List<LogSetting>,
    setConnectSetting: (ConnectSetting) -> Unit,
    setSystemSetting: (SystemSetting) -> Unit,
    //setLoggingSetting: (LogSetting) -> Unit,
    onControl: (ControlInfo) -> Unit,
    pressOnBack: () -> Unit = {}
){


    var ssid = ""
    var connectMode = ""
    var mainDeviceName = ""
    var infoDevice = ""

    var stateSoundOff = ""
    var stateSoundOffUnit = ""

    //val listLoggingSetting = getLoggingSetting()
    val listLoggingSetting = connectSetting.listLogSetting

    val mapDataForLog = mutableMapOf<Int,String>()

    val lastIndexData = 999

    if (!dataList.isNullOrEmpty()){

        dataList.forEach { dataModel->

            if (dataModel.id in 0..lastIndexData){
                if(dataModel.description==""){
                    mapDataForLog[dataModel.id] = dataModel.name.toString()
                }else {
                    mapDataForLog[dataModel.id] = dataModel.description
                }
            }

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

    //var loggingItemEnable by remember { mutableStateOf(!con) }


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

                            val checkedState =
                                remember { mutableStateOf(connectSetting.serverMode) }

                            Text(
                                text = "Запись данных на удаленный сервер",
                                Modifier.weight(4f),
                                style = MaterialTheme.typography.body1
                            )

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

                            Text(
                                text = "Циклическое обновление локальных данных",
                                Modifier.weight(4f),
                                style = MaterialTheme.typography.body1
                            )

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

                            val checkedStateShowDetail =
                                remember { mutableStateOf(systemSetting.showDetails) }

                            Text(
                                text = "Отображение детальной информации",
                                Modifier.weight(4f),
                                style = MaterialTheme.typography.body1
                            )

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
            }
            item{
                CardSettingElement {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        val stateOff = stateSoundOff == stateSoundOffUnit.substringBefore('/')



                        Text(
                            text = "Отключить звуковое оповещение",
                            Modifier.weight(4f),
                            style = MaterialTheme.typography.body1
                        )

                        Switch(
                            checked = stateOff,
                            onCheckedChange = {
                                onControl(
                                    ControlInfo(
                                        DataID.buttonSoundOff.id,
                                        if (stateOff) ControlValue.SOUND_ON.value else ControlValue.SOUND_OFF.value
                                    )
                                )
                                //    enableControl = false

                            },
                            Modifier.weight(1f),
                            enabled = enableControl
                        )
                    }

                }
            }

            item { CardSettingElement {
                Column {
                    listLoggingSetting.forEach {
                        MyDropDownMenu(connectSetting.serverMode,it, mapDataForLog.toMap(),
                            setLoggingSetting ={fromMenu->
                            connectSetting.listLogSetting.map { logSetting->

                                if (logSetting.logKey==fromMenu.logKey){
                                    logSetting.logId = fromMenu.logId
                                }


                            }
                        })
                    }
                }
            } }




//                items(listLoggingSetting) {
//                    CardSettingElement {
//                    MyDropDownMenu(it, mapDataForLog.toMap(), setLoggingSetting)
//                }
//            }
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



// Creating a composable function
// to create an Outlined Text Field
// Calling this function as content
// in the above function
@Composable
fun MyDropDownMenu(
    //dataList:List<DataModel>,
    serverMode:Boolean,
    logSetting:LogSetting,
    mapData:Map<Int,String>,
    setLoggingSetting: (LogSetting) -> Unit,
){
    val logType = try {
        LogKey.valueOf(logSetting.logKey).type
    }catch (e:Exception){
        LoggingType.UNDEFINED
    }

    val nameLogging = when(logType){
        LoggingType.LOGGING_PERIODIC -> "Logging"
        LoggingType.LOGGING_ONE_DAY -> "LoggingLastDay"
        else -> "NotType"
    }

    // Declaring a boolean value to store
    // the expanded state of the Text Field
    var mExpanded by remember { mutableStateOf(false) }

    // Create a list of cities
   // val list = listOf("Delhi", "Mumbai", "Chennai", "Kolkata", "Hyderabad", "Bengaluru", "Pune")

    // Create a string value to store the selected city
    //val valueSelectedText = if (logSetting.logId=0) "" else logSetting.logKey
    var mSelectedText by remember { mutableStateOf(mapData[logSetting.logId] ?:"") }

    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}

    // Up Icon when expanded and down icon when collapsed
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(Modifier.padding(10.dp)) {

        // Create an Outlined Text Field
        // with icon and not expanded
        OutlinedTextField(
            value = mSelectedText,
            onValueChange = {
                mSelectedText = it
                            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    mTextFieldSize = coordinates.size.toSize()
                },
            enabled = !serverMode,
            label = {Text(nameLogging)},
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier.clickable { mExpanded = !mExpanded })
            }
        )

        // Create a drop-down menu with list of cities,
        // when clicked, set the Text Field text as the city selected
        DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current){mTextFieldSize.width.toDp()})
        ) {
            mapData.forEach { map ->
                DropdownMenuItem(onClick = {
                    mSelectedText = map.value
                    mExpanded = false
                    setLoggingSetting(LogSetting(map.key,logSetting.logKey,logType))
                }) {
                    Text(text = map.value)
                }
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