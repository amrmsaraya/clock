package com.github.amrmsaraya.clock.presentation.common_ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import kotlin.math.ln

fun getSurfaceColor(
    elevation: Dp,
    primaryColor: Color,
    surfaceColor: Color
): Color {
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return primaryColor.copy(alpha = alpha).compositeOver(surfaceColor)
}