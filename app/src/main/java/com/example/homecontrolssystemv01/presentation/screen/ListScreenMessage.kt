package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.domain.model.Data
import com.example.homecontrolssystemv01.domain.model.MessageActive
import com.example.homecontrolssystemv01.ui.theme.Purple700

@Composable
fun ListMessage(
    modifierMain: Modifier = Modifier,
    listMessage:List<MessageActive>
){
    if (listMessage.isNullOrEmpty()) {
        Text(
            text = "NO Message",
            style = MaterialTheme.typography.h6
        )
    } else{
        LazyColumn(
//                modifier = Modifier
//                    .background(MaterialTheme.colors.primarySurface)
        ) {
            items(listMessage){
                MessageRow(it)
            }
        }
    }
}

@Composable
fun MessageRow(message: MessageActive) {
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
                    text = message.description,
                    modifier = Modifier.weight(4f),
                    style = MaterialTheme.typography.subtitle1
                )
//                Text(
//                    text = data.value.toString(),
//                    modifier = Modifier.weight(2f),
//                    textAlign = TextAlign.End,
//                    style = MaterialTheme.typography.subtitle1,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = data.unit,
//                    modifier = Modifier.weight(1f),
//                    style = MaterialTheme.typography.subtitle1
//                )
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