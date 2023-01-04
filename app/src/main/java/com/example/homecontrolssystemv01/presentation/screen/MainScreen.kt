package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.presentation.MainViewModel

@Composable
fun MainScreen(viewModel:MainViewModel) {

    val dataList = viewModel.getDataListUI().observeAsState().value
    val shopList = viewModel.getShopListUI()

    val connectSetting = viewModel.getConnectSettingUI()
    val systemSetting = viewModel.getSystemSettingUI()

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavScreen.DataScreen.route) {
            composable(NavScreen.DataScreen.route) {
                DataScreen(viewModel = viewModel,
                    selectItem = {navController.navigate(it)},
                    selectSetting = {navController.navigate(NavScreen.SettingScreen.route)})
            }

        composable(NavScreen.ShopScreen.route) {
            ShopScreen(
                shopList,
                addItem = {viewModel.addShopItemUI(it)},
                deleteItem = {viewModel.deleteShopItemUI(it)}
            ){navController.navigateUp()}
        }


            composable(NavScreen.SettingScreen.route) {
                SettingScreen(connectSetting,systemSetting,dataList,
                    setConnectSetting = {viewModel.setConnectSetting(it)},
                    setSystemSetting = {viewModel.setSystemSetting(it)},
                    onControl = {viewModel.putControlUI(it)}
                ){navController.navigateUp()}
            }



        }
}

sealed class NavScreen(val route: String) {

    object DataScreen : NavScreen("DataScreen")
    object ShopScreen : NavScreen("ShopScreen")
    object SettingScreen : NavScreen("SettingScreen")

}