package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.ui.theme.Purple700

@Composable
fun TestScreen(){

   CardSettingElement { TestCard() }


}

@Composable
fun TestCard(){


          Column(
                modifier = Modifier
                    .padding(10.dp)
                ,
                horizontalAlignment = Alignment.Start,
                //verticalArrangement = Arrangement.Center
            ) {

                Text(text = "Имя текущей сети", style = MaterialTheme.typography.body1)
                Text(text = "Имя локальной сети", style = MaterialTheme.typography.body1)
                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Purple700),

                    ) {
                    Text(text = "Сделать текущую сеть локальной",style = MaterialTheme.typography.body1)
                }


    }




}


@Preview(showBackground = true)
@Composable
fun TestPreviw(){
   // TestScreen()
}
