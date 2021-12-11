package com.github.amrmsaraya.clock.presentation.stopwatch

import com.github.amrmsaraya.timer.Stopwatch
import com.github.amrmsaraya.timer.Time

data class StopwatchUiState(
    val stopwatch: Time = Time(),
    val laps: List<Pair<Time, Time>> = emptyList(),
    val status: Int = Stopwatch.IDLE
)
