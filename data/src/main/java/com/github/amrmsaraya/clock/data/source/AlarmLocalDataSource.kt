package com.github.amrmsaraya.clock.data.source

import com.github.amrmsaraya.clock.data.model.AlarmDTO
import kotlinx.coroutines.flow.Flow

interface AlarmLocalDataSource {
    suspend fun insert(alarm: AlarmDTO)
    suspend fun delete(alarms: List<AlarmDTO>)
    suspend fun getAlarms(): Flow<List<AlarmDTO>>
}