package com.github.amrmsaraya.clock.data.source

import com.github.amrmsaraya.clock.data.model.TimerDTO
import kotlinx.coroutines.flow.Flow

interface TimerLocalDataSource {
    suspend fun insert(timer: TimerDTO)
    suspend fun delete(timers: List<TimerDTO>)
    suspend fun getTimers(): Flow<List<TimerDTO>>
}