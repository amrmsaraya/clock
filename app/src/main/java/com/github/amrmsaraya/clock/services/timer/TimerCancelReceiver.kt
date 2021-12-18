package com.github.amrmsaraya.clock.services.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.amrmsaraya.clock.presentation.timer.utils.sendTimerBroadcastAction

class TimerCancelReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        intent.getStringExtra("action")?.let {
            if (it == "cancel") {
                context.sendTimerBroadcastAction("reset")
            }
        }
    }
}