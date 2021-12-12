package com.github.amrmsaraya.clock.presentation.timer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.clock.presentation.common_ui.StopwatchTimer
import com.github.amrmsaraya.timer.Stopwatch
import com.github.amrmsaraya.timer.Timer

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimerScreen(
    modifier: Modifier,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val sliderValue = uiState.timer.timeInMillis / uiState.configuredTime.toFloat()

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
            timer = uiState.timer,
            sliderValue = sliderValue,
            animatedValue = animatedCircle,
            status = uiState.status
        )
        Column(modifier = Modifier.weight(0.5f)) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    cells = GridCells.Fixed(3),
                ) {
                    items(6) {
                        Column(
                            modifier = Modifier
                                .size(maxWidth / 3)
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember{ MutableInteractionSource()},
                                    onClick = {}
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Title", color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(text = "00:15:00", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }

            ControlRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                status = uiState.status,
                onStart = { viewModel.sendIntent(TimerIntent.Start) },
                onPause = { viewModel.sendIntent(TimerIntent.Pause) },
                onReset = { viewModel.sendIntent(TimerIntent.Reset) },
            )
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
) {
    if (status == Timer.IDLE) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Button(
                modifier = Modifier.fillMaxWidth(0.5f),
                onClick = onStart
            ) {
                Text(text = stringResource(R.string.start).uppercase())
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
                onClick = onReset
            ) {
                Text(text = stringResource(R.string.reset).uppercase())
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