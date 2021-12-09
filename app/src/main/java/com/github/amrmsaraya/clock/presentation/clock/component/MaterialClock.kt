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
import androidx.compose.ui.graphics.drawscope.Stroke
import com.github.amrmsaraya.clock.domain.entity.WorldClock
import com.github.amrmsaraya.clock.presentation.theme.Red500
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
        val mColor = MaterialTheme.colorScheme.primary
        val backgroundColor = MaterialTheme.colorScheme.surface

        Canvas(modifier = Modifier.size(if (maxWidth < maxHeight) maxWidth else maxHeight)) {
            val radius = size.width / 2
            drawCircle(
                color = frameColor,
                radius = radius,
                center = center,
                style = Stroke(radius * 0.07f, cap = StrokeCap.Round)
            )
            hourHand(
                hour = worldClock.hours,
                radius = radius,
                color = handleColor
            )
            minutesHand(
                minute = worldClock.minutes,
                radius = radius,
                color = handleColor
            )
            if (showSeconds) {
                secondsHand(
                    second = worldClock.seconds,
                    radius = radius,
                    color = Red500
                )
            }
            drawCircle(
                color = handleColor,
                radius = radius * .07f,
                center = center
            )
            drawCircle(
                color = backgroundColor,
                radius = radius * .04f,
                center = center
            )
        }
    }
}

private fun DrawScope.hourHand(
    hour: Float,
    radius: Float,
    color: Color,
) {
    drawLine(
        color = color,
        start = Offset(x = center.x, y = center.y),
        end = Offset(
            x = center.x + (radius * 0.6f) * cos(hour * Math.PI.toFloat() / 180),
            y = center.y + (radius * 0.6f) * sin(hour * Math.PI.toFloat() / 180)
        ),
        strokeWidth = radius * 0.04f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.minutesHand(
    minute: Float,
    radius: Float,
    color: Color,
) {
    drawLine(
        color = color,
        start = Offset(x = center.x, y = center.y),
        end = Offset(
            x = center.x + (radius * 0.85f) * cos(minute * Math.PI.toFloat() / 180),
            y = center.y + (radius * 0.85f) * sin(minute * Math.PI.toFloat() / 180)
        ),
        strokeWidth = radius * 0.04f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.secondsHand(
    second: Float,
    radius: Float,
    color: Color,
) {
    drawLine(
        color = color,
        start = Offset(x = center.x, y = center.y),
        end = Offset(
            x = center.x + (radius * 0.7f) * cos(second * Math.PI.toFloat() / 180),
            y = center.y + (radius * 0.7f) * sin(second * Math.PI.toFloat() / 180)
        ),
        strokeWidth = radius * 0.02f,
        cap = StrokeCap.Round
    )
}