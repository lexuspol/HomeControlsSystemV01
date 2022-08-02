package com.example.homecontrolssystemv01.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.homecontrolssystemv01.data.database.DataDbModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

object FirebaseFactory {

    private const val FIREBASE_URL =
        "https://homesystemcontrolv01-default-rtdb.asia-southeast1.firebasedatabase.app"
    private const val FIREBASE_PATH = "data"

    private val database = Firebase.database(FIREBASE_URL)
    private val myRef = database.getReference(FIREBASE_PATH)


    fun setDataToFirebase(list:List<DataDbModel>){
        myRef.setValue(list)
    }

    fun createEventListener(valueEventListener:ValueEventListener){
        myRef.addValueEventListener(valueEventListener)
        //Log.d("HCS_FIREBASE", "addValueEventListener")
    }

    fun removeEventListener(valueEventListener:ValueEventListener){
        myRef.removeEventListener(valueEventListener)
        //Log.d("HCS_FIREBASE", "removeEventListener")
    }

}