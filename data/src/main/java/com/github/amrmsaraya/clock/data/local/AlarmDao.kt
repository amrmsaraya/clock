package com.github.amrmsaraya.clock.data.local

import androidx.room.*
import com.github.amrmsaraya.clock.data.model.AlarmDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: AlarmDTO)

    @Delete
    suspend fun delete(alarms: List<AlarmDTO>)

    @Query("SELECT * FROM alarm")
    fun getAlarms(): Flow<List<AlarmDTO>>
}
