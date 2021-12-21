package com.github.amrmsaraya.clock.services.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.github.amrmsaraya.clock.BuildConfig
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.clock.domain.entity.Alarm
import com.github.amrmsaraya.clock.domain.usecase.AlarmCRUDUseCase
import com.github.amrmsaraya.clock.presentation.alarm_activity.AlarmActivity
import com.github.amrmsaraya.clock.utils.createNotification
import com.github.amrmsaraya.clock.utils.createNotificationChannel
import com.github.amrmsaraya.clock.utils.setAlarm
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

const val ALARM_NOTIFICATION_ID = 12
private const val NOTIFICATION_CHANNEL_ID = "${BuildConfig.APPLICATION_ID}.ALARM"

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmCRUDUseCase: AlarmCRUDUseCase

    override fun onReceive(context: Context, intent: Intent) {

        val scope = CoroutineScope(Dispatchers.IO + Job())

        val alarm = Alarm(
            id = intent.getIntExtra("id", 0).toLong(),
            title = intent.getStringExtra("title") ?: "",
            hour = intent.getIntExtra("hour", 0),
            minute = intent.getIntExtra("minute", 0),
            amPm = intent.getIntExtra("amPm", 0),
            color = intent.getIntExtra("color", 0),
            repeatOn = intent.getIntArrayExtra("repeatOn")?.toList() ?: intArrayOf().toList(),
            ringtone = intent.getStringExtra("ringtone") ?: "",
        )

        val pendingIntent = Intent(context, AlarmActivity::class.java).let {

            it.putExtra("id", alarm.id.toInt())
            it.putExtra("title", alarm.title)
            it.putExtra("hour", alarm.hour)
            it.putExtra("minute", alarm.minute)
            it.putExtra("amPm", alarm.amPm)
            it.putExtra("color", alarm.color)
            it.putExtra("ringtone", alarm.ringtone)
            it.putExtra("repeatOn", alarm.repeatOn.toIntArray())

            PendingIntent.getActivity(
                context,
                alarm.id.toInt(),
                it,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_MUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
        }

        context.createNotificationChannel(
            id = NOTIFICATION_CHANNEL_ID,
            name = context.getString(R.string.alarm),
            importance = NotificationManager.IMPORTANCE_HIGH
        )

        val notification = context.createNotification(
            channelId = NOTIFICATION_CHANNEL_ID,
            title = context.getString(R.string.alarm),
            content = alarm.title,
            icon = R.drawable.ic_norification_logo,
            isSilent = true,
            isOnGoing = true,
            priority = NotificationCompat.PRIORITY_HIGH,
            isFullScreen = true,
            intent = pendingIntent
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(ALARM_NOTIFICATION_ID, notification)

        scope.launch {
            if (alarm.repeatOn.isEmpty()) {
                alarmCRUDUseCase.insert(alarm.copy(enabled = false))
            } else {
                val ringTime = context.setAlarm(alarm, repeat = true)
                alarmCRUDUseCase.insert(alarm.copy(ringTime = ringTime))
            }
            scope.cancel()
        }
    }
}