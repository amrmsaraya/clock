package com.github.amrmsaraya.clock.feature_alarm.data.source

import com.github.amrmsaraya.clock.feature_alarm.data.model.AlarmDTO
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun insert(alarm: AlarmDTO)
    suspend fun delete(alarms: List<AlarmDTO>)
    suspend fun getAlarms(): Flow<List<AlarmDTO>>
}