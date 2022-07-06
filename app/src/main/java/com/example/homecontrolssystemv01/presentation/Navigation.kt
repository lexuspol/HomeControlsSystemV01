package com.example.homecontrolssystemv01.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route){
        composable(route = Screen.MainScreen.route){
                MainScreen(navController = navController)
            }
        composable(route =Screen.ListScreen.route){
            ListScreen(navController = navController)
        }
        composable(route =Screen.SettingScreen.route){
            SettingScreen(navController = navController)
        }
    }

}

@Composable
fun MainScreen(navController:NavController){

    //viewModel()

}

@Composable
fun ListScreen(navController:NavController){

}

@Composable
fun SettingScreen(navController:NavController){

}