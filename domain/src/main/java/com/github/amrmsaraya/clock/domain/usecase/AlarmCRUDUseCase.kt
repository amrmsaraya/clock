package com.github.amrmsaraya.clock.domain.usecase

import com.github.amrmsaraya.clock.domain.entity.Alarm
import com.github.amrmsaraya.clock.domain.repository.AlarmRepo
import kotlinx.coroutines.flow.Flow

class AlarmCRUDUseCase(private val alarmRepo: AlarmRepo) {

    suspend fun insert(alarm: Alarm) {
        alarmRepo.insert(alarm)
    }

    suspend fun delete(alarms: List<Alarm>) {
        alarmRepo.delete(alarms)
    }

    suspend fun getClocks(): Flow<List<Alarm>> {
        return alarmRepo.getAlarms()
    }

}