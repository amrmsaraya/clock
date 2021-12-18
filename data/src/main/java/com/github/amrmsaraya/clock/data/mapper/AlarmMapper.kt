package com.github.amrmsaraya.clock.data.mapper

import com.github.amrmsaraya.clock.data.model.AlarmDTO
import com.github.amrmsaraya.clock.domain.entity.Alarm

internal fun AlarmDTO.toAlarm(): Alarm {
    return Alarm(
        id = id,
        title = title,
        hour = hour,
        minute = minute,
        amPm = amPm,
        color = color,
        repeatOn = repeatOn,
        ringtone = ringtone,
        enabled = enabled,
        ringTime = ringTime,
        snooze = snooze
    )
}

internal fun Alarm.toAlarmDTO(): AlarmDTO {
    return AlarmDTO(
        id = id,
        title = title,
        hour = hour,
        minute = minute,
        amPm = amPm,
        color = color,
        repeatOn = repeatOn,
        ringtone = ringtone,
        enabled = enabled,
        ringTime = ringTime,
        snooze = snooze
    )
}