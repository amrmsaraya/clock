package com.github.amrmsaraya.clock.presentation.clock

import java.util.*

sealed class ClockIntent {
    data class InsertClock(val timeZone: TimeZone) : ClockIntent()
    data class DeleteClocks(val timeZones: List<TimeZone>) : ClockIntent()
    object GetClocks : ClockIntent()
    object ResetDeleteFlag : ClockIntent()
}
