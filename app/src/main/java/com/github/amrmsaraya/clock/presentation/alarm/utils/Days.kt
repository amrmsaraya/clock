package com.github.amrmsaraya.clock.presentation.alarm.utils

import androidx.annotation.StringRes
import com.github.amrmsaraya.clock.R
import java.util.*

enum class Days(
    @StringRes val letter: Int,
    @StringRes val string: Int,
    val calendar: Int
) {
    SUNDAY(R.string.s, R.string.sun, Calendar.SUNDAY),
    MONDAY(R.string.m, R.string.mon, Calendar.MONDAY),
    TUESDAY(R.string.t, R.string.tue, Calendar.TUESDAY),
    WEDNESDAY(R.string.w, R.string.wed, Calendar.WEDNESDAY),
    THURSDAY(R.string.t, R.string.thu, Calendar.THURSDAY),
    FRIDAY(R.string.f, R.string.fri, Calendar.FRIDAY),
    SATURDAY(R.string.s, R.string.sat, Calendar.SATURDAY),
}