package com.github.amrmsaraya.clock.feature_alarm.presentation.ui.alarm_screen

import com.github.amrmsaraya.clock.feature_alarm.domain.entity.Alarm

data class AlarmUiState(
    val alarms: List<Alarm> = emptyList(),
)
