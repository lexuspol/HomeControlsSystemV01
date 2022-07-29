package com.example.homecontrolssystemv01.presentation.screen

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.ui.theme.Purple200
import com.example.homecontrolssystemv01.R
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.homecontrolssystemv01.domain.model.ModeConnect
import com.example.homecontrolssystemv01.presentation.MainViewModel


@Composable
fun DataScreen(viewModel: MainViewModel,
               selectSetting: () -> Unit){

    val dataList = viewModel.getDataListUI()
    val dataConnect = viewModel.getDataConnectUI()

    val isLoading: Boolean by viewModel.isLoading
    val selectedTab = DataHomeTab.getTabFromResource(viewModel.selectedTab.value)
    val tabs = DataHomeTab.values()

    val color = when(dataConnect.value.modeConnect){
        ModeConnect.SERVER -> Color.Gray
        ModeConnect.LOCAL -> Color.Green
        ModeConnect.REMOTE -> Color.Yellow
        ModeConnect.STOP -> Color.Red
    }

        Scaffold(
            backgroundColor = MaterialTheme.colors.primarySurface,
            topBar = {DataAppBar(color, selectSetting)},
            bottomBar = {
                BottomNavigation(
                    backgroundColor = Purple200,

                ) {
                    tabs.forEach { tab ->
                        BottomNavigationItem(
                            icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                            label = { Text(text = stringResource(tab.title), color = Color.White) },
                            selected = tab == selectedTab,
                            onClick = { viewModel.selectTab(tab.title) },
                            selectedContentColor = LocalContentColor.current,
                            unselectedContentColor = LocalContentColor.current,
                        )
                    }
                }
            }
        ) { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            Crossfade(selectedTab) { destination ->
                when (destination) {
                    DataHomeTab.HOME -> HomeDataScreen(modifier, dataList,dataConnect)
                    DataHomeTab.LIST -> ListData(modifier, dataList)
                    DataHomeTab.CONTROL -> ControlDataScreen(dataList,onValueChange = {
                        viewModel.putControlUI(it)
                    })
//                    DataHomeTab.SETTING -> SettingData(modifier,listSsid, dataSetting,
//                        onValueChange = {
//                        viewModel.setDataSetting(it)
//                    }
//                    )
                }
            }
        }
        if (isLoading) {
//            CircularProgressIndicator(
//
//            )
        }
}

@Composable
private fun DataAppBar(color:Color, selectSetting: () -> Unit = {}) {
    TopAppBar(
        elevation = 4.dp,
        backgroundColor = Purple200,
        ){
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

        IconButton(onClick = { selectSetting()},
            modifier = Modifier
                .weight(1f)
        ) {
            Icon(
                Icons.Filled.Settings, null, tint = color            )
        }
    }

}




//    TopAppBar(
//        elevation = 6.dp,
//        backgroundColor = Purple200,
//        modifier = Modifier.height(58.dp)
//        title = {
//            Text("I'm a TopAppBar")
//        },
//        navigationIcon = ,
//        actions = {
//            IconButton(onClick = {/* Do Something*/ }) {
//                Icon(Icons.Filled.Share, null)
//            }
//            IconButton(onClick = {/* Do Something*/ }) {
//                Icon(Icons.Filled.Settings, null)
//            }
//        }
//    )
//    ) {
//        Text(
//            modifier = Modifier
//                .padding(8.dp)
//                .align(Alignment.CenterVertically),
//            text = stringResource(R.string.app_name),
//            color = Color.White,
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold
//        )
//
//    }


enum class DataHomeTab(
    @StringRes val title: Int,
    val icon: ImageVector
) {
    HOME(R.string.menu_home, Icons.Filled.Home),
    LIST(R.string.menu_list, Icons.Filled.List),
    CONTROL(R.string.menu_control, Icons.Filled.Done);

    companion object {
        fun getTabFromResource(@StringRes resource: Int): DataHomeTab {
            return when (resource) {
                R.string.menu_list -> LIST
                R.string.menu_control -> CONTROL
                else -> HOME
            }
        }
    }
}
//@Composable
//fun TopAppBarSample(){
//
//        TopAppBar(
//            elevation = 4.dp,
//            title = {
//                Text("I'm a TopAppBar")
//            },
//            backgroundColor =  MaterialTheme.colors.primarySurface,
//            navigationIcon = {
//                IconButton(onClick = {/* Do Something*/ }) {
//                    Icon(Icons.Filled.ArrowBack, null)
//                }
//            }, actions = {
//                IconButton(onClick = {/* Do Something*/ }) {
//                    Icon(Icons.Filled.Share, null)
//                }
//                IconButton(onClick = {/* Do Something*/ }) {
//                    Icon(Icons.Filled.Settings, null)
//                }
//            })
//
//        Text("Hello World")
//
//
//}