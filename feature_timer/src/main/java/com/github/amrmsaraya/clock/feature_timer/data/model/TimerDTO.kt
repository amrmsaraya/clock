package com.github.amrmsaraya.clock.feature_timer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timer")
data class TimerDTO(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val timeMillis: Long
)
