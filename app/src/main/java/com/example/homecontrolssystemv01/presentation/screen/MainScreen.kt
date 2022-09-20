package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.presentation.MainViewModel
import com.example.homecontrolssystemv01.util.createMessageListLimit

@Composable
fun MainScreen(viewModel:MainViewModel) {


    //val dataList = viewModel.getDataListUI().observeAsState().value
    //val settingList = viewModel.getDataSettingUI().observeAsState().value
    val ssid = viewModel.getDataListUI().observeAsState().value?.find { it.id == DataID.SSID.id }?.value

    //viewModel.putMessageListUI(createMessageListLimit(dataList,
        //settingList))

    val connectInfo = viewModel.getConnectInfoUI()
    //val listSsid = viewModel.getSsidListForRadioButton()
    //val prefSsid
    val connectSetting = viewModel.getConnectSettingUI()

    val navController = rememberNavController()

        NavHost(navController = navController, startDestination = NavScreen.DataScreen.route) {
            composable(NavScreen.DataScreen.route) {
                DataScreen(viewModel = viewModel,
                    selectSetting = {navController.navigate(NavScreen.SettingScreen.route)})
            }
            composable(NavScreen.SettingScreen.route) {
                SettingScreen(connectSetting,ssid,connectInfo,
                    onValueChange = {viewModel.setDataSetting(it)}
                ){navController.navigateUp()}
            }
        }
}

sealed class NavScreen(val route: String) {

    object DataScreen : NavScreen("DataScreen")

    object SettingScreen : NavScreen("SettingScreen")

}