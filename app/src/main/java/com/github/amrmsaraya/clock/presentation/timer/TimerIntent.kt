package com.github.amrmsaraya.clock.presentation.timer

import com.github.amrmsaraya.clock.domain.entity.Timer
import com.github.amrmsaraya.timer.Time

sealed class TimerIntent {
    data class UpdateTimer(val timer: Time, val status: Int) : TimerIntent()
    data class ConfigureTime(val timeMillis: Long) : TimerIntent()
    data class Insert(val timer: Timer) : TimerIntent()
    data class Delete(val timer: Timer) : TimerIntent()
    object GetTimers : TimerIntent()
}
