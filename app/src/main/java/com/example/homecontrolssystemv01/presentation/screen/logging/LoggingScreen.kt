//package com.example.homecontrolssystemv01.presentation.screen.logging
//
//import android.util.Log
//import androidx.annotation.StringRes
//import androidx.compose.foundation.*
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.rotate
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.example.homecontrolssystemv01.R
//import com.example.homecontrolssystemv01.domain.enum.DataType
//import com.example.homecontrolssystemv01.domain.enum.LoggingType
//import com.example.homecontrolssystemv01.presentation.ResourcesString
//import com.example.homecontrolssystemv01.domain.model.logging.LogItem
//import com.example.homecontrolssystemv01.domain.model.logging.LoggingID
//import com.example.homecontrolssystemv01.presentation.screen.CardSettingElement
//import com.example.homecontrolssystemv01.util.convertLongToTime
//import com.example.homecontrolssystemv01.util.convertMillisecondsToTime
//import kotlin.math.*
//import java.util.*
//
//@Composable
//fun LoggingScreen(
//    resourcesDataMapUI: Map<Int, ResourcesString.Data>,
//    getLogIdListRev1: () -> MutableState<List<LoggingID>>,
//    getLogIdList: () -> MutableState<List<String>>,
//    getLogRev1: (logKey: LoggingID) -> MutableState<Map<String, String>>,
//    getLog: (idKey: String) -> MutableState<Map<String, LogItem>>,
//    deleteLogItem: (idKey: String) -> Unit,
//    pressOnBack: () -> Unit = {}
//) {
//
//
//    //val logId = remember { mutableStateOf("0")}
//    val logId = remember { mutableStateOf("0") }
//    val logDescription = remember { mutableStateOf("") }
//    val logUnit = remember { mutableStateOf("") }
//
//    val logIdList = remember { getLogIdList() }// чтобы не вызывало много раз
//
//    val logIdListRev1 = remember { getLogIdListRev1() }
////    if (!loggingList.isNullOrEmpty()){
////        loggingList.forEach { item->
////
////            if (item.itemId != nextId.value){
////                return@forEach
////            }
////            nextId.value = item.itemId + 1
////       }
////    }
//
//    val showDialog = remember { mutableStateOf(false) }
//    val showLog = remember { mutableStateOf(false) }
//
//    if (logIdList.value.isNotEmpty() && !showLog.value) {
//        showDialog.value = true
//    }
//
//    if (showDialog.value) {
//        LoggingAlertDialog(resourcesDataMapUI, logIdList.value,
//            onDismiss = { showDialog.value = false },
//            getLog = fun(key: String, description: String, unit: String) {
//                logId.value = key
//                logDescription.value = description
//                logUnit.value = unit
//                showLog.value = true
//            }
//
//            // logId.value = it
//        )
//    }
//
//
//    Scaffold(
//        backgroundColor = MaterialTheme.colors.primarySurface,
//        topBar = {
//            AppBarLogging(onDialog = {
//                showLog.value = false
//                showDialog.value = true
//            }, pressOnBack)
//        }
//    ) { padding ->
//
//
//        if (showLog.value) {
//
//            val logMap = remember { getLog(logId.value) }//чтобы не вызывалось много раз
//
//
//            if (logMap.value.isNotEmpty()) {
//
//
//                val key = logId.value
//                val itemMap = logMap.value[key]
//                var maxValue: Float? = null
//                var minValue: Float? = null
//                var averageValue: Float? = null
//
//                if (itemMap != null) {
//                    val calendar = Calendar.getInstance()
//
//                    try {
//                        val listValue = mutableListOf<Float>()
//                        itemMap.value.forEach {
//                            listValue.add(it.second.toFloat())
//
//                        }
//
//                        maxValue = listValue.maxOrNull()
//                        minValue = listValue.minOrNull()
//                        averageValue = (listValue.average() * 100).roundToInt().toFloat() / 100
//
//                        //Log.d("HCS","max= $max, min = $min, average = $aver")
//
//                    } catch (e: java.lang.Exception) {
//                        Log.d("HCS", e.toString())
//                    }
//                    LazyColumn(modifier = Modifier.padding()) {
//
//                        item {
//
//                            CardSettingElement {
//
//                                Column(Modifier.fillMaxSize()) {
//                                    Text(
//                                        text = logDescription.value,
//                                        modifier = Modifier.padding(10.dp),
//                                        style = MaterialTheme.typography.h6
//                                    )
//                                    if (maxValue != null && minValue != null && averageValue != null) {
//
//                                        LoggingRowValue(
//                                            text = stringResource(R.string.maxValue),
//                                            value = maxValue.toString(), unit = logUnit.value
//                                        )
//                                        LoggingRowValue(
//                                            text = stringResource(R.string.minValue),
//                                            value = minValue.toString(), unit = logUnit.value
//                                        )
//                                        LoggingRowValue(
//                                            text = stringResource(R.string.averageValue),
//                                            value = averageValue.toString(), unit = logUnit.value
//                                        )
//
//                                    }
//
//                                }
//
//
//                            }
//                        }
//                        items(itemMap.value) {
//                            LoggingItemRow(it, logUnit.value, calendar)
//                        }
//
//                        item {
//                            Button(
//                                onClick = {
//                                    deleteLogItem(logId.value)
//                                    showLog.value = false
//                                },
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(10.dp),
//                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background)
//                            ) {
//                                Text(text = "DELETE LOG", style = MaterialTheme.typography.h6)
//
//                            }
//                        }
//                    }
//                }
//
//            }
//
//
//        }
//
//
//    }
//}
//
//@Composable
//fun LazyColumn() {
//
//
//}
//
//@Composable
//fun LoggingItemRow(
//    logValue: Pair<Long, String>, unit: String, calendar: Calendar
//    // addItem:(ShopItem)-> Unit,deleteItem:(Int) -> Unit
//) {
//
//    Card(
//        modifier = Modifier
//            .padding(10.dp, 5.dp)
//            .fillMaxWidth(),
//        shape = RoundedCornerShape(8.dp), elevation = 4.dp,
//        border = BorderStroke(1.dp, MaterialTheme.colors.background),
//        backgroundColor = MaterialTheme.colors.primarySurface
//    ) {
//
//        LoggingRowValue(
//            text = convertLongToTime(logValue.first),
//            value = logValue.second, unit = unit
//        )
//
////            Row(modifier = Modifier
////                .padding(10.dp)
////                .fillMaxSize(),
////                verticalAlignment = Alignment.CenterVertically,
////                horizontalArrangement = Arrangement.SpaceBetween) {
////
////                Text(text = convertLongToTime(logValue.first),style = MaterialTheme.typography.subtitle1)
////                Text(text = "${logValue.second} $unit",style = MaterialTheme.typography.subtitle1)
////            }
//    }
//}
//
//@Composable
//fun LoggingAlertDialog(
//    resourcesDataMapUI: Map<Int, ResourcesString.Data>,
//    listIdLog: List<String>,
//    onDismiss: () -> Unit,
//    getLog: (key: String, description: String, unit: String) -> Unit,
//) {
//
//    AlertDialog(onDismissRequest = onDismiss,
//        title = {
//        },
//        text = {
//            Column(Modifier.padding(10.dp)) {
//
//                listIdLog.forEach { idKey ->
//
//                    val idInt = try {
//                        idKey.substringBefore(LoggingType.LOGGING_PERIODIC.separator).toInt()
//                    } catch (e: Exception) {
//                        0
//                    }
//
//                    if (idInt != 0) {
//                        val description = resourcesDataMapUI[idInt]?.description ?: idKey
//                        val unit = resourcesDataMapUI[idInt]?.unit ?: ""
//
//                        Card(
//                            modifier = Modifier
//                                .padding(10.dp)
//                                .fillMaxWidth()
//                                .clickable {
//                                    // colorTitle = number.second
//                                    getLog(idKey, description, unit)
//                                    onDismiss()
//                                },
//                            shape = RoundedCornerShape(8.dp), elevation = 4.dp
//                        ) {
//                            Text(
//                                text = description,
//                                modifier = Modifier.padding(10.dp),
//                                style = MaterialTheme.typography.subtitle1,
//                                //fontSize = 24.sp,
//                                //textAlign = TextAlign.Center,
//                            )
//                        }
//                    }//end if
//                }//end for
//            }
//        },
//        confirmButton = {
//        }
//        // backgroundColor =
//    )
//}
//
//
//@Composable
//fun AppBarLogging(
//    //connectSetting: ConnectSetting,
//    onDialog: () -> Unit, pressOnBack: () -> Unit = {}
//) {
//    TopAppBar(
//        elevation = 4.dp,
//        //backgroundColor = Purple200,
//    ) {
//        Row(
//            modifier = Modifier.fillMaxSize(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween,
//        ) {
//            IconButton(onClick = { pressOnBack() }) {
//                Icon(Icons.Filled.ArrowBack, null)
//            }
//            Text(stringResource(R.string.logging))
//
//
//            IconButton(onClick = {
//                onDialog()
//            }) {
//                Icon(Icons.Filled.List, null)
//            }
//
//
//        }
//    }
//}
//
//@Composable
//fun CustomChart(
//    barValue: List<Float>,
//    xAxisScale: List<String>,
//    start_amount: Int,
//    end_amount: Int
//) {
//    // val context = LocalContext.current
//    // BarGraph Dimensions
//    val barGraphHeight by remember { mutableStateOf(200.dp) }
//    val barGraphWidth by remember { mutableStateOf(10.dp) }
//    // Scale Dimensions
//    val scaleYAxisWidth by remember { mutableStateOf(40.dp) }
//    val scaleLineWidth by remember { mutableStateOf(2.dp) }
//
//    val amount = abs(start_amount) + abs(end_amount)
//
//    Column(
//        modifier = Modifier
//            .padding(10.dp)
//            .fillMaxSize(),
//        verticalArrangement = Arrangement.Top
//    ) {
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(barGraphHeight),
//            verticalAlignment = Alignment.Bottom,
//            horizontalArrangement = Arrangement.Start
//        ) {
//            // scale Y-Axis
//            Box(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .width(scaleYAxisWidth),
//                contentAlignment = Alignment.BottomCenter
//            ) {
//
//                Column(
//                    modifier = Modifier.fillMaxHeight(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.SpaceBetween
//                ) {
//
//                    val ser = start_amount + amount / 2
//
//                    Text(text = end_amount.toString())
//                    Text(text = ser.toString())
//                    Text(text = start_amount.toString())
//                }
//
//            }
//
//            // Y-Axis Line
//            Box(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .width(scaleLineWidth)
//                    .background(Color.Black)
//            )
//
//            Box(
//                //modifier = Modifier.fillMaxHeight()
//            ) {
//
//                Box(
//                ) {
//                    Column(
//                        Modifier.fillMaxSize(),
//                        verticalArrangement = Arrangement.SpaceBetween
//
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(scaleLineWidth / 2)
//                                .background(Color.Gray)
//                        )
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(scaleLineWidth / 2)
//                                .background(Color.Gray)
//                        )
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(scaleLineWidth / 2)
//                                .background(Color.Gray)
//                        )
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(scaleLineWidth / 2)
//                                .background(Color.Gray)
//                        )
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(scaleLineWidth / 2)
//                                .background(Color.Gray)
//                        )
//                    }
//                }
//
//
//                Box(
//                    // modifier = Modifier.fillMaxHeight()
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(barGraphHeight),
//                        verticalAlignment = Alignment.Bottom,
//                        horizontalArrangement = Arrangement.Start
//
//                    ) {
//                        barValue.forEach {
//
//                            val height = (it + amount - end_amount) / amount
//
//                            //Column() {
//                            Box(
//                                modifier = Modifier
//                                    .padding(
//                                        start = barGraphWidth,
//                                        //    bottom = 5.dp
//                                    )
//                                    //          .clip(CircleShape)
//                                    .width(barGraphWidth)
//                                    .fillMaxHeight(height)
//                                    .background(color = Color.Red)
//                                    .clickable {
////                            Toast
////                                .makeText(context, it.toString(), Toast.LENGTH_SHORT)
////                                .show()
//                                    }
//                            )
//
//
//                            // }//
//                        }
//                    }
//                }
//            }
//            // graph
//        }
//        // X-Axis Line
////        Box(
////            modifier = Modifier
////                .fillMaxWidth()
////                .height(scaleLineWidth)
////                .background(Color.Black)
////        )
//        // Scale X-Axis
//        Row(
//            modifier = Modifier
//                .padding(start = scaleYAxisWidth)
//                .fillMaxWidth(),
//            //horizontalArrangement = Arrangement.spacedBy(barGraphWidth)
//        ) {
//            xAxisScale.forEach {
//                Text(
//                    modifier = Modifier
//                        .padding(start = barGraphWidth * 0.6f)
//                        //.width(barGraphWidth)
//                        .rotate(-90f),
//                    text = it,
//                    textAlign = TextAlign.Center
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun LoggingRowValue(text: String, value: String, unit: String) {
//
////   val textValue =  if (dataType==DataType.TIME){
////        convertMillisecondsToTime(value)
////    }else {
////        "$value $unit"
////   }
//
//
//    Row(
//        modifier = Modifier
//            .padding(10.dp)
//            .fillMaxSize(),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//
//        Text(text = text, style = MaterialTheme.typography.subtitle1)
//        Text(text = value, style = MaterialTheme.typography.subtitle1)
//    }
//
//
//}
//
//@Preview(showBackground = true)
//@Composable
//fun TestPreviwLogging() {
//    CustomChart(
//        barValue = listOf(2f, 2.6f, 10f, -2f, 5f, -5f, -10f, 1.2f, 2.7f, 0f),
//        xAxisScale = listOf("21", "21", "22", "25", "23", "24", "13", "11", "6", "9"),
//        -10,
//        40
//    )
//}
//
//enum class LoggingScreenTab(
//    @StringRes val title: Int,
//    val icon: ImageVector
//) {
//    DATA(R.string.menu_home, Icons.Filled.List),
//    MESSAGE(R.string.menu_message, Icons.Filled.Notifications),
//    CONTROL(R.string.menu_control, Icons.Filled.Done);
//
//    companion object {
//        fun getTabFromResource(@StringRes resource: Int): LoggingScreenTab {
//            return when (resource) {
//                R.string.menu_message -> MESSAGE
//                R.string.menu_control -> CONTROL
//                else -> DATA
//            }
//        }
//    }
//}
//
//fun convertTimeServerToTimeUITemp(time: String): String {
//    //if (time == null) return "00:00"
//
//    //T#2H_5M_8S_815MS
//    //T#0MS
//    val indexTimeFirst = time.indexOf("#")
//    val index_H = time.indexOf("h")
//    val index_M = time.indexOf("m")
//    val index_S = time.indexOf("s")
//
//    when {
//        indexTimeFirst != 1 || (index_S - index_M) < 3 -> return "00:00"
//        index_H > 2 && index_M > 5 && index_S > 8 -> {
//            return "${time.substring(indexTimeFirst + 1, index_H)}:" +
//                    "${time.substring(index_H + 2, index_M)}:" +
//                    "${time.substring(index_M + 2, index_S)}"
//        }
//        index_H > 2 && index_S > 5 -> {
//            return "${time.substring(indexTimeFirst + 1, index_H)}:" +
//                    "${time.substring(index_H + 2, index_S)}"
//        }
//        index_M > 2 && index_S > 5 -> {
//            return "${time.substring(indexTimeFirst + 1, index_M)}:" +
//                    "${time.substring(index_M + 2, index_S)}"
//        }
//        index_S > 2 -> {
//            return "${time.substring(indexTimeFirst + 1, index_S)}"
//        }
//
//        else -> return "00:00"
//
//    }
//
//}
