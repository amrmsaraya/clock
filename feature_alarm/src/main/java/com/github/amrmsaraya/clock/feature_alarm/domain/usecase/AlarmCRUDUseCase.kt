package com.github.amrmsaraya.clock.feature_alarm.domain.usecase

import com.github.amrmsaraya.clock.feature_alarm.domain.entity.Alarm
import com.github.amrmsaraya.clock.feature_alarm.domain.repository.AlarmRepo
import kotlinx.coroutines.flow.Flow

class AlarmCRUDUseCase(private val alarmRepo: AlarmRepo) {

    suspend fun insert(alarm: Alarm) {
        alarmRepo.insert(alarm)
    }

    suspend fun delete(alarms: List<Alarm>) {
        alarmRepo.delete(alarms)
    }

    suspend fun getAlarms(): Flow<List<Alarm>> {
        return alarmRepo.getAlarms()
    }

}