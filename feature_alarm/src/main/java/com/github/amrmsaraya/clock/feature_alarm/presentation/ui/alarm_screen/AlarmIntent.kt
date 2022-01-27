package com.github.amrmsaraya.clock.feature_alarm.presentation.ui.alarm_screen

import com.github.amrmsaraya.clock.feature_alarm.domain.entity.Alarm

sealed class AlarmIntent {
    data class InsertAlarm(val alarm: Alarm) : AlarmIntent()
    data class DeleteAlarms(val alarms: List<Alarm>) : AlarmIntent()
    object GetAlarms : AlarmIntent()
}