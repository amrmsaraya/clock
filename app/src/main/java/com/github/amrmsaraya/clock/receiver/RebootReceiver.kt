package com.github.amrmsaraya.clock.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.amrmsaraya.clock.feature_alarm.domain.usecase.AlarmCRUDUseCase
import com.github.amrmsaraya.clock.utils.setAlarm
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class RebootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmCRUDUseCase: AlarmCRUDUseCase

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {

            val scope = CoroutineScope(Dispatchers.IO + Job())

            scope.launch {
                alarmCRUDUseCase.getAlarms().collect { alarms ->
                    alarms.forEach { alarm ->
                        if (alarm.enabled) {
                            val ringTime = context.setAlarm(alarm)
                            alarmCRUDUseCase.insert(alarm.copy(ringTime = ringTime))
                        }
                    }
                    scope.cancel()
                }
            }

        }
    }
}