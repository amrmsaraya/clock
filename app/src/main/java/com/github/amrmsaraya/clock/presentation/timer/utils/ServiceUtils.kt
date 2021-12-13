package com.github.amrmsaraya.clock.presentation.timer.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.github.amrmsaraya.clock.services.TIMER_ACTION_ACTION
import com.github.amrmsaraya.clock.services.TIMER_TIME_ACTION
import com.github.amrmsaraya.clock.services.TimerService
import com.github.amrmsaraya.timer.Time
import com.github.amrmsaraya.timer.Timer
import com.github.amrmsaraya.timer.toTime

fun Context.startTimerService(configuredTimer: Long) {
    val intent = Intent().also { intent ->
        intent.putExtra("configuredTime", configuredTimer)
        intent.setClass(this, TimerService::class.java)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
}

fun Context.sendTimerBroadcastAction(action: String) {
    Intent().also { intent ->
        intent.action = TIMER_ACTION_ACTION
        intent.putExtra("action", action)
        sendBroadcast(intent)
    }
}

fun Context.configureTimerService(timeMillis: Long) {
    Intent().also { intent ->
        intent.action = TIMER_ACTION_ACTION
        intent.putExtra("configuredTime", timeMillis)
        sendBroadcast(intent)
    }
}

fun Context.registerTimerReceiver(onTimeUpdate: (Time, Int) -> Unit): BroadcastReceiver {
    val timerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onTimeUpdate(
                intent?.getLongExtra("time", 0L)?.toTime() ?: Time(),
                intent?.getIntExtra("status", Timer.IDLE) ?: Timer.IDLE,
            )
        }
    }
    IntentFilter().also { intent ->
        intent.addAction(TIMER_TIME_ACTION)
        registerReceiver(timerReceiver, intent)
    }
    return timerReceiver
}
