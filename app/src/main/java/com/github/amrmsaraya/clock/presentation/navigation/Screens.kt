package com.github.amrmsaraya.clock.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.amrmsaraya.clock.R

sealed class Screens(
    val route: String,
    @StringRes val title: Int,
    val activeIcon: ImageVector = Icons.Outlined.WatchLater,
    val inactiveIcon: ImageVector = Icons.Outlined.WatchLater
) {
    object Clock : Screens(
        route = "clock",
        title = R.string.clock,
        activeIcon = Icons.Outlined.WatchLater,
        inactiveIcon = Icons.Outlined.WatchLater
    )

    object Alarm : Screens(
        route = "alarm",
        title = R.string.alarm,
        activeIcon = Icons.Outlined.Alarm,
        inactiveIcon = Icons.Outlined.Alarm
    )

    object Stopwatch : Screens(
        route = "stopwatch",
        title = R.string.stopwatch,
        activeIcon = Icons.Outlined.Timer,
        inactiveIcon = Icons.Outlined.Timer
    )

    object Timer : Screens(
        route = "timer",
        title = R.string.timer,
        activeIcon = Icons.Outlined.HourglassEmpty,
        inactiveIcon = Icons.Outlined.HourglassEmpty
    )
}