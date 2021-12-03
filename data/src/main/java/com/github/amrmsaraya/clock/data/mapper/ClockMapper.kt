package com.github.amrmsaraya.clock.data.mapper

import com.github.amrmsaraya.clock.data.model.ClockDTO
import com.github.amrmsaraya.clock.domain.entity.Clock

internal fun ClockDTO.toClock(): Clock {
    return Clock(id, displayName)
}

internal fun Clock.toClockDTO(): ClockDTO {
    return ClockDTO(id, displayName)
}