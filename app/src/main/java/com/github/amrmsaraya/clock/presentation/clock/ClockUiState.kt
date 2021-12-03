package com.github.amrmsaraya.clock.presentation.clock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

data class ClockUiState(
    val time: Flow<Time> = flow { },
    val clocks: Flow<Map<TimeZone, Time>> = flow { },
    val timeZones: List<TimeZone> = emptyList()
)
