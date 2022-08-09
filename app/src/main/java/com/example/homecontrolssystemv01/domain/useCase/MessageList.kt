package com.example.homecontrolssystemv01.domain.useCase

import com.example.homecontrolssystemv01.domain.model.Data
import com.example.homecontrolssystemv01.domain.model.DataLimit
import com.example.homecontrolssystemv01.domain.model.MessageActive

class MessageList() {

    private var _listDataFloat:MutableList<Data> = mutableListOf()
    private var _listLimit:List<DataLimit> = listOf()

    fun getMessageListFloat(listData:List<Data>, listLimit:List<DataLimit>):MutableList<MessageActive>{

        _listLimit = listLimit

        listData.forEach {
            if (it.type == 3) _listDataFloat.add(it)//3 - Real type
        }

        val messageActiveList = mutableListOf<MessageActive>()

        _listDataFloat.forEach { data->
            _listLimit.forEach { lim->
                if ((data.id == lim.id)&&!data.value.isNullOrEmpty() ){
                    when{
                        (data.value.toFloat() > lim.max) ->
                            messageActiveList.add(MessageActive(lim.typeAlarm,lim.descriptorMax))

                        (data.value.toFloat() < lim.min) ->
                            messageActiveList.add(MessageActive(lim.typeAlarm,lim.descriptorMin))
                    }
                }
            }
        }
        return  messageActiveList
    }
}