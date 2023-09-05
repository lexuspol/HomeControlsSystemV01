package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.homecontrolssystemv01.presentation.MainViewModel
import com.example.homecontrolssystemv01.presentation.ShopViewModel
import com.example.homecontrolssystemv01.presentation.screen.data.DataScreen
import com.example.homecontrolssystemv01.presentation.screen.logging.LoggingScreenRev1
import com.example.homecontrolssystemv01.presentation.screen.shop.ShopScreen

@Composable
fun MainScreen(viewModel: MainViewModel, shopViewModel: ShopViewModel) {

  //  Log.d("HCS","MainScreen")

    val resourcesDataMapUI = viewModel.getResourcesDataMapUI()

    val dataList = viewModel.getDataListUI().observeAsState().value

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
            ShopScreen(viewModel = shopViewModel,
            ){
                shopViewModel.closeShopScreenUI()
                navController.navigateUp()}
        }

        composable(NavScreen.LoggingScreen.route) {
            LoggingScreenRev1(
                resourcesDataMapUI,
                getLoggingIdList = {viewModel.getLoggingIdListUI()},
                getLoggingValue = {viewModel.getLoggingValueUI(it)},
                deleteLoggingValue = { viewModel.deleteLoggingValueUI(it) }
                // addItem = {viewModel.addShopItemUI(it)},
                //  deleteItem = {viewModel.deleteShopItemUI(it)}
            ) { navController.navigateUp() }
        }

            composable(NavScreen.SettingScreen.route) {
                SettingScreen(connectSetting,systemSetting,dataList,
                    //getLoggingSetting = {viewModel.getLoggingSetting()},
                    setConnectSetting = {viewModel.setConnectSetting(it)},
                    setSystemSetting = {viewModel.setSystemSetting(it)},
                    //setLoggingSetting = {viewModel.setLoggingSetting(it)},
                    onControl = {viewModel.putControlUI(it)}
                ){navController.navigateUp()}
            }

        }
}

sealed class NavScreen(val route: String) {

    object DataScreen : NavScreen("DataScreen")
    object ShopScreen : NavScreen("ShopScreen")
    object SettingScreen : NavScreen("SettingScreen")
    object LoggingScreen : NavScreen("LoggingScreen")

}