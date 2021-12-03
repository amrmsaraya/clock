package com.github.amrmsaraya.clock.data.source

import com.github.amrmsaraya.clock.data.model.ClockDTO
import kotlinx.coroutines.flow.Flow

interface ClockLocalDataSource {
    suspend fun insert(clock: ClockDTO)
    suspend fun delete(clocks: List<ClockDTO>)
    suspend fun getClocks(): Flow<List<ClockDTO>>
}