package com.example.homecontrolssystemv01.domain

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import com.example.homecontrolssystemv01.domain.model.Data

class batteryMonitor(private val application: Application) {









    private fun batteryPct():Float {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            application.registerReceiver(null, ifilter)
        }
        val batteryPct: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }
        Log.d("HCS_fromMainViewModel","battery = $batteryPct %")

        return (batteryPct ?: 0) as Float
    }



}