package com.github.amrmsaraya.clock.utils

import android.util.LayoutDirection
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.core.text.layoutDirection
import java.text.NumberFormat
import java.util.*


fun Long.format(): String {
    return NumberFormat.getInstance().format(this)
}

fun Int.format(): String {
    return NumberFormat.getInstance().format(this)
}

fun Modifier.mirror(): Modifier {
    return when (Locale.getDefault().layoutDirection) {
        LayoutDirection.RTL -> scale(scaleX = -1f, scaleY = 1f)
        else -> this
    }
}