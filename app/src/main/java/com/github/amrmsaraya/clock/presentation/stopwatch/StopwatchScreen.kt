package com.github.amrmsaraya.clock.presentation.stopwatch

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.timer.Time

@Composable
fun StopwatchScreen(
    modifier: Modifier,
    viewModel: StopwatchViewModel = viewModel()
) {
    val uiState by viewModel.uiState
    val sliderValue = (uiState.stopwatch.seconds * 1000 + uiState.stopwatch.millis) / 60_000f

    var animatedValue by remember { mutableStateOf(0f) }
    val animatedCircle by animateFloatAsState(
        targetValue = animatedValue,
        animationSpec = tween(1000)
    )

    LaunchedEffect(key1 = true) {
        animatedValue = 1f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        StopwatchTimer(uiState.stopwatch, sliderValue, animatedCircle, uiState.isRunning)
        Spacer(modifier = Modifier.size(16.dp))
        ControlRow(
            isRunning = uiState.isRunning,
            onStart = { viewModel.sendIntent(StopwatchIntent.Start) },
            onPause = { viewModel.sendIntent(StopwatchIntent.Pause) },
            onReset = { viewModel.sendIntent(StopwatchIntent.Reset) }
        )
    }
}

@Composable
fun ControlRow(
    isRunning: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        OutlinedButton(
            onClick = onReset,
            shape = CircleShape
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
                text = stringResource(R.string.reset).uppercase(),
            )
        }

        Button(
            onClick = { if (isRunning) onPause() else onStart() },
            shape = CircleShape
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
                text = if (isRunning) {
                    stringResource(R.string.pause).uppercase()
                } else {
                    stringResource(R.string.start).uppercase()
                }
            )
        }
    }
}

@Composable
fun StopwatchTimer(
    timer: Time,
    sliderValue: Float,
    animatedValue: Float,
    isRunning: Boolean = true
) {
    val color = MaterialTheme.colors.primary
    val isDarkTheme = isSystemInDarkTheme()

    BoxWithConstraints(contentAlignment = Alignment.Center) {
        Text(
            text = stopwatchFormat(timer, color),
            fontSize = 32.sp
        )

        Canvas(modifier = Modifier.size(if (maxWidth < maxHeight) maxWidth else maxHeight)) {
            drawArc(
                color = if (isDarkTheme) Color.DarkGray else Color.LightGray,
                startAngle = -90f,
                sweepAngle = animatedValue * 360,
                useCenter = false,
                style = Stroke(10f, cap = StrokeCap.Round),
                size = Size(size.width, size.width),
                alpha = 0.5f
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sliderValue * 360,
                useCenter = false,
                style = Stroke(10f, cap = StrokeCap.Round),
                size = Size(size.width, size.width),
                alpha = if (isRunning) 1f else 0.5f
            )
        }
    }
}

fun stopwatchFormat(time: Time, color: Color): AnnotatedString {
    val format: (Int) -> String = { "%02d".format(it) }
    return buildAnnotatedString {
        append("${format(time.hours)}:${format(time.minutes)}:${format(time.seconds)}")
        withStyle(style = SpanStyle(color = color)) {
            append(":${format(time.millis / 10)}")
        }
    }
}