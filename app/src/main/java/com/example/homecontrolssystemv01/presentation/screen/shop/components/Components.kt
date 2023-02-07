package com.example.homecontrolssystemv01.presentation.screen.shop.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.presentation.screen.shop.NavShopScreen

@Composable
fun AppBarShop(route: String, onDialog: () -> Unit, pressOnBack: () -> Unit = {}) {
    TopAppBar(
        elevation = 4.dp,
        //backgroundColor = Purple200,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {

            IconButton(onClick = { pressOnBack() }) {
                Icon(Icons.Filled.ArrowBack, null)
            }

            Text(
                stringResource(
                    when (route) {
                        NavShopScreen.ShopPublicScreen.route -> R.string.shop_public
                        NavShopScreen.ShopPersonalScreen.route -> R.string.shop_personal
                        NavShopScreen.TaskScreen.route -> R.string.shop_task
                        else -> R.string.shop
                    }
                )
            )

            IconButton(onClick = {
                onDialog()
            }) {
                Icon(Icons.Filled.Add, null)
            }
        }
    }
}