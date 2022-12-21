package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homecontrolssystemv01.R

@Composable
fun TestScreen(){

    TestCard()


}

@Composable
fun TestCard(){


    TopAppBar(
        elevation = 4.dp,
       // backgroundColor = Purple200,
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

        IconButton(onClick = { },
            modifier = Modifier
                .weight(1f)
        ) {
            Icon(
                Icons.Filled.ArrowBack, null,
                //tint = color
            )
        }
        IconButton(onClick = { },
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


@Preview(showBackground = true)
@Composable
fun TestPreviw(){
   //TestScreen()
}
