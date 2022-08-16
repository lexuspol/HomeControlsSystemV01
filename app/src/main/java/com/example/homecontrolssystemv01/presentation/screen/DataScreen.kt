package com.example.homecontrolssystemv01.presentation.screen

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.ui.theme.Purple200
import com.example.homecontrolssystemv01.R
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.homecontrolssystemv01.presentation.MainViewModel
import com.example.homecontrolssystemv01.util.createDataContainer
import com.example.homecontrolssystemv01.util.loadingIsComplete


@Composable
fun DataScreen(viewModel: MainViewModel,
               selectSetting: () -> Unit){

    val dataList = viewModel.getDataListUI()
        .observeAsState()//если так не делать, то данные на экране не обновляются
        .value
    val settingList = viewModel.getDataSettingUI()
        .observeAsState()
        .value

    val dataContainerList = createDataContainer(dataList,settingList)

    val messageList = viewModel.getMessageListUI().observeAsState().value

    val connectInfo = viewModel.getConnectInfoUI()

    val loadingIsComplete = loadingIsComplete(dataList, connectInfo.value,-1)

    //val isLoading: Boolean by viewModel.isLoading
    val selectedTab = DataScreenTab.getTabFromResource(viewModel.selectedTab.value)
    val tabs = DataScreenTab.values()











       Scaffold(
            backgroundColor = MaterialTheme.colors.primarySurface,
            topBar = {DataAppBar(selectSetting)},
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
                    DataScreenTab.DATA -> DataListScreen(
                        dataContainerList,
                        loadingIsComplete,
                        connectInfo,
                        onSettingChange = {viewModel.putDataSettingUI(it)},
                    onControl = {viewModel.putControlUI(it)},
                        onLoadData = {viewModel.loadDataUI()}
                    )

                    DataScreenTab.MESSAGE -> MessageScreen(
                        dataContainerList,
                        messageList,
                        loadingIsComplete,
                        deleteMessage = {viewModel.deleteMessageUI(it)}
                    )

                    DataScreenTab.CONTROL -> ControlListScreen(
                        dataContainerList,
                        loadingIsComplete,
                        onControl = {viewModel.putControlUI(it)}
                    )
                }
            }





        }

}

@Composable
private fun DataAppBar(selectSetting: () -> Unit = {}) {
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
                Icons.Filled.Settings, null,
                //tint = color
        )
        }
    }

}

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
