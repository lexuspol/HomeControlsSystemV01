package com.example.homecontrolssystemv01.presentation.screen

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.R
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.homecontrolssystemv01.presentation.MainViewModel
import com.example.homecontrolssystemv01.util.createDataContainer
import kotlinx.coroutines.launch


@Composable
fun DataScreen(viewModel: MainViewModel,
               selectItem: (String) -> Unit,
               selectSetting: () -> Unit){

    val dataListLive = viewModel.getDataListUI()
    val settingListLive = viewModel.getDataSettingUI()

    val dataList = dataListLive.observeAsState().value
    val settingList = settingListLive.observeAsState().value

    //val dataContainerList = createDataContainer(dataListLive,settingList)
    val dataContainerList = createDataContainer(dataList,settingList)

    val messageListSystem = viewModel.getMessageListUI().observeAsState().value
    //Log.d("HCS",messageListSystem.toString())

    val systemSetting = viewModel.getSystemSettingUI()

    val selectedTab = DataScreenTab.getTabFromResource(viewModel.selectedTab.value)
    val tabs = DataScreenTab.values()

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

       Scaffold(
           scaffoldState = scaffoldState,
            backgroundColor = MaterialTheme.colors.primarySurface,
            topBar = {DataAppBar(selectSetting,selectMenu={
                scope.launch{
                    scaffoldState.drawerState.open()
                }
            })},
            bottomBar = {
                BottomNavigation(
                    //backgroundColor = Mate,

                ) {
                    tabs.forEach { tab ->
                        //цвет MESSAGE
                        var color = LocalContentColor.current
                        if (tab.name==DataScreenTab.MESSAGE.name && !messageListSystem.isNullOrEmpty()){

                            val alarm = messageListSystem.find { it.type==2 }
                            val warning = messageListSystem.find { it.type==1 }

                            when{
                                (alarm !=null) ->  {
                                    color = Color.Red
                                }
                                (warning!=null) -> color = Color.Yellow
                            }

                        }
                        BottomNavigationItem(
                            icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                            label = { Text(text = stringResource(tab.title), color = Color.White) },
                            selected = tab == selectedTab,
                            onClick = { viewModel.selectTab(tab.title) },
                            selectedContentColor = color,
                            unselectedContentColor = color,

                        )
                    }
                }
            },
           drawerContent={MyMenu(selectItem = {
               scope.launch{ scaffoldState.drawerState.close()}
               selectItem(it)})}
        ) { innerPadding ->
            val modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
            Crossfade(selectedTab) { destination ->
                when (destination) {
                    DataScreenTab.DATA -> DataListScreen(
                        modifier = modifier,
                        dataContainerList,
                        messageListSystem,
                        systemSetting,
                        onSettingChange = {viewModel.putDataSettingUI(it)},
                    onControl = {viewModel.putControlUI(it)},
                        onLoadData = {viewModel.loadDataUI()},
                        deleteData = {viewModel.deleteDataUI(it)}
                    )

                    DataScreenTab.MESSAGE -> MessageScreen(
                        modifier = modifier,
                        messageListSystem,
                        deleteMessage = {
                            viewModel.deleteMessageUI(it)
                        }
                    )

                    DataScreenTab.CONTROL -> ControlListScreen(
                        modifier = modifier,
                        dataContainerList,
                        onControl = {viewModel.putControlUI(it)}
                    )
                }
            }





        }

}

@Composable
private fun DataAppBar(selectSetting: () -> Unit = {},
                       selectMenu: () -> Unit = {}

                       ) {
    TopAppBar(
        elevation = 4.dp,
       // backgroundColor = PrimaryDark,
        ){

        IconButton(onClick = { selectMenu()},
            modifier = Modifier
                .weight(1f)
        ) {
            Icon(
                Icons.Filled.Menu, null,
                //tint = color
            )
        }



                Text(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically)
                .weight(3f),
            text = stringResource(R.string.app_name),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

//        IconButton(onClick = { selectSetting()},
//            modifier = Modifier
//                .weight(1f)
//        ) {
//            Icon(
//                Icons.Filled.Settings, null,
//                //tint = color
//        )
//        }
    }

}


@Composable
fun MyMenu(selectItem: (String) -> Unit){

    val itemsList = prepareNavigationDrawerItems()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
          //  .background(brush = Brush.verticalGradient(colors = MaterialTheme.colors.)),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 36.dp)
    ) {
        item {

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.secondary)
            )

            Text(
                modifier = Modifier
                    .padding(top = 12.dp),
                text = stringResource(R.string.app_name),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                modifier = Modifier.padding(top = 8.dp, bottom = 30.dp),
                text = "lexuspol@gmail.com",
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                //color = Color.White
            )
        }

        items(itemsList){item ->
            NavigationListItem(item = item) {
                selectItem(item.route)
            }

        }
    }
}



@Composable
private fun NavigationListItem(
    item: NavigationDrawerItem,
    itemClick: (String) -> Unit
) {

    CardSettingElement {
        Row(modifier = Modifier
            .clickable { itemClick(item.route) }
            .fillMaxSize()
            .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically){
            Icon(
                imageVector = item.image,
                contentDescription = item.label,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = item.label,
            )

        }
    }
    //Spacer(Modifier.size(ButtonDefaults.IconSpacing))
}




@Composable
private fun prepareNavigationDrawerItems(): List<NavigationDrawerItem> {
    val itemsList = arrayListOf<NavigationDrawerItem>()

    itemsList.add(
        NavigationDrawerItem(
            image = Icons.Filled.List,
            label = "Data",
            route = NavScreen.DataScreen.route

        )
    )

    itemsList.add(
        NavigationDrawerItem(
            image = Icons.Filled.ShoppingCart,
            label = "Shop",
            route = NavScreen.ShopScreen.route

        )
    )

    itemsList.add(
        NavigationDrawerItem(
            image = Icons.Filled.Settings,
            label = "Settings",
            route = NavScreen.SettingScreen.route

        )
    )


    return itemsList
}

data class NavigationDrawerItem(
    val image: ImageVector,
    val label: String,
    val route: String,
    val showUnreadBubble: Boolean = false
)

enum class DataScreenTab(
    @StringRes val title: Int,
    val icon: ImageVector
) {
    DATA(R.string.menu_home, Icons.Filled.List),
    MESSAGE(R.string.menu_message, Icons.Filled.Notifications),
    CONTROL(R.string.menu_control, Icons.Filled.Done);

    companion object {
        fun getTabFromResource(@StringRes resource: Int): DataScreenTab {
            return when (resource) {
                R.string.menu_message -> MESSAGE
                R.string.menu_control -> CONTROL
                else -> DATA
            }
        }
    }
}
