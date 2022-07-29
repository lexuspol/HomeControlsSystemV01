package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.homecontrolssystemv01.domain.model.Data
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.ui.theme.Purple200
import com.example.homecontrolssystemv01.ui.theme.Purple500
import com.example.homecontrolssystemv01.ui.theme.Purple700

@Composable
fun ListData(
    modifierMain: Modifier = Modifier,
    listData:List<Data>
){
        if (listData.isNotEmpty()) {

            LazyColumn(
//                modifier = Modifier
//                    .background(MaterialTheme.colors.primarySurface)
            ) {
                items(listData){ data ->
                    //MessageRow(data)
                    if (data.description!="") MessageRow(data)
                }
            }
        }
}

@Composable
fun MessageRow(data: Data) {
    Card(
        modifier = Modifier
            .padding(8.dp, 4.dp)
            .fillMaxWidth()
           // .background(Purple500)
            .height(50.dp), shape = RoundedCornerShape(8.dp), elevation = 4.dp,
        //contentColor = Purple500,
        //backgroundColor = Purple500
    ) {
        Surface(
 //           modifier = Modifier.background(Purple500),
            color = Purple700
        ) {
//            Row(
//                Modifier
//                    .padding(4.dp)
//                    .fillMaxSize(),
//
//
//            ) {
//                Image(
//                    painter = rememberImagePainter(
//                        data = movie.imageUrl,
//
//                        builder = {
//                            scale(Scale.FILL)
//                            placeholder(R.drawable.placeholder)
//                            transformations(CircleCropTransformation())
//
//                        }
//                    ),
//                    contentDescription = movie.desc,
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .weight(0.2f)
//                )

            Row(Modifier
                .fillMaxWidth()
                .padding(10.dp)
            ) {
                Text(
                    text = data.description,
                    modifier = Modifier.weight(4f),
                    style = MaterialTheme.typography.subtitle1
                )
                Text(
                    text = data.value.toString(),
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = data.unit,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.subtitle1
                )
            }
//                Row(
// //                   verticalArrangement = Arrangement.Center,
//                    modifier = Modifier
//                        .padding(10.dp)
//                        .fillMaxSize()
// //                       .background(Purple500)
//                        //.weight(0.8f)
//                ) {
////                    Text(
////                        text = data.id.toString(),
////                        style = MaterialTheme.typography.caption
////                    )
//                    Text(
//
//                        text = data.description,
//                        style = MaterialTheme.typography.body1,
//                        textAlign = TextAlign.Left,
//                        maxLines = 2,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                    Text(
//                        text = data.value.toString(),
//                        style = MaterialTheme.typography.subtitle1,
//                        textAlign = TextAlign.Right,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier
//
////                            .background(
////                                Color.LightGray
////                            )
////                            .padding(4.dp)
//                    )
//
//
//                }
 //           }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun Test(){
    MessageRow(Data(23,"value","stateDoor",1,"description"))
}
