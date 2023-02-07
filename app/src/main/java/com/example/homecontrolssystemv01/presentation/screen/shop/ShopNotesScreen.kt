package com.example.homecontrolssystemv01.presentation.screen.shop

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.primarySurface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.homecontrolssystemv01.data.database.shop.ShopDbModel
import com.example.homecontrolssystemv01.data.database.shop.TaskDbModel
import com.example.homecontrolssystemv01.presentation.screen.shop.components.AppBarShop

@Composable
fun ShopNotesScreen(route: String,
                    taskList: List<TaskDbModel>?,
                    pressOnBack: () -> Unit = {}) {

    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {

    }
    Scaffold(
        backgroundColor = MaterialTheme.colors.primarySurface,
        topBar = {
            //@Composable
            AppBarShop(
                route,
                onDialog = {
                },
                pressOnBack
            )
        }
    ) { padding ->

        if (!taskList.isNullOrEmpty()) {
            LazyColumn(modifier = Modifier.padding()) {
                //  item { Text("ShopList") }
                items(taskList) { item ->

                }
            }
        }
    }
}


