package com.github.amrmsaraya.clock.presentation.timer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.amrmsaraya.clock.presentation.stopwatch.ControlRow
import com.github.amrmsaraya.clock.presentation.stopwatch.StopwatchTimer

@Composable
fun TimerScreen(
    modifier: Modifier,
    viewModel: TimerViewModel = viewModel()
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        StopwatchTimer(
            timer = uiState.timer,
            sliderValue = sliderValue,
            animatedValue = animatedCircle,
            status = uiState.status
        )
        Spacer(modifier = Modifier.size(16.dp))
        ControlRow(
            status = uiState.status,
            onStart = { viewModel.sendIntent(TimerIntent.Start) },
            onPause = { viewModel.sendIntent(TimerIntent.Pause) },
            onReset = { viewModel.sendIntent(TimerIntent.Reset) },
            onLap = {}
        )
    }
}