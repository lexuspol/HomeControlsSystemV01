package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homecontrolssystemv01.R

@Composable
fun TestScreen(){

    //TestCard()
    MyAlert()

}

@Composable
fun MyAlert(){

    Card(
        modifier = Modifier
            .padding(10.dp, 5.dp)
            .fillMaxWidth()

        ,
        // .background(Purple500)
        //         .height(50.dp),
        shape = RoundedCornerShape(8.dp), elevation = 4.dp,
      //  border = BorderStroke(1.dp, colorBorder)

        //contentColor = Purple500,
        //backgroundColor = Color.Red
    ) {


        Row(modifier = Modifier
            .padding(10.dp)
            .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {

            Text(text = "хлеб",style = MaterialTheme.typography.subtitle1)
            Text(text = "2",style = MaterialTheme.typography.subtitle1)

        }




    }




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


@Composable
fun PreviewIcons(){
    Column() {
        Button(onClick = { }) {
            Icon(Icons.Filled.Share,contentDescription = null,modifier = Modifier.size(ButtonDefaults.IconSize))
        }
        Button(onClick = {  }) {
            Icon(Icons.Filled.Person,contentDescription = null,modifier = Modifier.size(ButtonDefaults.IconSize))
        }
        Button(onClick = {  }) {
            Icon(Icons.Filled.LocationOn,contentDescription = null,modifier = Modifier.size(ButtonDefaults.IconSize))
        }
        Button(onClick = {  }) {
            Icon(Icons.Filled.Star,contentDescription = null,modifier = Modifier.size(ButtonDefaults.IconSize))
        }
        Button(onClick = {  }) {
            Icon(Icons.Filled.ShoppingCart,contentDescription = null,modifier = Modifier.size(ButtonDefaults.IconSize))
        }


    }
}




@Preview(showBackground = true)
@Composable
fun TestPreviw(){
   //TestScreen()
}
