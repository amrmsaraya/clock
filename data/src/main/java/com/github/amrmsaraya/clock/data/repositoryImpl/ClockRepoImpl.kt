package com.github.amrmsaraya.clock.data.repositoryImpl

import com.github.amrmsaraya.clock.data.mapper.toClock
import com.github.amrmsaraya.clock.data.mapper.toClockDTO
import com.github.amrmsaraya.clock.data.source.ClockLocalDataSource
import com.github.amrmsaraya.clock.domain.entity.Clock
import com.github.amrmsaraya.clock.domain.repository.ClockRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ClockRepoImpl(
    private val localDataSource: ClockLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ClockRepo {
    override suspend fun insert(clock: Clock) {
        withContext(dispatcher) {
            localDataSource.insert(clock.toClockDTO())
        }
    }

    override suspend fun delete(clocks: List<Clock>) {
        withContext(dispatcher) {
            localDataSource.delete(clocks.map { it.toClockDTO() })
        }
    }

    override suspend fun getClocks(): Flow<List<Clock>> {
        return withContext(dispatcher) {
            localDataSource.getClocks().map {
                it.map { clockDTO ->
                    clockDTO.toClock()
                }
            }
        }
    }
}