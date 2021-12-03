package com.github.amrmsaraya.clock.domain.usecase

import com.github.amrmsaraya.clock.domain.entity.Clock
import com.github.amrmsaraya.clock.domain.repository.ClockRepo
import kotlinx.coroutines.flow.Flow

class ClockCRUDUseCase(private val clockRepo: ClockRepo) {

    suspend fun insert(clock: Clock) {
        clockRepo.insert(clock)
    }

    suspend fun delete(clocks: List<Clock>) {
        clockRepo.delete(clocks)
    }

    suspend fun getClocks(): Flow<List<Clock>> {
        return clockRepo.getClocks()
    }

}