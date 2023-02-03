package com.example.homecontrolssystemv01.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.presentation.screen.CardSettingElement
import com.example.homecontrolssystemv01.domain.enum.MessageType
import com.example.homecontrolssystemv01.domain.model.message.Message
//import com.example.homecontrolssystemv01.ui.theme.Purple700
import com.example.homecontrolssystemv01.util.convertLongToTime

@Composable
fun MessageScreen(
    modifier:Modifier,
    messageList:List<Message>?,
    deleteMessage: (Int) -> Unit,
){

        if (messageList.isNullOrEmpty()){
            Text(text = "No message",
                modifier = Modifier.padding(10.dp),
                style = MaterialTheme.typography.body1)
        }else{

            //Log.d("HCS_temp","$messageList")

           // messageList.sortedBy { it.time }
           // messageList.reversed()
            if (messageList.size>1){ //одно сообщение остается всегда - START/STOP
                MessageList(modifier,messageList.sortedBy { it.time }.reversed(),deleteMessage )
            }


        }
    }

@Composable
fun MessageList(modifier:Modifier, messageList:List<Message>, deleteMessage: (Int) -> Unit){

    val checkedStateVisible = remember { mutableStateOf(false) }
    val completeUpdateTime = messageList.find { it.id == DataID.completeUpdate.id }?.time?:-1L


    Column(
        modifier = modifier,
    verticalArrangement = Arrangement.SpaceBetween
    ) {

        Box(
            modifier = Modifier.weight(6f)
            ) {
            LazyColumn(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                items(messageList){container->
                    if (container.type>=0){
                        if(checkedStateVisible.value) {ListRow(container,completeUpdateTime,deleteMessage)} else{
                            if (container.type!=MessageType.SYSTEM.int) ListRow(container,completeUpdateTime,deleteMessage)
                        }
                    }


                }
            }
        }

        CardSettingElement {
            Box(
                modifier = Modifier.weight(1f)

                //modifier = Modifier.padding(10.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    //                   .padding(bottom = 50.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "System message",
                        // Modifier.padding(start = 5.dp),
                        style = MaterialTheme.typography.subtitle1)

                    Switch(checked = checkedStateVisible.value, onCheckedChange = {
                        checkedStateVisible.value = it
                    })
                    Button(onClick = { deleteMessage(0) }
                        // Modifier.padding(end = 5.dp),
                       // colors = ButtonDefaults.buttonColors(backgroundColor = Purple700)
                    )
                    {
                        Text(text = "Delete all",style = MaterialTheme.typography.subtitle1)
                    }
                }

            }
        }
        //Spacer(modifier = Modifier.size(20.dp))
    }
    }




@Composable
fun ListRow(message: Message, completeUpdateTime:Long, deleteMessage: (Int) -> Unit) {

    val colorMes =
        when {
            (message.time == completeUpdateTime)&&
                    (message.type == MessageType.SYSTEM.int) -> Color.White
            (message.time == completeUpdateTime)&&
                    (message.type == MessageType.WARNING.int) -> Color.Yellow
            message.type == MessageType.ALARM.int-> Color.Red
            else -> Color.Gray
        }


    Card(
        modifier = Modifier
            .padding(8.dp, 4.dp)
            .fillMaxWidth()

//            .background(                          //цвет за контентом
//                color = when (message.type) {
//                    0 -> Color.White
//                    1 -> Color.Yellow
//                    2 -> Color.Red
//                    else -> Color.Gray
//                }
//            )
            .clickable { deleteMessage(message.id) }
            ,
            // .background(Purple500)
 //           .height(50.dp),
        //shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        border = BorderStroke(3.dp, colorMes)

        //contentColor = Purple500,
        //backgroundColor = Purple500
    ) {

        Surface(
            //           modifier = Modifier.background(Purple500),
//            color =
//                when {
//                    (message.time == completeUpdateTime)&&
//                            (message.type == MessageType.SYSTEM.int) -> Color.White
//                    (message.time == completeUpdateTime)&&
//                            (message.type == MessageType.WARNING.int) -> Color.Yellow
//                    message.type == MessageType.ALARM.int-> Color.Red
//                    else -> Color.Gray
//                },
            //contentColor = Color.Black
        ) {
            Column(
              //  contentColorFor(backgroundColor = )
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()

                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,

                ) {
                    Text(
                        text = when (message.type) {
                            MessageType.SYSTEM.int -> "System message"
                            MessageType.WARNING.int -> "Warning message"
                            MessageType.ALARM.int-> "Alarm message"
                            else -> ""
                        },
                        modifier = Modifier.padding(5.dp),
                        style = MaterialTheme.typography.subtitle2,
                   // color = colorMes
                    )
                    Text(
                        text = if (message.time>0) convertLongToTime(message.time) else "",
                        modifier = Modifier.padding(5.dp),
                        style = MaterialTheme.typography.subtitle2,
                   //     color = Color.Black
                    )
                }
                Text(
                    text = message.description,
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.body1,
               //     color = Color.Black
                )
            }//column
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TestMessage(){
    val list = listOf<Message>(

    )

    fun test(id:Long){}

//    ListRow(message = Message(20202020,23,1,"Температура выше нормы", true),
//        deleteMessage = {test(202020)})

   // MessageList(list) { test(0) }

    //DataRow(Data(23,"value","stateDoor",1,"description"))
}
















