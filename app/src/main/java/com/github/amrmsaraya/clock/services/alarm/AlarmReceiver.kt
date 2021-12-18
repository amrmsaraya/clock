package com.github.amrmsaraya.clock.services.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.clock.presentation.alarm_activity.AlarmActivity
import com.github.amrmsaraya.clock.utils.createNotification
import com.github.amrmsaraya.clock.utils.createNotificationChannel

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingIntent = Intent(context, AlarmActivity::class.java).let {

            it.putExtra("id", intent.getIntExtra("id", 0))
            it.putExtra("hour", intent.getIntExtra("hour", 0))
            it.putExtra("minute", intent.getIntExtra("minute", 0))
            it.putExtra("amPm", intent.getIntExtra("amPm", 0))
            it.putExtra("color", intent.getIntExtra("color", 0))

            it.putExtra("title", intent.getStringExtra("title"))
            it.putExtra("ringtone", intent.getStringExtra("ringtone"))

            PendingIntent.getActivity(
                context,
                0,
                it,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
        }

        context.createNotificationChannel(
            id = "Alarm",
            name = "Alarm",
            importance = NotificationManager.IMPORTANCE_HIGH
        )

        val notification = context.createNotification(
            channelId = "Alarm",
            title = "Alarm",
            content = "Alarm Content",
            icon = R.drawable.ic_norification_logo,
            isSilent = true,
            isOnGoing = true,
            priority = NotificationCompat.PRIORITY_HIGH,
            isFullScreen = true,
            intent = pendingIntent
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(
            intent.getIntExtra("id", 0),
            notification
        )
    }
}