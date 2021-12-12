package com.github.amrmsaraya.clock.data.repositoryImpl

import com.github.amrmsaraya.clock.data.mapper.toTimer
import com.github.amrmsaraya.clock.data.mapper.toTimerDTO
import com.github.amrmsaraya.clock.data.source.TimerLocalDataSource
import com.github.amrmsaraya.clock.domain.entity.Timer
import com.github.amrmsaraya.clock.domain.repository.TimerRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TimerRepoImpl(private val localDataSource: TimerLocalDataSource) : TimerRepo {
    override suspend fun insert(timer: Timer) {
        localDataSource.insert(timer.toTimerDTO())
    }

    override suspend fun delete(timers: List<Timer>) {
        localDataSource.delete(timers.map { it.toTimerDTO() })
    }

    override suspend fun getTimers(): Flow<List<Timer>> {
        return localDataSource.getTimers().map {
            it.map { timerDTO ->
                timerDTO.toTimer()
            }
        }
    }

}