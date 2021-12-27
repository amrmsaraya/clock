package com.github.amrmsaraya.clock.feature_clock.data.local

import androidx.room.*
import com.github.amrmsaraya.clock.feature_clock.data.model.ClockDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface ClockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(clock: ClockDTO)

    @Delete
    suspend fun delete(clocks: List<ClockDTO>)

    @Query("SELECT * FROM clock")
    fun getClocks(): Flow<List<ClockDTO>>
}
