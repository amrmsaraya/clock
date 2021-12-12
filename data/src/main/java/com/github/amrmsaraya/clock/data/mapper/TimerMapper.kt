package com.github.amrmsaraya.clock.data.mapper

import com.github.amrmsaraya.clock.data.model.TimerDTO
import com.github.amrmsaraya.clock.domain.entity.Timer

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