package com.github.amrmsaraya.clock.presentation.timer

import com.github.amrmsaraya.timer.Time

data class TimerUiState(
    val timer: Time = Time(),
    val configuredTime: Long = 1,
    val isRunning: Boolean = false
)
