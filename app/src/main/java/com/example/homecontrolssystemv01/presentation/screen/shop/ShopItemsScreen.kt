package com.example.homecontrolssystemv01.presentation.screen.shop

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.data.database.ShopDbModel

@Composable
fun ShopItemsScreen(
    route:String,
    shopList: List<ShopDbModel>?,
    putItem:(ShopDbModel)-> Unit,
    deleteItem:(Int) -> Unit,
    pressOnBack: () -> Unit = {}
){

    val showDialog = remember { mutableStateOf(false)}

    if (showDialog.value){
       ShopAlertDialog(route,getNextId(shopList),onDismiss = {showDialog.value = false},putItem,deleteItem)
    }
    Scaffold(
        backgroundColor = MaterialTheme.colors.primarySurface,
        topBar = { AppBarShop(route,{showDialog.value = true},pressOnBack) }
    ) {
            padding ->

        if (!shopList.isNullOrEmpty()){

        LazyColumn(modifier = Modifier.padding()){
          //  item { Text("ShopList") }
                items(shopList){
                    ShopItemRow(route,shopItem = it,putItem,deleteItem)
                }
            }
        }
    }
}

fun getNextId(shopList: List<ShopDbModel>?):Int{

    if (!shopList.isNullOrEmpty()){

        val list2 = shopList.map {it.itemId}

        var i = 0

        run outer@{
            for (j in 0..list2.size+1) {
                if (!list2.contains(j)) {
                    i = j
                    return@outer
                }
            }
        }

//        run outer@{
//            shopList.forEachIndexed { index, shopItem ->
//                if (index != shopItem.itemId) {
//                    i = index
//                    return@outer
//                }
//                i = index + 1
//            }
//        }

        return i
    }else return 0

}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShopItemRow(route:String, shopItem: ShopDbModel,addItem:(ShopDbModel)-> Unit,deleteItem:(Int) -> Unit){

    val showDialog = remember { mutableStateOf(false)}

    if (showDialog.value){
        ShopAlertDialog(route,-1,onDismiss = {showDialog.value = false},addItem,deleteItem,shopItem)
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
fun ShopAlertDialog(route: String,
    nextId:Int,onDismiss: () -> Unit,
                    addItem:(ShopDbModel)-> Unit,deleteItem: (Int) -> Unit,
                    item:ShopDbModel = ShopDbModel() ){


    val stringList = stringArrayResource(id = when (route){
        NavShopScreen.ShopPublicScreen.route-> R.array.shopPublicGroup
        NavShopScreen.ShopPersonalScreen.route-> R.array.shopPersonalGroup
        else -> R.array.shopPublicGroup
    })

//        listOf("1. Мучное, сладкое","2. Химия","3. Крупы, приправы","4. Фрукты, овощи",
//                            "5. Мясо, рыба","6. Молочка","7. Вода")

    //Log.d("HCS","ShopPublicScreen init $nextId")

    var groupId by remember { mutableStateOf(item.groupId) }

    val section = if (item.groupId==0) "" else stringList[item.groupId-1]

    var textItem by remember { mutableStateOf(item.itemName) }
    var textCount by remember { mutableStateOf(item.countString) }
    var textSection by remember { mutableStateOf(section) }



    var mExpanded by remember { mutableStateOf(false) }
    var mTextFieldSizeSection by remember { mutableStateOf(Size.Zero)}

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
                    label = {Text("Раздел")},
                    trailingIcon = {
                        Icon(icon,"contentDescription",
                            Modifier.clickable { mExpanded = !mExpanded })
                    }
                )
                DropdownMenu(
                    expanded = mExpanded,
                    onDismissRequest = { mExpanded = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current){mTextFieldSizeSection.width.toDp()})
                ) {
                    stringList.forEachIndexed { index, map ->
                        DropdownMenuItem(onClick = {
                            textSection = map
                            mExpanded = false
                            groupId = index+1
                           // setLoggingSetting(LogSetting(map.key,logSetting.logKey,logType))
                        }) {
                            Text(text = map)
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
                        Text("УДАЛИТЬ")
                    }

                    Button(onClick = { onDismiss()
                        addItem(
                            ShopDbModel(
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
fun AppBarShop(route:String,onDialog: () -> Unit,pressOnBack: () -> Unit = {}){
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
            Text(stringResource(when(route){
                NavShopScreen.ShopPublicScreen.route->R.string.shop_public
                NavShopScreen.ShopPersonalScreen.route->R.string.shop_personal
                else ->  R.string.shop
            }))
            IconButton(onClick = {
                onDialog()
            }) {
                Icon(Icons.Filled.Add, null)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShopTestPreview(){
   // ShopAlertDialog(nextId = 1, onDismiss = { /*TODO*/ }, addItem = { /*TODO*/ }, deleteItem ={ /*TODO*/ } )
}

