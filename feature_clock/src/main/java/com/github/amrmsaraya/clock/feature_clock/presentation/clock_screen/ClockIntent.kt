package com.github.amrmsaraya.clock.feature_clock.presentation.clock_screen

import java.util.*

sealed class ClockIntent {
    data class InsertClock(val timeZone: TimeZone) : ClockIntent()
    data class DeleteClocks(val timeZones: List<TimeZone>) : ClockIntent()
    object GetClocks : ClockIntent()
}
