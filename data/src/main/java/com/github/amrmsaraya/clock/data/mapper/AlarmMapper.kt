package com.github.amrmsaraya.clock.data.mapper

import com.github.amrmsaraya.clock.data.model.AlarmDTO
import com.github.amrmsaraya.clock.domain.entity.Alarm

internal fun AlarmDTO.toAlarm(): Alarm {
    return Alarm(
        title = title,
        hour = hour,
        minute = minute,
        amPm = amPm,
        color = color,
        repeatOn = repeatOn,
        enabled = enabled
    )
}

internal fun Alarm.toAlarmDTO(): AlarmDTO {
    return AlarmDTO(
        title = title,
        hour = hour,
        minute = minute,
        amPm = amPm,
        color = color,
        repeatOn = repeatOn,
        enabled = enabled
    )
}