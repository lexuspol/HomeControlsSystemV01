package com.example.homecontrolssystemv01

import android.net.wifi.ScanResult
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.homecontrolssystemv01.data.DataList
import com.example.homecontrolssystemv01.domain.Data
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
                    Greeting(viewModel.getData(),DataList.ssidState.value)
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HomeControlsSystemV01Theme {
        //Greeting("Android")
    }
}

