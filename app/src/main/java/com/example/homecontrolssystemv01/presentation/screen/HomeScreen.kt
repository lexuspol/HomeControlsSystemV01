package com.example.homecontrolssystemv01.presentation.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homecontrolssystemv01.domain.model.*
import com.example.homecontrolssystemv01.util.difTime
import com.example.homecontrolssystemv01.util.giveDataById
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    listDataContainer:MutableList<DataContainer>,
    loadingIsComplete:Boolean
){
    if (loadingIsComplete){

        HomeScreenData(listDataContainer)

    } else {
        CustomLinearProgressBar()
    }

    }

@Composable
fun HomeScreenData(listDataContainer:MutableList<DataContainer>){
            LazyColumn(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                items(listDataContainer){container->
                    if (container.setting.visible) ListRow(container)
                }
            }
    }




@Composable
fun ListRow(container:DataContainer) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            //.wrapContentHeight()
            .fillMaxWidth()
            .padding(5.dp)
        //.background("#063041".color)
    ) {
        Text(text = container.data.description,
            modifier = Modifier.weight(4f),
            style = MaterialTheme.typography.subtitle1
        )

        Text(text = "${container.data.value.toString()} ${container.data.unit}",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.subtitle1
        )
    }

}



@Composable
fun HomeScreenMessage(){



}












