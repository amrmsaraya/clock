package com.github.amrmsaraya.clock.domain.entity

data class Alarm(
    val id: Long = 0,
    val title: String = "",
    val hour: Int = 9,
    val minute: Int = 0,
    val amPm: Int = 0,
    val color: Int = 0,
    val repeatOn: List<Int> = emptyList(),
    val ringtone: String = "",
    val enabled: Boolean = true
)
