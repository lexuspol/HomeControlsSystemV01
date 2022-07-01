package com.example.homecontrolssystemv01

import android.net.wifi.ScanResult
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.homecontrolssystemv01.data.DataList
import com.example.homecontrolssystemv01.domain.Data
import com.example.homecontrolssystemv01.domain.Mode
import com.example.homecontrolssystemv01.presentation.MainViewModel
import com.example.homecontrolssystemv01.ui.theme.HomeControlsSystemV01Theme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    private var movieListResponse:List<ScanResult> by mutableStateOf(listOf())
    private var movieSSIDResponse:String by mutableStateOf("")


    override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {
            HomeControlsSystemV01Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {

                        LoadData(ssidList = viewModel.getSsid(),viewModel)
                        MyButtonLoad(viewModel)
                        Greeting(viewModel.getData(),DataList.ssidState.value)
                    }


                }
            }
        }
    }
}

@Composable
fun Greeting(listData:List<Data>, ssid:String) {

    Column{
        Text(text = "Имя WiFi сети $ssid")
        if (listData.isNotEmpty()) {
            Text(text = "Дата и время ${listData[0].value}")
            Text(text = "Наружная температура ${listData[1].value} С")
            Text(text = "Входная дверь ${listData[2].value}")
            Text(text = "Дверь на террасу ${listData[3].value}")
        }
    }
}


@Composable
fun LoadData(ssidList:MutableList<String>,viewModel:MainViewModel) {
    //val radioOptions = listOf("Calls", "Missed", "Friends")

    val radioOptionsSsid = mutableListOf("NO_WIFI")
    radioOptionsSsid.addAll(ssidList)
    var indexSsid = radioOptionsSsid.indexOf(viewModel.parametrs.ssidSet)
if (indexSsid == -1){
    indexSsid = 0
}


//    radioOptionsSsid.forEach {
//if (it == viewModel.parametrs.ssidSet){
//    return
//}else{
//    indexSsid += 1
//    if (indexSsid>radioOptionsSsid.size){
//        indexSsid = 0
//    }
//
//}
//    }

    val radioOptionsMode = mutableListOf<String>()

    Mode.values().map {
        radioOptionsMode.add(it.name)
    }

    var indexMode = radioOptionsMode.indexOf(viewModel.parametrs.mode)
    if (indexMode == -1){
        indexMode = 0
    }

    Row() {

        MyRadioButton(1,list = radioOptionsSsid,viewModel,indexSsid)
        MyRadioButton(2,list = radioOptionsMode,viewModel,indexMode)

    }


}

@Composable
fun MyRadioButton (nParam:Int,list:MutableList<String>,viewModel:MainViewModel,index:Int){
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(list[index]) }
    Column(
//        Modifier.selectableGroup()
    ) {
        list.forEach { text ->
            Row(
                Modifier
                    //.fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            onOptionSelected(text)
                            viewModel.setParam(nParam,text)
                             },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick =                         null
                        //

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
fun MyButtonLoad(viewModel:MainViewModel){
    Button(
        onClick = {
            viewModel.savePref()
                  },
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(
            Icons.Filled.Send,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("Load")
    }

}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HomeControlsSystemV01Theme {
        //Preview_SingleRadioButton()
    }
}

