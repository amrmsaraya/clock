package com.github.amrmsaraya.clock.feature_timer.presentation.ui.timer_screen

import com.github.amrmsaraya.timer.Time
import com.github.amrmsaraya.timer.Timer
import com.github.amrmsaraya.clock.feature_timer.domain.entity.Timer as EntityTimer

data class TimerUiState(
    val timer: Time = Time(),
    val timers: List<EntityTimer> = emptyList(),
    val configuredTime: Long = 0,
    val status: Int = Timer.IDLE
)
