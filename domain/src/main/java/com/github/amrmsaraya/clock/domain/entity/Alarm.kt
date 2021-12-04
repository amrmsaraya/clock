package com.github.amrmsaraya.clock.domain.entity

data class Alarm(
    val title: String,
    val hour: Int,
    val minute: Int,
    val amPm: Int,
    val color: Int,
    val repeatOn: List<Int>,
    val enabled: Boolean = true
)
