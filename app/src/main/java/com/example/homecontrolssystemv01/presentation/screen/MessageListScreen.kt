package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.domain.model.*
import com.example.homecontrolssystemv01.ui.theme.Purple700

@Composable
fun MessageScreen(
    listDataContainer:MutableList<DataContainer>,
    messageList:List<Message>?,
    loadingIsComplete:Boolean,
    deleteMessage: (Long) -> Unit,
){

    if (messageList.isNullOrEmpty()){
        CustomLinearProgressBar()
    }else{
        MessageList(
            messageList.reversed(),
            deleteMessage )
    }


    }

@Composable
fun MessageList(messageList:List<Message>, deleteMessage: (Long) -> Unit){

    val checkedStateVisible = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxHeight(),
    verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(10.dp)
        ) {
            items(messageList){container->
                if(checkedStateVisible.value) {ListRow(container,deleteMessage)} else{
                    if (container.type!=0) ListRow(container,deleteMessage)
                }
            }
        }
        //Spacer(modifier = Modifier.size(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "System message",
                // Modifier.padding(start = 5.dp),
                style = MaterialTheme.typography.subtitle1)

            Switch(checked = checkedStateVisible.value, onCheckedChange = {
                checkedStateVisible.value = it
            })

            Button(onClick = { deleteMessage(0L) },
                // Modifier.padding(end = 5.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Purple700)) {
                Text(text = "Delete all",style = MaterialTheme.typography.subtitle1)
            }



        }

    }



    }




@Composable
fun ListRow(message: Message, deleteMessage: (Long) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp, 4.dp)
            .fillMaxWidth()
            // .background(Purple500)
            .height(50.dp), shape = RoundedCornerShape(8.dp), elevation = 4.dp,

        //contentColor = Purple500,
        //backgroundColor = Purple500
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable {
                    deleteMessage(message.time)
                }

        ) {

            Text(text = message.type.toString(),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.subtitle1
            )

            Text(text = message.description,
                modifier = Modifier.weight(4f),
                style = MaterialTheme.typography.subtitle1
            )

        }



    }

}

@Preview(showBackground = true)
@Composable
fun TestMessage(){
    val list = listOf<Message>(
        Message(1,0,"Ошибка"),
        Message(1,0,"Ошибка"),
        Message(1,0,"Ошибка"),
        Message(1,0,"Ошибка"),
        Message(1,0,"Ошибка")
    )



    //DataRow(Data(23,"value","stateDoor",1,"description"))
}
















