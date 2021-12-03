package com.github.amrmsaraya.clock.presentation.clock

import java.util.*

data class Time(
    val calendar: Calendar = Calendar.getInstance(),
    val hours: Float = 0f,
    val minutes: Float = 0f,
    val seconds: Float = 0f,
)