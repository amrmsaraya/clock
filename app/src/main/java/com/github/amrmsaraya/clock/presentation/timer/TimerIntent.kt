package com.github.amrmsaraya.clock.presentation.timer

import com.github.amrmsaraya.clock.domain.entity.Timer

sealed class TimerIntent {
    object Start : TimerIntent()
    object Pause : TimerIntent()
    object Reset : TimerIntent()
    data class Configure(val timeMillis: Long, val delay: Long = 10) : TimerIntent()
    data class Insert(val timer: Timer) : TimerIntent()
    data class Delete(val timer: Timer) : TimerIntent()
    object GetTimers : TimerIntent()
}
