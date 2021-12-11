package com.github.amrmsaraya.timer

/**
 * Time class is a class which holds time in an easy formatted shape
 * @param timeInMillis The current timer in milliseconds
 * @param days How many days in timer
 * @param hours How many hours in timer
 * @param minutes How many minutes in timer
 * @param seconds How many seconds in timer
 * @param millis How many milliseconds in timer
 */
data class Time(
    /** The current timer in milliseconds */
    val timeInMillis: Long = 0,

    /** How many days in timer */
    val days: Int = 0,

    /** How many hours in timer */
    val hours: Int = 0,

    /** How many minutes in timer */
    val minutes: Int = 0,

    /** How many seconds in timer */
    val seconds: Int = 0,

    /** How many milliseconds in timer */
    val millis: Int = 0
)
