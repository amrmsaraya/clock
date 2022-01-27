package com.github.amrmsaraya.clock.feature_alarm.data.sourceImpl

import com.github.amrmsaraya.clock.database.feature.alarm.dao.AlarmDao
import com.github.amrmsaraya.clock.database.feature.alarm.model.AlarmDTO
import com.github.amrmsaraya.clock.feature_alarm.data.source.LocalDataSource
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(private val alarmDao: AlarmDao) : LocalDataSource {
    override suspend fun insert(alarm: AlarmDTO) {
        alarmDao.insert(alarm)
    }

    override suspend fun delete(alarms: List<AlarmDTO>) {
        alarmDao.delete(alarms)
    }

    override fun getAlarms(): Flow<List<AlarmDTO>> {
        return alarmDao.getAlarms()
    }
}