package com.example.homecontrolssystemv01.data

import android.util.Log
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
    private val valueEventListener:ValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val data = snapshot.getValue<List<DataDbModel>>()

                if (data != null) {
                    Log.d("HCS_FIREBASE", data[0].value.toString())
                    updateListValue(data)

                } else {
                    Log.d("HCS_FIREBASE_ERROR", "Data = null")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("HCS_FIREBASE_ERROR", "Failed to read value.", error.toException())
            }
        }

    private fun updateListValue(listValueSnapshot:List<DataDbModel>) {
        DataList.movieListResponse = listValueSnapshot
    }

    fun setDataToFirebase(list:List<DataDbModel>){
        myRef.setValue(list)
    }


    fun createEventListener(){
        myRef.addValueEventListener(valueEventListener)
        //Log.d("HCS_FIREBASE", "addValueEventListener")
    }

    fun removeEventListener(){
        myRef.removeEventListener(valueEventListener)
        //Log.d("HCS_FIREBASE", "removeEventListener")
    }

}