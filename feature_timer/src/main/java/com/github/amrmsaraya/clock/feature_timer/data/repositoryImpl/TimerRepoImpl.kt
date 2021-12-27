package com.github.amrmsaraya.clock.feature_timer.data.repositoryImpl

import com.github.amrmsaraya.clock.feature_timer.data.mapper.toTimer
import com.github.amrmsaraya.clock.feature_timer.data.mapper.toTimerDTO
import com.github.amrmsaraya.clock.feature_timer.data.source.LocalDataSource
import com.github.amrmsaraya.clock.feature_timer.domain.entity.Timer
import com.github.amrmsaraya.clock.feature_timer.domain.repository.TimerRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TimerRepoImpl(
    private val localDataSource: LocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : TimerRepo {
    override suspend fun insert(timer: Timer) {
        withContext(dispatcher) {
            localDataSource.insert(timer.toTimerDTO())
        }
    }

    override suspend fun delete(timer: Timer) {
        withContext(dispatcher) {
            localDataSource.delete(timer.toTimerDTO())
        }
    }

    override suspend fun getTimers(): Flow<List<Timer>> {
        return withContext(dispatcher) {
            localDataSource.getTimers().map {
                it.map { timerDTO ->
                    timerDTO.toTimer()
                }
            }
        }
    }

}