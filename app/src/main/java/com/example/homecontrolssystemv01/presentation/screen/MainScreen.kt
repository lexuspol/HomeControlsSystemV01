package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.homecontrolssystemv01.presentation.MainViewModel

@Composable
fun MainScreen(viewModel:MainViewModel) {

    val dataList = viewModel.getDataListUI()
    val dataConnect = viewModel.getDataConnectUI()
    val listSsid = viewModel.getSsidListForRadioButton()
    val dataSetting = viewModel.getDataSettingUI()

    val navController = rememberNavController()

        NavHost(navController = navController, startDestination = NavScreen.DataScreen.route) {
            composable(NavScreen.DataScreen.route) {
                DataScreen(viewModel = viewModel,
                    selectSetting = {navController.navigate(NavScreen.SettingScreen.route)})
            }
            composable(NavScreen.SettingScreen.route) {
                SettingScreen(listSsid,dataSetting,dataList,dataConnect,
                    onValueChange = {viewModel.setDataSetting(it)}
                ){navController.navigateUp()}
            }
        }
}

sealed class NavScreen(val route: String) {

    object DataScreen : NavScreen("DataScreen")

    object SettingScreen : NavScreen("SettingScreen")

}