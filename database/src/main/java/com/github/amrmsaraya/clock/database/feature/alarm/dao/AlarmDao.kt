package com.github.amrmsaraya.clock.database.feature.alarm.dao

import androidx.room.*
import com.github.amrmsaraya.clock.database.feature.alarm.model.AlarmDTO
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
