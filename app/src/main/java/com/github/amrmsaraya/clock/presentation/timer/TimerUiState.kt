package com.github.amrmsaraya.clock.presentation.timer

import com.github.amrmsaraya.timer.Time
import com.github.amrmsaraya.timer.Timer

data class TimerUiState(
    val timer: Time = Time(),
    val configuredTime: Long = 1,
    val status: Int = Timer.IDLE
)
