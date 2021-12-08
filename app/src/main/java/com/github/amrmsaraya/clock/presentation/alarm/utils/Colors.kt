package com.github.amrmsaraya.clock.presentation.alarm.utils

import androidx.compose.ui.graphics.Color
import com.github.amrmsaraya.clock.presentation.theme.*

enum class Colors(
    val activeBackgroundColor: Color,
    val inActiveBackgroundColor: Color,
    val contentColor: Color
) {
    PURPLE(Purple200, Purple100, Purple900),
    TEAL(Teal200, Teal100, Teal900),
    ORANGE(Orange200, Orange100, Orange900),
    BLUE_GRAY(BlueGray200, BlueGray100, BlueGray900),
    BLUE(Blue200, Blue100, Blue900),
    PINK(Pink200, Pink100, Pink900),
}