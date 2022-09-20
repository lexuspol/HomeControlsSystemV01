package com.example.homecontrolssystemv01.data.mapper

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.homecontrolssystemv01.data.database.DataDao
import com.example.homecontrolssystemv01.data.database.MessageDbModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun insertMessage(context: Context, dataDao: DataDao, message: MessageDbModel){
    try {
        dataDao.insertMessage(message)
    }catch (e:Exception){
        Log.d(message.description, e.toString())
toastMessage(context, message.description)
    }
}

suspend fun toastMessage(context: Context,message:String){
    coroutineScope {
        launch(Dispatchers.Main){
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}
