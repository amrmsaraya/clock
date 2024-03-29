package com.github.amrmsaraya.clock.feature_alarm.domain.entity

import java.util.*

const val DEFAULT_SNOOZE: Long = 5 * 60 * 1000

data class Alarm(
    val id: Long = 0,
    val title: String = "",
    val hour: Int = Calendar.getInstance().get(Calendar.HOUR),
    val minute: Int = Calendar.getInstance().get(Calendar.MINUTE),
    val amPm: Int = Calendar.getInstance().get(Calendar.AM_PM),
    val color: Int = 0,
    val repeatOn: List<Int> = emptyList(),
    val ringtone: String = "",
    val enabled: Boolean = true,
    val ringTime: Long = 0,
    val snooze: Long = DEFAULT_SNOOZE,
)
