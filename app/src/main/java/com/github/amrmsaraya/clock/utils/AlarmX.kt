package com.github.amrmsaraya.clock.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.github.amrmsaraya.clock.domain.entity.Alarm
import com.github.amrmsaraya.clock.presentation.alarm.utils.Days
import com.github.amrmsaraya.clock.services.alarm.AlarmReceiver
import java.util.*

fun Context.setAlarm(
    alarm: Alarm,
    repeat: Boolean = false,
    snooze: Boolean = false
): Long {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val pendingIntent = Intent(this, AlarmReceiver::class.java).let { intent ->

        intent.putExtra("id", alarm.id.toInt())
        intent.putExtra("title", alarm.title)
        intent.putExtra("hour", alarm.hour)
        intent.putExtra("minute", alarm.minute)
        intent.putExtra("amPm", alarm.amPm)
        intent.putExtra("ringtone", alarm.ringtone)
        intent.putExtra("color", alarm.color)
        intent.putExtra("repeatOn", alarm.repeatOn.toIntArray())

        PendingIntent.getBroadcast(
            this,
            alarm.id.toInt(),
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
    }

    val currentTime = Calendar.getInstance()
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR, if (alarm.hour == 12) 0 else alarm.hour)
        set(Calendar.MINUTE, alarm.minute)
        set(Calendar.AM_PM, alarm.amPm)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        when (alarm.repeatOn.isEmpty()) {
            true -> if (timeInMillis < currentTime.timeInMillis) {
                set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1)
            }
            false -> {
                val repeatOn = Days.values().filter { it.ordinal in alarm.repeatOn }

                set(Calendar.DAY_OF_WEEK, getDayOfWeek(repeatOn, repeat, currentTime))

                if (timeInMillis < currentTime.timeInMillis) {
                    set(Calendar.DAY_OF_WEEK, getDayOfWeek(repeatOn, true, this))
                    if (get(Calendar.DAY_OF_WEEK) == currentTime.get(Calendar.DAY_OF_WEEK)) {
                        set(Calendar.WEEK_OF_YEAR, currentTime.get(Calendar.WEEK_OF_YEAR) + 1)
                    }
                }
            }
        }
    }

    alarmManager.setAlarmClock(
        AlarmManager.AlarmClockInfo(
            if (snooze) System.currentTimeMillis() + alarm.snooze else calendar.timeInMillis,
            pendingIntent
        ),
        pendingIntent
    )
    return if (snooze) System.currentTimeMillis() + alarm.snooze else calendar.timeInMillis
}

private fun getDayOfWeek(
    repeatOn: List<Days>,
    getNextDay: Boolean,
    calendar: Calendar
): Int {
    val dayOfWeek = repeatOn.filter {
        if (getNextDay) {
            it.calendar > calendar.get(Calendar.DAY_OF_WEEK)
        } else {
            it.calendar >= calendar.get(Calendar.DAY_OF_WEEK)
        }
    }.minOfOrNull { it.calendar } ?: repeatOn.minOf { it.calendar }

    return dayOfWeek
}

fun Context.cancelAlarm(alarm: Alarm) {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val pendingIntent = Intent(this, AlarmReceiver::class.java).let { intent ->

        intent.putExtra("id", alarm.id.toInt())
        intent.putExtra("title", alarm.title)
        intent.putExtra("hour", alarm.hour)
        intent.putExtra("minute", alarm.minute)
        intent.putExtra("amPm", alarm.amPm)
        intent.putExtra("ringtone", alarm.ringtone)
        intent.putExtra("color", alarm.color)
        intent.putExtra("repeatOn", alarm.repeatOn.toIntArray())

        PendingIntent.getBroadcast(
            this,
            alarm.id.toInt(),
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
    }

    alarmManager.cancel(pendingIntent)
}