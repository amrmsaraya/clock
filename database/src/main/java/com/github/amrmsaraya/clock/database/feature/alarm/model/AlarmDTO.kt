package com.github.amrmsaraya.clock.database.feature.alarm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm")
data class AlarmDTO(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val hour: Int = 0,
    val minute: Int = 0,
    val amPm: Int = 0,
    val color: Int = 0,
    val repeatOn: List<Int> = emptyList(),
    val ringtone: String = "",
    val enabled: Boolean = true,
    val ringTime: Long = 0,
    val snooze: Long = 5 * 60 * 1000
)
