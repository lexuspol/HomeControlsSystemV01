package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.domain.model.shop.ShopItem

@Composable
fun ShopScreen(
    shopList: List<ShopItem>?,
    addItem:(ShopItem)-> Unit,
    deleteItem:(Int) -> Unit,
    pressOnBack: () -> Unit = {}
){
    val nextId = remember { mutableStateOf(0)}

    if (!shopList.isNullOrEmpty()){
        shopList.forEach { item->

            if (item.itemId != nextId.value){
                return@forEach
            }
            nextId.value = item.itemId + 1
       }
    }

    val showDialog = remember { mutableStateOf(false)}

    if (showDialog.value){
        ShopAlertDialog(nextId.value,onDismiss = {showDialog.value = false},addItem,deleteItem)
    }
    Scaffold(
        backgroundColor = MaterialTheme.colors.primarySurface,
        topBar = { AppBarShop({showDialog.value = true},pressOnBack)}

    ) {
            padding ->

        if (!shopList.isNullOrEmpty()){
        LazyColumn(modifier = Modifier.padding()){
          //  item { Text("ShopList") }
                items(shopList){
                    ShopItemRow(shopItem = it,addItem,deleteItem)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShopItemRow(shopItem: ShopItem,addItem:(ShopItem)-> Unit,deleteItem:(Int) -> Unit){

    val showDialog = remember { mutableStateOf(false)}

    if (showDialog.value){
        ShopAlertDialog(-1,onDismiss = {showDialog.value = false},addItem,deleteItem,shopItem)
    }

    val colorBorder = if (shopItem.enabled) Color.White else MaterialTheme.colors.background
    val colorCard = if (shopItem.enabled) MaterialTheme.colors.background else MaterialTheme.colors.primarySurface


    Card(
        modifier = Modifier
            .padding(10.dp, 5.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    shopItem.enabled = !shopItem.enabled
                    addItem(shopItem)
                },
                onLongClick = {
                    showDialog.value = true
                }
            )
            ,
        // .background(Purple500)
        //         .height(50.dp),
        shape = RoundedCornerShape(8.dp), elevation = 4.dp,
       border = BorderStroke(1.dp, colorBorder),

        //contentColor = Purple500,
        backgroundColor = colorCard
    ) {
            Row(modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {

                Text(text = "${shopItem.groupId}.",style = MaterialTheme.typography.subtitle1)
                Text(text = shopItem.itemName,style = MaterialTheme.typography.subtitle1)
                Text(text = shopItem.countString,style = MaterialTheme.typography.subtitle1)
            }
    }
}

@Composable
fun ShopAlertDialog(nextId:Int,onDismiss: () -> Unit,
                    addItem:(ShopItem)-> Unit,deleteItem: (Int) -> Unit,
                    item:ShopItem = ShopItem() ){
    val numberList = listOf(1,2,3,4,5,6)

    var textItem by remember { mutableStateOf(item.itemName) }
    var textCount by remember { mutableStateOf(item.countString) }

    var groupId by remember { mutableStateOf(item.groupId) }

    AlertDialog(onDismissRequest = onDismiss,
    title = {
            },
        text = {
            Column(Modifier.padding(10.dp)) {
                OutlinedTextField(
                    value = textItem,
                    onValueChange = {
                        textItem = it
                    },
                    // enabled = checkedStateLimit.value,
                    label = { Text(text = "Item") },
                    textStyle = MaterialTheme.typography.subtitle1,
                    //  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                OutlinedTextField(
                    value = textCount,
                    onValueChange = {
                        textCount = it
                    },
                    label = { Text(text = "Count") },
                    textStyle = MaterialTheme.typography.subtitle1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(numberList){number->
                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable {
                                   // colorTitle = number.second
                                    groupId = number
                                }
                            ,
                            backgroundColor = if(number == groupId) MaterialTheme.colors.primarySurface
                            else MaterialTheme.colors.background
                        ) {
                            Text(
                                text = number.toString(),
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(4.dp),
                            )
                        }
                    }
                }

                Row(modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {

                    Button(onClick = { onDismiss()
                        deleteItem(item.itemId)
                    },
                        enabled = nextId==-1

                    ) {
                        Text("DELETE")
                    }

                    Button(onClick = { onDismiss()
                        addItem(
                            ShopItem(
                                if(nextId==-1)item.itemId else nextId,
                                textItem,
                                groupId,
                                textCount))})
                    {
                        Text("OK")
                    }
                }
            }
        },
        confirmButton = {
        }
       // backgroundColor =
        )
}


@Composable
fun AppBarShop(onDialog: () -> Unit,pressOnBack: () -> Unit = {}){
    TopAppBar(
        elevation = 4.dp,
        //backgroundColor = Purple200,
        ){
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,){
            IconButton(onClick = {pressOnBack()}) {
                Icon(Icons.Filled.ArrowBack, null)
            }
            Text(stringResource(R.string.shop))
            IconButton(onClick = {
                onDialog()
            }) {
                Icon(Icons.Filled.Add, null)
            }
        }
    }
}