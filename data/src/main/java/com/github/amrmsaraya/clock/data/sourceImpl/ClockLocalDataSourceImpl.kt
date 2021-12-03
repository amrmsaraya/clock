package com.github.amrmsaraya.clock.data.sourceImpl

import com.github.amrmsaraya.clock.data.local.ClockDao
import com.github.amrmsaraya.clock.data.model.ClockDTO
import com.github.amrmsaraya.clock.data.source.ClockLocalDataSource
import kotlinx.coroutines.flow.Flow

class ClockLocalDataSourceImpl(private val clockDao: ClockDao) : ClockLocalDataSource {
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