package com.github.amrmsaraya.clock.data.repositoryImpl

import com.github.amrmsaraya.clock.data.mapper.toAlarm
import com.github.amrmsaraya.clock.data.mapper.toAlarmDTO
import com.github.amrmsaraya.clock.data.source.AlarmLocalDataSource
import com.github.amrmsaraya.clock.domain.entity.Alarm
import com.github.amrmsaraya.clock.domain.repository.AlarmRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlarmRepoImpl(private val localDataSource: AlarmLocalDataSource) : AlarmRepo {
    override suspend fun insert(alarm: Alarm) {
        localDataSource.insert(alarm.toAlarmDTO())
    }

    override suspend fun delete(alarms: List<Alarm>) {
        localDataSource.delete(alarms.map { it.toAlarmDTO() })
    }

    override suspend fun getAlarms(): Flow<List<Alarm>> {
        return localDataSource.getAlarms().map {
            it.map { alarmDTO ->
                alarmDTO.toAlarm()
            }
        }
    }

}