package com.example.homecontrolssystemv01.presentation.screen

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homecontrolssystemv01.ui.theme.Purple200
import com.example.homecontrolssystemv01.R
import androidx.compose.runtime.getValue
import com.example.homecontrolssystemv01.presentation.MainViewModel


@Composable
fun Main(viewModel: MainViewModel){

    val dataList = viewModel.getDataListUI()
    val listSsid = viewModel.getSsidListForRadioButton()
    val dataConnect = viewModel.getDataConnectUI()
    val dataSetting = viewModel.getDataSettingUI()

    val isLoading: Boolean by viewModel.isLoading
    val selectedTab = DataHomeTab.getTabFromResource(viewModel.selectedTab.value)
    val tabs = DataHomeTab.values()

        Scaffold(
            backgroundColor = MaterialTheme.colors.primarySurface,
            topBar = { DataAppBar() },
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
                    DataHomeTab.HOME -> HomeData(modifier, dataList,dataConnect)
                    DataHomeTab.LIST -> ListData(modifier, dataList)
                    DataHomeTab.SETTING -> SettingData(modifier,listSsid, dataSetting,
                        onValueChange = {
                        viewModel.setDataSetting(it)
                    })
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
private fun DataAppBar() {
    TopAppBar(
        elevation = 6.dp,
        backgroundColor = Purple200,
        modifier = Modifier.height(58.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically),
            text = stringResource(R.string.app_name),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

enum class DataHomeTab(
    @StringRes val title: Int,
    val icon: ImageVector
) {
    HOME(R.string.menu_home, Icons.Filled.Home),
    LIST(R.string.menu_list, Icons.Filled.List),
    SETTING(R.string.menu_setting, Icons.Filled.Settings);

    companion object {
        fun getTabFromResource(@StringRes resource: Int): DataHomeTab {
            return when (resource) {
                R.string.menu_list -> LIST
                R.string.menu_setting -> SETTING
                else -> HOME
            }
        }
    }
}