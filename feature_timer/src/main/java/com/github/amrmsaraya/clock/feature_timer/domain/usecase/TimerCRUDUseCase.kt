package com.github.amrmsaraya.clock.feature_timer.domain.usecase

import com.github.amrmsaraya.clock.feature_timer.domain.entity.Timer
import com.github.amrmsaraya.clock.feature_timer.domain.repository.TimerRepo
import kotlinx.coroutines.flow.Flow

class TimerCRUDUseCase(private val timerRepo: TimerRepo) {

    suspend fun insert(timer: Timer) {
        timerRepo.insert(timer)
    }

    suspend fun delete(timer: Timer) {
        timerRepo.delete(timer)
    }

    suspend fun getTimers(): Flow<List<Timer>> {
        return timerRepo.getTimers()
    }

}