package com.example.homecontrolssystemv01.presentation

import android.app.Application
import com.example.homecontrolssystemv01.R

object ResourcesString{
    //    val alarmMessage = application.resources.getStringArray(R.array.alarmMessage)
    fun getResourcesDataMap(application: Application):Map<Int, Data>{
        val arrayData = application.resources.getStringArray(R.array.data)
        val map = mutableMapOf<Int, Data>()

        arrayData.forEach {
            val id = try {it.substringBefore('|').toInt()} catch (e:Exception){0}
            if (id!=0){
                val description = it.substringAfter('|').substringBefore('^')
                val unit = it.substringAfter('^')
                map[id] = Data(description,unit)
            }
        }

        return map.toMap()
    }

    data class Data(
        var description:String = "",
        var unit:String = "",
    )
    }
