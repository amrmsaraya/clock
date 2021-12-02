package com.github.amrmsaraya.clock.presentation.alarm

sealed class AlarmIntent {
    object Start : AlarmIntent()
    object Pause : AlarmIntent()
    object Reset : AlarmIntent()
    data class Configure(val delay: Long) : AlarmIntent()

}
