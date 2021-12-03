package com.github.amrmsaraya.clock.domain.entity

import java.util.*

data class WorldClock(
    val calendar: Calendar = Calendar.getInstance(),
    val hours: Float = 0f,
    val minutes: Float = 0f,
    val seconds: Float = 0f,
)