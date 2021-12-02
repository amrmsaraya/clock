package com.github.amrmsaraya.clock.presentation.stopwatch

import com.github.amrmsaraya.timer.Time

data class StopwatchUiState(
    val stopwatch: Time = Time(),
    val isRunning: Boolean = false
)
