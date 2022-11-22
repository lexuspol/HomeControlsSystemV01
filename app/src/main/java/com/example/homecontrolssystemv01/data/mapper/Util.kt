package com.example.homecontrolssystemv01.data.mapper

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.homecontrolssystemv01.DataID
import com.example.homecontrolssystemv01.R
import com.example.homecontrolssystemv01.data.database.DataDao
import com.example.homecontrolssystemv01.data.database.MessageDbModel
import com.example.homecontrolssystemv01.domain.enum.MessageType
import com.example.homecontrolssystemv01.domain.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*

suspend fun insertMessage(context: Context, dataDao: DataDao, idMessage:Int){

    try {

//        if (idMessage == DataID.completeUpdate.id){
//
//            dataDao.insertMessage(MessageDbModel(Date().time,
//                DataID.completeUpdate.id,
//                MessageType.SYSTEM.int
//                ,DataID.completeUpdate.name + " OK"))
//
//        }else{

            val messageListRes = context.resources.getStringArray(R.array.message)

            messageListRes.forEach { messageItemRes->

                val idRes = messageItemRes.substringBefore('|').toInt()

                if (idRes == idMessage){

                    val descriptionRes =messageItemRes.substringAfter('|').substringBefore('^')
                    val typeRes = messageItemRes.substringAfter('^').toInt()

                    dataDao.insertMessage(MessageDbModel(Date().time,idMessage,typeRes,descriptionRes))

                    return@forEach

                }


            }


       // }









    }catch (e:Exception){
        Log.d("HCS_insertMessage", e.toString())
toastMessage(context, "Error insert message to base")
    }
}

suspend fun toastMessage(context: Context,message:String){
    coroutineScope {
        launch(Dispatchers.Main){
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}
