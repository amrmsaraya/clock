package com.github.amrmsaraya.clock.presentation.stopwatch.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.github.amrmsaraya.clock.services.STOPWATCH_ACTION_ACTION
import com.github.amrmsaraya.clock.services.STOPWATCH_LAPS_ACTION
import com.github.amrmsaraya.clock.services.STOPWATCH_TIME_ACTION
import com.github.amrmsaraya.clock.services.StopwatchService
import com.github.amrmsaraya.timer.Stopwatch
import com.github.amrmsaraya.timer.Time
import com.github.amrmsaraya.timer.toTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun Context.startStopwatchService() {
    val intent = Intent().also { intent ->
        intent.setClass(this, StopwatchService::class.java)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
}

fun Context.sendStopwatchBroadcastAction(action: String) {
    Intent().also { intent ->
        intent.action = STOPWATCH_ACTION_ACTION
        intent.putExtra("action", action)
        sendBroadcast(intent)
    }
}

fun Context.registerLapsReceiver(onLapsUpdate: (List<Pair<Time, Time>>) -> Unit): BroadcastReceiver {
    val lapsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getStringExtra("laps")?.let { lapsString ->
                onLapsUpdate(
                    Json.decodeFromString<List<Pair<Long, Long>>>(lapsString).map { lap ->
                        Pair(lap.first.toTime(), lap.second.toTime())
                    }
                )
            }
        }
    }
    IntentFilter().also { intent ->
        intent.addAction(STOPWATCH_LAPS_ACTION)
        registerReceiver(lapsReceiver, intent)
    }
    return lapsReceiver
}

fun Context.registerStopwatchReceiver(onTimeUpdate: (Time, Int) -> Unit): BroadcastReceiver {
    val stopwatchReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onTimeUpdate(
                intent?.getLongExtra("time", 0L)?.toTime() ?: Time(),
                intent?.getIntExtra("status", Stopwatch.IDLE) ?: Stopwatch.IDLE,
            )
        }
    }
    IntentFilter().also { intent ->
        intent.addAction(STOPWATCH_TIME_ACTION)
        registerReceiver(stopwatchReceiver, intent)
    }
    return stopwatchReceiver
}
