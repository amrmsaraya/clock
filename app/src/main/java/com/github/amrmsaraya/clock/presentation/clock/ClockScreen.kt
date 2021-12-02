package com.github.amrmsaraya.clock.presentation.clock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.github.amrmsaraya.clock.presentation.theme.Red500
import kotlinx.coroutines.delay
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ClockScreen(modifier: Modifier) {
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    val second =
        (calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND)) * 6 / 1000f + 270
    val minute = calendar.get(Calendar.MINUTE) * 6 + 270 + calendar.get(Calendar.SECOND) / 10f
    val hour = calendar.get(Calendar.HOUR) * 30 + 270 + calendar.get(Calendar.MINUTE) / 2f

    LaunchedEffect(key1 = calendar) {
        delay(10)
        calendar = Calendar.getInstance()
    }

    Clock(
        modifier = modifier.fillMaxSize(),
        hour = hour,
        minute = minute,
        second = second,
        color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
    )

}

@Composable
private fun Clock(
    modifier: Modifier = Modifier,
    hour: Float,
    minute: Float,
    second: Float,
    color: Color
) {
    BoxWithConstraints(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(if (maxWidth < maxHeight) maxWidth else maxHeight)) {
            val radius = size.width / 2

            hoursDial(radius, color)
            minutesDial(radius, color)
            hourHand(
                hour = hour,
                radius = radius,
                color = color
            )
            minutesHand(
                minute = minute,
                radius = radius,
                color = color
            )
            secondsHand(
                second = second,
                radius = radius,
                color = Red500
            )
            drawCircle(
                color = color,
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
    hour: Float,
    radius: Float,
    color: Color,
) {
    drawLine(
        color = color,
        start = Offset(x = center.x, y = center.y),
        end = Offset(
            x = center.x + (radius * 0.533f) * cos(hour * Math.PI.toFloat() / 180),
            y = center.y + (radius * 0.533f) * sin(hour * Math.PI.toFloat() / 180)
        ),
        strokeWidth = radius * 0.015f,
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
            x = center.x + (radius * 0.8f) * cos(minute * Math.PI.toFloat() / 180),
            y = center.y + (radius * 0.8f) * sin(minute * Math.PI.toFloat() / 180)
        ),
        strokeWidth = radius * 0.015f,
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
            x = center.x + (radius * 0.66f) * cos(second * Math.PI.toFloat() / 180),
            y = center.y + (radius * 0.66f) * sin(second * Math.PI.toFloat() / 180)
        ),
        strokeWidth = radius * 0.01f,
        cap = StrokeCap.Round
    )
}