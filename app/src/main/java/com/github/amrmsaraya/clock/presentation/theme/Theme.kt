package com.github.amrmsaraya.clock.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = Purple400,
    secondary = Purple400,
    background = Purple,
    surface = Purple,
)

private val LightColorPalette = lightColors(
    primary = Purple400,
    secondary = Purple400,
)

@Composable
fun ClockTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(color = colors.surface)
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}