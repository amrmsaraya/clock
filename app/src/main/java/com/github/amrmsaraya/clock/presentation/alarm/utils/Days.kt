package com.github.amrmsaraya.clock.presentation.alarm.utils

import androidx.annotation.StringRes
import com.github.amrmsaraya.clock.R

enum class Days(@StringRes val stringRes: Int) {
    SATURDAY(R.string.saturday),
    SUNDAY(R.string.sunday),
    MONDAY(R.string.monday),
    TUESDAY(R.string.tuesday),
    WEDNESDAY(R.string.wednesday),
    THURSDAY(R.string.thursday),
    FRIDAY(R.string.friday),
}