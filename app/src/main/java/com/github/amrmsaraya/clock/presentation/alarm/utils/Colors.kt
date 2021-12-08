package com.github.amrmsaraya.clock.presentation.alarm.utils

import androidx.compose.ui.graphics.Color
import com.github.amrmsaraya.clock.presentation.theme.*

enum class Colors(
    val activeBackgroundColor: Color,
    val inActiveBackgroundColor: Color,
    val contentColor: Color
) {
    BLUE(Blue200, Blue50, Blue900),
    PURPLE(Purple200, Purple50, Purple900),
    PINK(Pink200, Pink50, Pink900),
    ORANGE(Orange200, Orange50, Orange900),
    TEAL(Teal200, Teal50, Teal900),
    GREEN(Green200, Green50, Green900),
    AMBER(Amber200, Amber50, Amber900),
    BLUE_GRAY(BlueGray200, BlueGray50, BlueGray900),
}