package com.github.amrmsaraya.clock.presentation.alarm.utils

import androidx.annotation.StringRes
import com.github.amrmsaraya.clock.R
import java.util.*

enum class Days(
    @StringRes val letter: Int,
    @StringRes val string: Int,
    val calendar: Int
) {
    SUNDAY(R.string.sunday, R.string.sun, Calendar.SUNDAY),
    MONDAY(R.string.monday, R.string.mon, Calendar.MONDAY),
    TUESDAY(R.string.tuesday, R.string.tue, Calendar.TUESDAY),
    WEDNESDAY(R.string.wednesday, R.string.wed, Calendar.WEDNESDAY),
    THURSDAY(R.string.thursday, R.string.thu, Calendar.THURSDAY),
    FRIDAY(R.string.friday, R.string.fri, Calendar.FRIDAY),
    SATURDAY(R.string.saturday, R.string.sat, Calendar.SATURDAY),
}