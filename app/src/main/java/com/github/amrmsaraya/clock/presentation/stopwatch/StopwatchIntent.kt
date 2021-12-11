package com.github.amrmsaraya.clock.presentation.stopwatch

sealed class StopwatchIntent {
    object Start : StopwatchIntent()
    object Pause : StopwatchIntent()
    object Reset : StopwatchIntent()
    object Lap : StopwatchIntent()
    data class Configure(val delay: Long) : StopwatchIntent()

}
