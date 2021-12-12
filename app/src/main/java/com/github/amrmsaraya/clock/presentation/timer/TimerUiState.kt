package com.github.amrmsaraya.clock.presentation.timer

import com.github.amrmsaraya.timer.Time
import com.github.amrmsaraya.timer.Timer
import com.github.amrmsaraya.clock.domain.entity.Timer as LocalTimer

data class TimerUiState(
    val timer: Time = Time(),
    val timers: List<LocalTimer> = emptyList(),
    val configuredTime: Long = 1,
    val status: Int = Timer.IDLE
)
