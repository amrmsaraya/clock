package com.github.amrmsaraya.clock.feature_clock.data.sourceImpl

import com.github.amrmsaraya.clock.feature_clock.data.local.ClockDao
import com.github.amrmsaraya.clock.feature_clock.data.model.ClockDTO
import com.github.amrmsaraya.clock.feature_clock.data.source.LocalDataSource
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(private val clockDao: ClockDao) : LocalDataSource {
    override suspend fun insert(clock: ClockDTO) {
        clockDao.insert(clock)
    }

    override suspend fun delete(clocks: List<ClockDTO>) {
        clockDao.delete(clocks)
    }

    override suspend fun getClocks(): Flow<List<ClockDTO>> {
        return clockDao.getClocks()
    }
}