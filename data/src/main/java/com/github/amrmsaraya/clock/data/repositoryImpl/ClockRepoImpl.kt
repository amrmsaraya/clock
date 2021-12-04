package com.github.amrmsaraya.clock.data.repositoryImpl

import com.github.amrmsaraya.clock.data.mapper.toClock
import com.github.amrmsaraya.clock.data.mapper.toClockDTO
import com.github.amrmsaraya.clock.data.source.ClockLocalDataSource
import com.github.amrmsaraya.clock.domain.entity.Clock
import com.github.amrmsaraya.clock.domain.repository.ClockRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClockRepoImpl(private val localDataSource: ClockLocalDataSource) : ClockRepo {
    override suspend fun insert(clock: Clock) {
        localDataSource.insert(clock.toClockDTO())
    }

    override suspend fun delete(clocks: List<Clock>) {
        localDataSource.delete(clocks.map { it.toClockDTO() })
    }

    override suspend fun getClocks(): Flow<List<Clock>> {
        return localDataSource.getClocks().map {
            it.map { clockDTO ->
                clockDTO.toClock()
            }
        }
    }
}