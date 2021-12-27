package com.github.amrmsaraya.clock.feature_stopwatch.presentation.ui.stopwatch_screen

sealed class StopwatchIntent {
    object Start : StopwatchIntent()
    object Pause : StopwatchIntent()
    object Reset : StopwatchIntent()
    object Lap : StopwatchIntent()
    data class Configure(val delay: Long) : StopwatchIntent()

}
