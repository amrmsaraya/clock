package com.github.amrmsaraya.clock.data.sourceImpl

import com.github.amrmsaraya.clock.data.local.AlarmDao
import com.github.amrmsaraya.clock.data.model.AlarmDTO
import com.github.amrmsaraya.clock.data.source.AlarmLocalDataSource
import kotlinx.coroutines.flow.Flow

class AlarmLocalDataSourceImpl(private val alarmDao: AlarmDao) : AlarmLocalDataSource {
    override suspend fun insert(alarm: AlarmDTO) {
        alarmDao.insert(alarm)
    }

    override suspend fun delete(alarms: List<AlarmDTO>) {
        alarmDao.delete(alarms)
    }

    override suspend fun getAlarms(): Flow<List<AlarmDTO>> {
        return alarmDao.getAlarms()
    }
}