package com.github.amrmsaraya.clock.feature_clock.data.source

import com.github.amrmsaraya.clock.database.feature.clock.model.ClockDTO
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun insert(clock: ClockDTO)
    suspend fun delete(clocks: List<ClockDTO>)
    suspend fun getClocks(): Flow<List<ClockDTO>>
}