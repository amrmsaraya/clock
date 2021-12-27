package com.github.amrmsaraya.clock.feature_clock.domain.entity

import java.util.*

data class WorldClock(
    val calendar: Calendar = Calendar.getInstance(),
    val hoursAngel: Float = 0f,
    val minutesAngel: Float = 0f,
    val secondsAngel: Float = 0f,
)