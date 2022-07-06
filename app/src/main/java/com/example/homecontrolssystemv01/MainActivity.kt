package com.example.homecontrolssystemv01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.homecontrolssystemv01.presentation.MainViewModel
import com.example.homecontrolssystemv01.presentation.screen.Main
import com.example.homecontrolssystemv01.ui.theme.HomeControlsSystemV01Theme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

        setContent {
            HomeControlsSystemV01Theme {
                //Surface(
               // ) {
                    Main(viewModel)
              //  }
            }
        }
    }
}




//@Composable
//fun MyButtonLoad(viewModel:MainViewModel){
//    Button(
//        onClick = {
//            viewModel.savePref()
//                  },
//        Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//    ) {
//        Icon(
//            Icons.Filled.Send,
//            contentDescription = null,
//            modifier = Modifier.size(ButtonDefaults.IconSize)
//        )
//        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
//        Text("Load")
//    }
//
//}






@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HomeControlsSystemV01Theme {
        //Preview_SingleRadioButton()
    }
}

