package com.github.amrmsaraya.timer

/**
 * Extension function to [Long] which converts the timeInMillis to [Time]
 * @return [Time] version of the timeInMillis
 */
fun Long.toTime(): Time {
    val days = this / (3_600_000 * 24)
    val hours = this / 3_600_000 - days * 24
    val minutes = this / 60_000 - (days * 24 + hours) * 60
    val seconds = (this / 1000) - (days * 24 * 60 + hours * 60 + minutes) * 60
    val millis = this % 1000

    return Time(
        timeInMillis = this,
        days = days.toInt(),
        hours = hours.toInt(),
        minutes = minutes.toInt(),
        seconds = seconds.toInt(),
        millis = millis.toInt()
    )
}