package com.github.amrmsaraya.clock.presentation.alarm

import com.github.amrmsaraya.clock.domain.entity.Alarm

sealed class AlarmIntent {
    data class InsertAlarm(val alarm: Alarm) : AlarmIntent()
    data class DeleteAlarms(val alarms: List<Alarm>) : AlarmIntent()
    object GetClocks : AlarmIntent()
}