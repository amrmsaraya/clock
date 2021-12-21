package com.github.amrmsaraya.clock.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat

fun Context.createNotificationChannel(
    id: String,
    name: String,
    importance: Int
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(id, name, importance)
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun Context.createNotification(
    channelId: String,
    title: String,
    content: String,
    @DrawableRes icon: Int,
    priority: Int = NotificationCompat.PRIORITY_DEFAULT,
    isFullScreen: Boolean = false,
    isSilent: Boolean = false,
    isOnGoing: Boolean = false,
    intent: PendingIntent? = null,
    actionTitle: String? = null,
    actionIntent: PendingIntent? = null,
): Notification {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationCompat.Builder(this, channelId).apply {
            setContentTitle(title)
            setContentText(content)
            setSmallIcon(icon)
            setSilent(isSilent)
            setOngoing(isOnGoing)
            setPriority(priority)

            intent?.let {
                setContentIntent(intent)
            }

            if (actionTitle != null && actionIntent != null) {
                addAction(
                    icon,
                    actionTitle,
                    actionIntent
                )
            }

            if (isFullScreen) {
                setFullScreenIntent(intent, true)
            }

        }.build()
    } else {
        NotificationCompat.Builder(this).apply {
            setContentTitle(title)
            setContentText(content)
            setSmallIcon(icon)
            setSilent(isSilent)
            setOngoing(isOnGoing)
            setPriority(priority)

            intent?.let {
                setContentIntent(intent)
            }

            if (actionTitle != null && actionIntent != null) {
                addAction(
                    icon,
                    actionTitle,
                    actionIntent
                )
            }

            if (isFullScreen) {
                setFullScreenIntent(intent, true)
            }

        }.build()
    }
}