package com.github.amrmsaraya.clock.data.repositoryImpl

import com.github.amrmsaraya.clock.data.mapper.toAlarm
import com.github.amrmsaraya.clock.data.mapper.toAlarmDTO
import com.github.amrmsaraya.clock.data.source.AlarmLocalDataSource
import com.github.amrmsaraya.clock.domain.entity.Alarm
import com.github.amrmsaraya.clock.domain.repository.AlarmRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AlarmRepoImpl(
    private val localDataSource: AlarmLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AlarmRepo {
    override suspend fun insert(alarm: Alarm) {
        withContext(dispatcher) {
            localDataSource.insert(alarm.toAlarmDTO())
        }
    }

    override suspend fun delete(alarms: List<Alarm>) {
        withContext(dispatcher) {
            localDataSource.delete(alarms.map { it.toAlarmDTO() })
        }
    }

    override suspend fun getAlarms(): Flow<List<Alarm>> {
        return withContext(dispatcher) {
            localDataSource.getAlarms().map {
                it.map { alarmDTO ->
                    alarmDTO.toAlarm()
                }
            }
        }
    }

}