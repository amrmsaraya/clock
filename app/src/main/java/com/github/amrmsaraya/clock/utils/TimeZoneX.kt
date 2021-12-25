package com.github.amrmsaraya.clock.utils

import com.github.amrmsaraya.clock.domain.entity.WorldClock
import java.util.*

const val DEGREES_PER_DIAL = 360 / 60
const val START_ANGEL = 270

fun TimeZone.convertToWorldClock(): WorldClock {
    val calendar = Calendar.getInstance(this)

    val hours = START_ANGEL + calendar.get(Calendar.HOUR) * DEGREES_PER_DIAL * 5 +
            calendar.get(Calendar.MINUTE) / 2f
    val minutes = START_ANGEL + calendar.get(Calendar.MINUTE) * DEGREES_PER_DIAL +
            calendar.get(Calendar.SECOND) / 10f
    val seconds = START_ANGEL + (calendar.get(Calendar.SECOND) * 1000 +
            calendar.get(Calendar.MILLISECOND)) * DEGREES_PER_DIAL / 1000f

    return WorldClock(
        calendar = calendar,
        hoursAngel = hours,
        minutesAngel = minutes,
        secondsAngel = seconds,
    )
}