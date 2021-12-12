package com.github.amrmsaraya.clock.data.local

import androidx.room.*
import com.github.amrmsaraya.clock.data.model.TimerDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timer: TimerDTO)

    @Delete
    suspend fun delete(timer: TimerDTO)

    @Query("SELECT * FROM timer")
    fun getTimers(): Flow<List<TimerDTO>>
}
