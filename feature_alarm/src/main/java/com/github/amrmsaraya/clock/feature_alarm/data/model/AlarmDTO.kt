package com.github.amrmsaraya.clock.feature_alarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm")
data class AlarmDTO(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val hour: Int,
    val minute: Int,
    val amPm: Int,
    val color: Int,
    val repeatOn: List<Int>,
    val ringtone: String,
    val enabled: Boolean = true,
    val ringTime: Long = 0,
    val snooze: Long = 5 * 60 * 1000
)
