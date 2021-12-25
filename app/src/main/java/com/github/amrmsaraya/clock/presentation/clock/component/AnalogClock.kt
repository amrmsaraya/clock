package com.github.amrmsaraya.clock.presentation.clock.component

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
import com.github.amrmsaraya.clock.domain.entity.WorldClock
import com.github.amrmsaraya.clock.presentation.theme.Red500
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalogClock(
    modifier: Modifier = Modifier,
    worldClock: WorldClock,
    color: Color,
    showSeconds: Boolean = true
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val backgroundColor = MaterialTheme.colorScheme.surface

        Canvas(modifier = Modifier.size(if (maxWidth < maxHeight) maxWidth else maxHeight)) {
            val radius = size.width / 2

            hoursDial(radius, color)
            minutesDial(radius, color)
            hourHand(
                hoursAngel = worldClock.hoursAngel,
                radius = radius,
                color = color
            )
            minutesHand(
                minutesAngel = worldClock.minutesAngel,
                radius = radius,
                color = color
            )
            if (showSeconds) {
                secondsHand(
                    secondsAngel = worldClock.secondsAngel,
                    radius = radius,
                    color = Red500
                )
            }
            drawCircle(
                color = color,
                radius = radius * .04f,
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

private fun DrawScope.hoursDial(radius: Float, color: Color) {
    for (angle in 0..360 step 30) {
        drawLine(
            color = color,
            start = Offset(
                x = center.x + (radius) * cos(angle * Math.PI.toFloat() / 180),
                y = center.y + (radius) * sin(angle * Math.PI.toFloat() / 180)
            ),
            end = Offset(
                x = center.x + (radius - radius * 0.125f) * cos(angle * Math.PI.toFloat() / 180),
                y = center.y + (radius - radius * 0.125f) * sin(angle * Math.PI.toFloat() / 180)
            ),
            strokeWidth = radius * 0.01f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.minutesDial(radius: Float, color: Color) {
    for (angle in 0..360 step 6) {
        drawLine(
            color = color,
            start = Offset(
                x = center.x + (radius) * cos(angle * Math.PI.toFloat() / 180),
                y = center.y + (radius) * sin(angle * Math.PI.toFloat() / 180)
            ),
            end = Offset(
                x = center.x + (radius - radius * 0.05f) * cos(angle * Math.PI.toFloat() / 180),
                y = center.y + (radius - radius * 0.05f) * sin(angle * Math.PI.toFloat() / 180)
            ),
            strokeWidth = radius * 0.01f,
            cap = StrokeCap.Round
        )
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
            x = center.x + (radius * 0.533f) * cos(hoursAngel * Math.PI.toFloat() / 180),
            y = center.y + (radius * 0.533f) * sin(hoursAngel * Math.PI.toFloat() / 180)
        ),
        strokeWidth = radius * 0.015f,
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
            x = center.x + (radius * 0.8f) * cos(minutesAngel * Math.PI.toFloat() / 180),
            y = center.y + (radius * 0.8f) * sin(minutesAngel * Math.PI.toFloat() / 180)
        ),
        strokeWidth = radius * 0.015f,
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
            x = center.x + (radius * 0.66f) * cos(secondsAngel * Math.PI.toFloat() / 180),
            y = center.y + (radius * 0.66f) * sin(secondsAngel * Math.PI.toFloat() / 180)
        ),
        strokeWidth = radius * 0.01f,
        cap = StrokeCap.Round
    )
}