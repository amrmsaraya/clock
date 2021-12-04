package com.github.amrmsaraya.clock.presentation.alarm

import com.github.amrmsaraya.clock.domain.entity.Alarm

data class AlarmUiState(
    val alarms: List<Alarm> = emptyList(),
)
