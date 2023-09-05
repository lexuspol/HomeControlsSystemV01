package com.example.homecontrolssystemv01.presentation.screen.shop

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.data.database.shop.ShopDbModel
import com.example.homecontrolssystemv01.presentation.screen.MyCircularProgressIndicator
import com.example.homecontrolssystemv01.presentation.screen.shop.components.AppBarShop
import com.example.homecontrolssystemv01.util.getColorShopItem

@Composable
fun ShopItemsScreen(
    route: String,
    shopList: List<ShopDbModel>?,
    putItem: (ShopDbModel) -> Unit,
    deleteItem: (Int) -> Unit,
    pressOnBack: () -> Unit = {}
) {


    var itemShop by remember { mutableStateOf(ShopDbModel()) }

    val id = if (!shopList.isNullOrEmpty()) {
        getNextId2(shopList.map { it.itemId })
    } else 0

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        //@Composable
        ShopAlertDialog(
            route, onDismiss = { showDialog = false },
            putItem, deleteItem, itemShop
        )
    }
    Scaffold(
        backgroundColor = MaterialTheme.colors.primarySurface,
        topBar = {
            //@Composable
            AppBarShop(
                route,
                onDialog = {
                    itemShop = ShopDbModel(itemId = id)
                    showDialog = true
                },
                pressOnBack
            )
        }
    ) { padding ->

        if (!shopList.isNullOrEmpty()) {
            LazyColumn(modifier = Modifier.padding()) {
                //  item { Text("ShopList") }
                items(shopList) { item ->
                    ShopItemRow(
                        shopItem = item,
                        onLongClick = {
                            itemShop = item
                            showDialog = true
                        },
                        putItem,
                        // deleteItem
                    )
                }//items
            }//lazy
        }else{
            MyCircularProgressIndicator()
        }
    }
}


//fun getNextId(shopList: List<ShopDbModel>?):Int{
//    if (!shopList.isNullOrEmpty()){
//        val list2 = shopList.map {it.itemId}
//        var i = 0
//        run outer@{
//            for (j in 0..list2.size+1) {
//                if (!list2.contains(j)) {
//                    i = j
//                    return@outer
//                }
//            }
//        }
//        return i
//    }else return 0
//}
fun getNextId2(list: List<Int>): Int {
    var i = 0
    run outer@{
        for (j in 0..list.size + 1) {
            if (!list.contains(j)) {
                i = j
                return@outer
            }
        }
    }
    return i
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShopItemRow(
    shopItem: ShopDbModel, onLongClick: () -> Unit,
    putItem: (ShopDbModel) -> Unit
)
//deleteItem:(Int) -> Unit)
{

//    val showDialog = remember { mutableStateOf(false)}
//
//    if (showDialog.value){
//        //ShopAlertDialog(route,-1,onDismiss = {showDialog.value = false},addItem,deleteItem,shopItem)
//    }

    val colorBorder = if (shopItem.enabled) Color.White else MaterialTheme.colors.background
    val colorCard =
        if (shopItem.enabled) MaterialTheme.colors.background else MaterialTheme.colors.primarySurface

    Card(
        modifier = Modifier
            .padding(10.dp, 5.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    shopItem.enabled = !shopItem.enabled
                    putItem(shopItem)
                },
                onLongClick = {
                    onLongClick()
                    // showDialog.value = true
                }
            ),
        // .background(Purple500)
        //         .height(50.dp),
        shape = RoundedCornerShape(8.dp), elevation = 4.dp,
        border = BorderStroke(1.dp, colorBorder),
        //contentColor = Purple500,
        backgroundColor = colorCard
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Box(modifier =
            Modifier
                .size(15.dp)
                .clip(CircleShape)
                .background(getColorShopItem(shopItem.groupId))
            )
            //Text(text = "${shopItem.groupId}.", style = MaterialTheme.typography.subtitle1)
            Text(text = shopItem.itemName, style = MaterialTheme.typography.subtitle1)
            Text(text = shopItem.countString, style = MaterialTheme.typography.subtitle1)
        }
    }
}

@Composable
fun ShopAlertDialog(
    route: String,
    onDismiss: () -> Unit,
    addItem: (ShopDbModel) -> Unit, deleteItem: (Int) -> Unit,
    item: ShopDbModel
) {

   // Log.d("HCS", item.toString())

    val stringList = stringArrayResource(
        id = when (route) {
            NavShopScreen.ShopPublicScreen.route -> R.array.shopPublicGroup
            NavShopScreen.ShopPersonalScreen.route -> R.array.shopPersonalGroup
            else -> R.array.shopPublicGroup
        }
    )

    var groupId by remember { mutableStateOf(item.groupId) }

    val section = if (item.groupId < 1) "" else stringList[item.groupId - 1]

    var textItem by remember { mutableStateOf(item.itemName) }
    var textCount by remember { mutableStateOf(item.countString) }
    var textSection by remember { mutableStateOf(section) }

    var mExpanded by remember { mutableStateOf(false) }
    var mTextFieldSizeSection by remember { mutableStateOf(Size.Zero) }

    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

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
                    label = { Text(text = "Продукт") },
                    textStyle = MaterialTheme.typography.subtitle1,
                    //  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                OutlinedTextField(
                    value = textCount,
                    onValueChange = {
                        textCount = it
                    },
                    label = { Text(text = "Кол-во") },
                    textStyle = MaterialTheme.typography.subtitle1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                OutlinedTextField(
                    value = textSection,
                    onValueChange = {
                        textSection = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        // .padding(top=10.dp)
                        .onGloballyPositioned { coordinates ->
                            // This value is used to assign to
                            // the DropDown the same width
                            mTextFieldSizeSection = coordinates.size.toSize()
                        },
                    // enabled = !serverMode,
                    label = { Text("Раздел") },
                    trailingIcon = {
                        Icon(icon, "contentDescription",
                            Modifier.clickable { mExpanded = !mExpanded })
                    }
                )
                DropdownMenu(
                    expanded = mExpanded,
                    onDismissRequest = { mExpanded = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { mTextFieldSizeSection.width.toDp() })
                ) {
                    stringList.forEachIndexed { index, map ->
                        DropdownMenuItem(onClick = {
                            textSection = map
                            mExpanded = false
                            groupId = index + 1
                        },
//                        modifier = Modifier.background(
//                           getColorShopItem(groupId)
//                        )
                        ) {
                            Text(text = map)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(
                        onClick = {
                            onDismiss()
                            deleteItem(item.itemId)
                        },
                        //enabled = nextId==-1
                        enabled = item.groupId != -1
                    ) {
                        Text("УДАЛИТЬ")
                    }

                    Button(onClick = {
                        onDismiss()
                        item.itemName = textItem
                        item.groupId = if (groupId ==-1) 0 else groupId //чтобы была активная кнопка удаления при пустом разделе
                        item.countString = textCount
                        addItem(item)
                    })
                    {
                        Text("OK")
                    }
                }
            }
        },
        confirmButton = {
        }
    )
}


@Preview(showBackground = true)
@Composable
fun ShopTestPreview() {
}

