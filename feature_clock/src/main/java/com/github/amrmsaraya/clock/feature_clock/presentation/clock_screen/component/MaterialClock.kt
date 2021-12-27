package com.github.amrmsaraya.clock.feature_clock.presentation.clock_screen.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.github.amrmsaraya.clock.common.ui.theme.Red500
import com.github.amrmsaraya.clock.feature_clock.domain.entity.WorldClock
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MaterialClock(
    modifier: Modifier = Modifier,
    worldClock: WorldClock,
    frameColor: Color,
    handleColor: Color,
    showSeconds: Boolean = true
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val backgroundColor = MaterialTheme.colorScheme.surface

        Canvas(modifier = Modifier.size(if (maxWidth < maxHeight) maxWidth else maxHeight)) {
            val radius = size.width / 2
            drawCircle(
                color = frameColor,
                radius = radius,
                center = center,
                style = Stroke(radius * 0.03f, cap = StrokeCap.Round)
            )
            hourHand(
                hoursAngel = worldClock.hoursAngel,
                radius = radius,
                color = handleColor
            )
            minutesHand(
                minutesAngel = worldClock.minutesAngel,
                radius = radius,
                color = handleColor
            )
            if (showSeconds) {
                secondsHand(
                    secondsAngel = worldClock.secondsAngel,
                    radius = radius,
                    color = Red500
                )
            }
            drawCircle(
                color = handleColor,
                radius = radius * .06f,
                center = center
            )
            drawCircle(
                color = backgroundColor,
                radius = radius * .03f,
                center = center
            )
        }
    }
}

private fun DrawScope.hourHand(
    hoursAngel: Float,
    radius: Float,
    color: Color,
) {
    drawLine(
        color = color,
        start = Offset(x = center.x, y = center.y),
        end = Offset(
            x = center.x + (radius * 0.6f) * cos(hoursAngel * Math.PI.toFloat() / 180),
            y = center.y + (radius * 0.6f) * sin(hoursAngel * Math.PI.toFloat() / 180)
        ),
        strokeWidth = radius * 0.03f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.minutesHand(
    minutesAngel: Float,
    radius: Float,
    color: Color,
) {
    drawLine(
        color = color,
        start = Offset(x = center.x, y = center.y),
        end = Offset(
            x = center.x + (radius * 0.85f) * cos(minutesAngel * Math.PI.toFloat() / 180),
            y = center.y + (radius * 0.85f) * sin(minutesAngel * Math.PI.toFloat() / 180)
        ),
        strokeWidth = radius * 0.03f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.secondsHand(
    secondsAngel: Float,
    radius: Float,
    color: Color,
) {
    drawLine(
        color = color,
        start = Offset(x = center.x, y = center.y),
        end = Offset(
            x = center.x + (radius * 0.725f) * cos(secondsAngel * Math.PI.toFloat() / 180),
            y = center.y + (radius * 0.725f) * sin(secondsAngel * Math.PI.toFloat() / 180)
        ),
        strokeWidth = radius * 0.01f,
        cap = StrokeCap.Round
    )
}