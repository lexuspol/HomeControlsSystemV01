package com.example.homecontrolssystemv01.presentation.screen.shop

import androidx.compose.runtime.Composable
import com.example.homecontrolssystemv01.data.database.ShopDbModel
import com.example.homecontrolssystemv01.domain.model.shop.ShopItem

@Composable
fun ShopPersonalScreen(

    shopList:List<ShopDbModel>?,
    putItem: (ShopDbModel) -> Unit,
    deleteItem: (Int) -> Unit,
    pressOnBack: () -> Unit = {}





) {
}