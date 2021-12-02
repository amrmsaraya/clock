package com.github.amrmsaraya.clock.presentation.clock

sealed class ClockIntent {
    object Start : ClockIntent()
    object Pause : ClockIntent()
    object Reset : ClockIntent()
    data class Configure(val delay: Long) : ClockIntent()

}
