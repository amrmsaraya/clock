package com.github.amrmsaraya.clock.presentation.stopwatch

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.clock.presentation.common_ui.StopwatchTimer
import com.github.amrmsaraya.clock.presentation.common_ui.getSurfaceColor
import com.github.amrmsaraya.clock.presentation.common_ui.stopwatchTimerFormat
import com.github.amrmsaraya.clock.presentation.stopwatch.utils.registerLapsReceiver
import com.github.amrmsaraya.clock.presentation.stopwatch.utils.registerStopwatchReceiver
import com.github.amrmsaraya.clock.presentation.stopwatch.utils.sendStopwatchBroadcastAction
import com.github.amrmsaraya.clock.presentation.stopwatch.utils.startStopwatchService
import com.github.amrmsaraya.timer.Stopwatch
import com.github.amrmsaraya.timer.Time

@Composable
fun StopwatchScreen(
    modifier: Modifier,
    viewModel: StopwatchViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val uiState = viewModel.uiState

    val sliderValue = (uiState.stopwatch.seconds * 1000 + uiState.stopwatch.millis) / 60_000f

    var animatedValue by remember { mutableStateOf(if (uiState.status == Stopwatch.IDLE) 0f else 1f) }
    val animatedCircle by animateFloatAsState(
        targetValue = animatedValue,
        animationSpec = tween(1000)
    )

    LaunchedEffect(key1 = true) {
        animatedValue = 1f
    }

    DisposableEffect(key1 = true) {
        val stopwatchReceiver = context.registerStopwatchReceiver { stopwatch, status ->
            viewModel.updateStopwatch(
                stopwatch = stopwatch,
                status = status
            )
        }

        val lapsReceiver = context.registerLapsReceiver { laps ->
            viewModel.updateLaps(laps = laps)
        }

        onDispose {
            context.unregisterReceiver(stopwatchReceiver)
            context.unregisterReceiver(lapsReceiver)
        }
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
                onStart = {
                    context.startStopwatchService()
                    context.sendStopwatchBroadcastAction("start")
                },
                onPause = { context.sendStopwatchBroadcastAction("pause") },
                onReset = { context.sendStopwatchBroadcastAction("reset") },
                onLap = { context.sendStopwatchBroadcastAction("lap") }
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
                        )
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "%02d".format(laps.lastIndex - index + 1))
                Text(
                    text = stopwatchTimerFormat(
                        time.first,
                        MaterialTheme.colorScheme.onSurface
                    ).text
                )
                Text(
                    text = "+${
                        stopwatchTimerFormat(
                            time.second,
                            MaterialTheme.colorScheme.onSurface
                        ).text
                    }"
                )
            }
            if (index != laps.lastIndex) {
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
}

@Composable
private fun ControlRow(
    modifier: Modifier = Modifier,
    status: Int,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onLap: () -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AnimatedVisibility(
            status == Stopwatch.IDLE,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(0.5f),
                onClick = onStart
            ) {
                Text(text = stringResource(R.string.start).uppercase())
            }
        }

        AnimatedVisibility(
            status != Stopwatch.IDLE,
            enter = fadeIn(tween()) + expandHorizontally(tween()),
            exit = fadeOut(tween()) + shrinkHorizontally(tween())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilledTonalButton(
                    modifier = Modifier.weight(0.5f),
                    onClick = { if (status == Stopwatch.RUNNING) onLap() else onReset() }
                ) {
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
}