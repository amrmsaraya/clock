package com.github.amrmsaraya.clock.feature_alarm.data.source

import com.github.amrmsaraya.clock.database.feature.alarm.model.AlarmDTO
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun insert(alarm: AlarmDTO)
    suspend fun delete(alarms: List<AlarmDTO>)
    fun getAlarms(): Flow<List<AlarmDTO>>
}