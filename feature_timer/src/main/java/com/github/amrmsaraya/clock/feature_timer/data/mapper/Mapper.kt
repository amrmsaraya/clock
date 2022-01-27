package com.github.amrmsaraya.clock.feature_timer.data.mapper

import com.github.amrmsaraya.clock.database.feature.timer.model.TimerDTO
import com.github.amrmsaraya.clock.feature_timer.domain.entity.Timer

fun TimerDTO.toTimer(): Timer {
    return Timer(
        id = id,
        title = title,
        timeMillis = timeMillis
    )
}

fun Timer.toTimerDTO(): TimerDTO {
    return TimerDTO(
        id = id,
        title = title,
        timeMillis = timeMillis
    )
}