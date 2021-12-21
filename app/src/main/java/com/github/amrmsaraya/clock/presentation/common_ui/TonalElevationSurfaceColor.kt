package com.github.amrmsaraya.clock.presentation.common_ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import kotlin.math.ln

@Composable
fun getSurfaceColor(elevation: Dp): Color {
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return MaterialTheme.colorScheme.primary.copy(alpha = alpha)
        .compositeOver(MaterialTheme.colorScheme.surface)
}