package com.github.amrmsaraya.clock.feature_clock.presentation.clock_screen

import com.github.amrmsaraya.clock.feature_clock.domain.entity.WorldClock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

data class ClockUiState(
    val localClock: Flow<WorldClock> = flow { },
    val worldClocks: Flow<Map<TimeZone, WorldClock>> = flow { },
    val timeZones: List<TimeZone> = emptyList()
)
