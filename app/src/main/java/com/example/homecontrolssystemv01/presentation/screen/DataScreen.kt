package com.example.homecontrolssystemv01.presentation.screen

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.homecontrolssystemv01.domain.model.Message
import com.example.homecontrolssystemv01.domain.model.ModeConnect
import com.example.homecontrolssystemv01.presentation.MainViewModel
import com.example.homecontrolssystemv01.util.createDataContainer
import com.example.homecontrolssystemv01.util.createMessageListLimit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


@Composable
fun DataScreen(viewModel: MainViewModel,
               selectSetting: () -> Unit){

    val dataListLive = viewModel.getDataListUI()
    val settingListLive = viewModel.getDataSettingUI()

    val dataList = dataListLive.observeAsState().value
    val settingList = settingListLive.observeAsState().value


    val dataContainerList = createDataContainer(dataList,settingList)

    val messageListSystem = viewModel.getMessageListUI().observeAsState().value
    //Log.d("HCS",messageListSystem.toString())

    val connectInfo = viewModel.getConnectInfoUI()

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
            }
        ) { innerPadding ->
            val modifier = Modifier.padding(innerPadding).fillMaxHeight()
            Crossfade(selectedTab) { destination ->
                when (destination) {
                    DataScreenTab.DATA -> DataListScreen(
                        modifier = modifier,
                        dataContainerList,
                        messageListSystem,
                        connectInfo,
                        onSettingChange = {viewModel.putDataSettingUI(it)},
                    onControl = {viewModel.putControlUI(it)},
                        onLoadData = {viewModel.loadDataUI()}
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
                        connectInfo,
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
