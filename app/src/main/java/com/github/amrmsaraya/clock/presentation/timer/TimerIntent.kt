package com.github.amrmsaraya.clock.presentation.timer

sealed class TimerIntent {
    object Start : TimerIntent()
    object Pause : TimerIntent()
    object Reset : TimerIntent()
    data class Configure(val timeMillis: Long, val delay: Long = 10) : TimerIntent()

}
