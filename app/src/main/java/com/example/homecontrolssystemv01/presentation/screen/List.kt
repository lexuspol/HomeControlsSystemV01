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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.ui.theme.Purple500

@Composable
fun ListData(
    modifierMain: Modifier = Modifier,
    listData:List<Data>
){
        if (listData.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .background(MaterialTheme.colors.primarySurface)
            ) {
                items(listData){ data ->
                    MessageRow(data)
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
            .height(110.dp), shape = RoundedCornerShape(8.dp), elevation = 4.dp,
        //backgroundColor = Purple500
    ) {
        Surface(
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
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxHeight()
 //                       .background(Purple500)
                        //.weight(0.8f)
                ) {
                    Text(
                        text = data.id.toString(),
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = data.value.toString(),
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier
                            .background(
                                Color.LightGray
                            )
                            .padding(4.dp)
                    )
                    Text(
                        text = data.name.toString(),
                        style = MaterialTheme.typography.body1,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                }
 //           }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun Test(){
    MessageRow(Data(23,"open","stateDoor",1))
}
