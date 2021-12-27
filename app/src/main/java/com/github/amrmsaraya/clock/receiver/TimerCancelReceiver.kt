package com.github.amrmsaraya.clock.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.amrmsaraya.clock.utils.sendTimerBroadcastAction

class TimerCancelReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        intent.getStringExtra("action")?.let {
            if (it == "cancel") {
                context.sendTimerBroadcastAction("reset")
            }
        }
    }
}