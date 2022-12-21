package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.ui.theme.PrimaryLight

@Composable
fun CustomLinearProgressBar(){
    Column(modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(15.dp),
            backgroundColor = Color.LightGray,
            color = Color.Green //progress color
        )
    }
}



@Composable
fun CardSettingElement(content: @Composable () -> Unit){

    Card(
        modifier = Modifier
            .padding(10.dp)
        ,
        shape = RoundedCornerShape(8.dp),
        //backgroundColor = MaterialTheme.colors.primary,
        //contentColor = Color.Red,//цвет текста
        //elevation = 4.dp,//возвышение
        border = BorderStroke(1.dp, Color.White),
content = {content()}
        )

}