package com.github.amrmsaraya.clock.domain.repository

import com.github.amrmsaraya.clock.domain.entity.Timer
import kotlinx.coroutines.flow.Flow

interface TimerRepo {
    suspend fun insert(timer: Timer)
    suspend fun delete(timer: Timer)
    suspend fun getTimers(): Flow<List<Timer>>
}