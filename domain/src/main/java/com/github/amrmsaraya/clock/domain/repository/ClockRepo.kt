package com.github.amrmsaraya.clock.domain.repository

import com.github.amrmsaraya.clock.domain.entity.Clock
import kotlinx.coroutines.flow.Flow

interface ClockRepo {
    suspend fun insert(clock: Clock)
    suspend fun delete(clocks: List<Clock>)
    suspend fun getClocks(): Flow<List<Clock>>
}