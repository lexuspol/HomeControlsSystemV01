package com.example.homecontrolssystemv01.presentation

sealed class Screen(val route: String){
    object MainScreen: Screen("main_screen")
    object ListScreen: Screen("list_screen")
    object SettingScreen: Screen("setting_screen")
}
