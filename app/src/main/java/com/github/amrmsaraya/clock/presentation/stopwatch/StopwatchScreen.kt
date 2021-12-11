package com.github.amrmsaraya.clock.presentation.stopwatch

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.clock.presentation.common_ui.getSurfaceColor
import com.github.amrmsaraya.timer.Stopwatch
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
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        StopwatchTimer(
            modifier = Modifier
                .weight(0.5f)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            timer = uiState.stopwatch,
            sliderValue = sliderValue,
            animatedValue = animatedCircle,
            status = uiState.status
        )
        Column(modifier = Modifier.weight(0.5f)) {
            Laps(
                modifier = Modifier.weight(1f),
                laps = uiState.laps
            )
            ControlRow(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                status = uiState.status,
                onStart = { viewModel.sendIntent(StopwatchIntent.Start) },
                onPause = { viewModel.sendIntent(StopwatchIntent.Pause) },
                onReset = { viewModel.sendIntent(StopwatchIntent.Reset) },
                onLap = { viewModel.sendIntent(StopwatchIntent.Lap) }
            )
        }
    }
}

@Composable
private fun Laps(modifier: Modifier = Modifier, laps: List<Pair<Time, Time>>) {
    LazyColumn(modifier) {
        itemsIndexed(laps.reversed()) { index, time ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        getSurfaceColor(
                            elevation = LocalAbsoluteTonalElevation.current + 3.dp,
                            primaryColor = MaterialTheme.colorScheme.primary,
                            surfaceColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "%02d".format(laps.lastIndex - index + 1))
                Text(text = stopwatchFormat(time.first, MaterialTheme.colorScheme.onSurface))
                Text(
                    text = "+${stopwatchFormat(time.second, MaterialTheme.colorScheme.onSurface)}"
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}

@Composable
fun ControlRow(
    modifier: Modifier = Modifier,
    status: Int,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onLap: () -> Unit
) {
    if (status == Stopwatch.IDLE) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Button(
                modifier = Modifier.fillMaxWidth(0.5f),
                onClick = onStart
            ) {
                Text(
                    text = if (status == Stopwatch.RUNNING) {
                        stringResource(R.string.pause).uppercase()
                    } else {
                        stringResource(R.string.start).uppercase()
                    }
                )
            }
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilledTonalButton(
                modifier = Modifier.weight(0.5f),
                onClick = { if (status == Stopwatch.RUNNING) onLap() else onReset() }) {
                Text(
                    text = if (status == Stopwatch.RUNNING) stringResource(R.string.lap).uppercase()
                    else stringResource(R.string.reset).uppercase(),
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                modifier = Modifier.weight(0.5f),
                onClick = { if (status == Stopwatch.RUNNING) onPause() else onStart() }
            ) {
                Text(
                    text = if (status == Stopwatch.RUNNING) {
                        stringResource(R.string.pause).uppercase()
                    } else {
                        stringResource(R.string.resume).uppercase()
                    }
                )
            }
        }
    }
}

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
            text = stopwatchFormat(timer, color),
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

fun stopwatchFormat(time: Time, color: Color): AnnotatedString {
    val format: (Int) -> String = { "%02d".format(it) }
    return buildAnnotatedString {
        append("${format(time.hours)}:${format(time.minutes)}:${format(time.seconds)}")
        withStyle(style = SpanStyle(color = color)) {
            append(":${format(time.millis / 10)}")
        }
    }
}