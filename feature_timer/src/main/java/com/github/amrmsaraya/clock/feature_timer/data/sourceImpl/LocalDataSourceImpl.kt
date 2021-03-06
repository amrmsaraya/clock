package com.github.amrmsaraya.clock.feature_timer.data.sourceImpl

import com.github.amrmsaraya.clock.database.feature.timer.dao.TimerDao
import com.github.amrmsaraya.clock.database.feature.timer.model.TimerDTO
import com.github.amrmsaraya.clock.feature_timer.data.source.LocalDataSource
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(private val timerDao: TimerDao) : LocalDataSource {
    override suspend fun insert(timer: TimerDTO) {
        timerDao.insert(timer)
    }

    override suspend fun delete(timer: TimerDTO) {
        timerDao.delete(timer)
    }

    override fun getTimers(): Flow<List<TimerDTO>> {
        return timerDao.getTimers()
    }

}