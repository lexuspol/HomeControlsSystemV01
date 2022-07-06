package com.example.homecontrolssystemv01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.example.homecontrolssystemv01.presentation.MainViewModel
import com.example.homecontrolssystemv01.presentation.RadioButtonList
import com.example.homecontrolssystemv01.ui.theme.HomeControlsSystemV01Theme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

        setContent {
            HomeControlsSystemV01Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {

                        Row() {
                            MyRadioButton(viewModel.getSsidForRadioButton(),viewModel)
                            MyRadioButton(viewModel.getMode(),viewModel)
                        }
                        MyButtonLoad(viewModel)
                        Greeting(viewModel)
                    }


                }
            }
        }
    }
}

@Composable
fun Greeting(viewModel: MainViewModel) {

    val listData = viewModel.getData()
    val ssid = viewModel.getSsidFromText()


    Column{
        Text(text = "Имя WiFi сети ${ssid.value}")
        if (listData.isNotEmpty()) {
            Text(text = "Дата и время ${listData[0].value}")
            Text(text = "Наружная температура ${listData[1].value} С")
            Text(text = "Входная дверь ${listData[2].value}")
            Text(text = "Дверь на террасу ${listData[3].value}")
        }
    }
}

@Composable
fun MyRadioButton (radioButtonList: RadioButtonList,viewModel:MainViewModel){
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
                            viewModel.setParam(radioButtonList.keySetting, text)
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

