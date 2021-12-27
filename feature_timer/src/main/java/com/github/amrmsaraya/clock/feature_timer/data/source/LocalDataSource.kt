package com.github.amrmsaraya.clock.feature_timer.data.source

import com.github.amrmsaraya.clock.feature_timer.data.model.TimerDTO
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun insert(timer: TimerDTO)
    suspend fun delete(timer: TimerDTO)
    suspend fun getTimers(): Flow<List<TimerDTO>>
}