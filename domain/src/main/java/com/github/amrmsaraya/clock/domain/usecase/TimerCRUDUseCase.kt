package com.github.amrmsaraya.clock.domain.usecase

import com.github.amrmsaraya.clock.domain.entity.Timer
import com.github.amrmsaraya.clock.domain.repository.TimerRepo
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