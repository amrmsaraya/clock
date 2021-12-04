package com.github.amrmsaraya.clock.domain.repository

import com.github.amrmsaraya.clock.domain.entity.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepo {
    suspend fun insert(alarm: Alarm)
    suspend fun delete(alarms: List<Alarm>)
    suspend fun getAlarms(): Flow<List<Alarm>>
}