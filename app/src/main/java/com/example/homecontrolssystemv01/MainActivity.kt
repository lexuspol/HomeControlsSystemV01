package com.example.homecontrolssystemv01

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.example.homecontrolssystemv01.presentation.MainViewModel
import com.example.homecontrolssystemv01.ui.theme.HomeControlsSystemV01Theme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    //private lateinit var wifiManager: WifiManager

    //private lateinit var connectivityManager:ConnectivityManager

    private var movieListResponse:List<ScanResult> by mutableStateOf(listOf())
    private var movieSSIDResponse:String by mutableStateOf("")

//    val wifiScanReceiver = object : BroadcastReceiver() {
//
//        override fun onReceive(context: Context, intent: Intent) {
//            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
//            if (success) {
//                scanSuccess()
//            }
//        }
//    }

    val request = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()

    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {    //when Wifi is on
            super.onAvailable(network)

            Log.d("HCS","Wifi is on!")


        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val wifiInfo = networkCapabilities.transportInfo as? WifiInfo

                movieSSIDResponse = wifiInfo?.ssid.toString()
                Log.d("HCS"," = $movieSSIDResponse")

            } else {
                movieSSIDResponse = "API < 29"
            }
        }

        override fun onLost(network: Network) {    //when Wifi 【turns off】
            super.onLost(network)

            Log.d("HCS","Wifi turns off!!")

                    }


    }






    override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]


        //connectivityManager = this.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager




        //val intentFilter = IntentFilter()
/* подписываемся на сообщения о получении новых результатов сканирования */
        //intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        //this.applicationContext.registerReceiver(wifiScanReceiver, intentFilter)


//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            ActivityCompat.requestPermissions(
//                activity,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CHANGE_WIFI_STATE),
//                1
//            )
//            makeEnableLocationServices(activity.applicationContext)
//        } else {
//            ActivityCompat.requestPermissions(
//                activity,
//                arrayOf(Manifest.permission.CHANGE_WIFI_STATE),
//                1
//            )
//        }

//        val success = wifiManager.startScan()
//        if (!success) {
//            /* что-то не получилось при запуске сканирования, проверьте выданые разрешения */
//            Log.d("HCS","что-то не получилось при запуске сканирования, проверьте выданые разрешения")
//        }





        setContent {
            HomeControlsSystemV01Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {





                    Greeting(movieListResponse,DataList.ssidState.value)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        //connectivityManager.requestNetwork(request, networkCallback)
        //connectivityManager.registerNetworkCallback(request, networkCallback)

    }

    override fun onPause() {
        super.onPause()
        //connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    fun scanSuccess(){

//        wifiManager.wifiState
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            val network = connectivityManager.activeNetwork
//            val wifiInfo = connectivityManager.getNetworkCapabilities(network)?.transportInfo as WifiInfo
//            movieSSIDResponse = wifiInfo.ssid
//        }else{
//            movieSSIDResponse = wifiManager.connectionInfo.ssid
//        }
        //movieSSIDResponse = wifiManager.connectionInfo.ssid

        //movieListResponse = wifiManager.scanResults
//
//        Log.d("HCS","scanSuccess(), wifiState${wifiManager.wifiState.toString()}")
//
//        if (movieListResponse.isEmpty()){
//            Log.d("HCS","movieListResponse.isEmpty()")
//        }else{
//            movieListResponse.map {
//                Log.d("HCS",it.frequency.toString())
//            }
//
//        }




    }


}

@Composable
fun Greeting(listResult:List<ScanResult>,ssid:String) {




//    val listSSID = mutableListOf<String>()
//
//if(listResult.isEmpty()){
//    Text(text = "ScanResult isEmpty")
//
//}else{
//
//    listResult.map {
//        listSSID.add(it.SSID)
//    }
//
//}

    Column{
        Text(text = "ScanResult_SSID ${listResult}")
        //Text(text = "ScanResult_listSSID ${listSSID}")
        Text(text = "SSID $ssid")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HomeControlsSystemV01Theme {
        //Greeting("Android")
    }




}

