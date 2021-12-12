package com.github.amrmsaraya.clock.data.sourceImpl

import com.github.amrmsaraya.clock.data.local.TimerDao
import com.github.amrmsaraya.clock.data.model.AlarmDTO
import com.github.amrmsaraya.clock.data.model.TimerDTO
import com.github.amrmsaraya.clock.data.source.TimerLocalDataSource
import kotlinx.coroutines.flow.Flow

class TimerLocalDataSourceImpl(private val timerDao: TimerDao) : TimerLocalDataSource {
    override suspend fun insert(timer: TimerDTO) {
        timerDao.insert(timer)
    }

    override suspend fun delete(timer: TimerDTO) {
        timerDao.delete(timer)
    }

    override suspend fun getTimers(): Flow<List<TimerDTO>> {
        return timerDao.getTimers()
    }

}