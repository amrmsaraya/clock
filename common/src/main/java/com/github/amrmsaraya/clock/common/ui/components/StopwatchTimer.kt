package com.github.amrmsaraya.clock.common.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.github.amrmsaraya.timer.Stopwatch
import com.github.amrmsaraya.timer.Time

@Composable
fun StopwatchTimer(
    modifier: Modifier = Modifier,
    timer: Time,
    sliderValue: Float,
    animatedValue: Float,
    status: Int
) {
    val color = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.outline

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stopwatchTimerFormat(timer, color),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Canvas(modifier = Modifier.size(if (maxWidth < maxHeight) maxWidth else maxHeight)) {
            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = animatedValue * 360,
                useCenter = false,
                style = Stroke(10f, cap = StrokeCap.Round),
                size = Size(size.width, size.width),
                alpha = 0.3f
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sliderValue * 360,
                useCenter = false,
                style = Stroke(10f, cap = StrokeCap.Round),
                size = Size(size.width, size.width),
                alpha = if (status == Stopwatch.RUNNING) 1f else 0.5f
            )
        }
    }
}

fun stopwatchTimerFormat(
    time: Time,
    color: Color? = null,
    withMillis: Boolean = true
): AnnotatedString {
    val format: (Int) -> String = { "%02d".format(it) }
    return buildAnnotatedString {
        append("${format(time.hours)}:${format(time.minutes)}:${format(time.seconds)}")
        if (withMillis) {
            color?.let {
                withStyle(style = SpanStyle(color = color, fontSize = 26.sp)) {
                    append(".${format(time.millis / 10)}")
                }
            } ?: append(":${format(time.millis / 10)}")
        }
    }
}