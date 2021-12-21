package com.github.amrmsaraya.clock.presentation.timer

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.clock.presentation.common_ui.FullScreenDialog
import com.github.amrmsaraya.clock.presentation.common_ui.StopwatchTimer
import com.github.amrmsaraya.clock.presentation.timer.component.NewTimerDialog
import com.github.amrmsaraya.clock.presentation.timer.component.NewTimerTemplate
import com.github.amrmsaraya.clock.presentation.timer.component.SetupTimer
import com.github.amrmsaraya.clock.presentation.timer.component.TimerTemplateItem
import com.github.amrmsaraya.clock.presentation.timer.utils.configureTimerService
import com.github.amrmsaraya.clock.presentation.timer.utils.registerTimerReceiver
import com.github.amrmsaraya.clock.presentation.timer.utils.sendTimerBroadcastAction
import com.github.amrmsaraya.clock.presentation.timer.utils.startTimerService
import com.github.amrmsaraya.timer.Stopwatch
import com.github.amrmsaraya.timer.Timer
import com.github.amrmsaraya.timer.toTime
import com.github.amrmsaraya.clock.domain.entity.Timer as EntityTimer

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimerScreen(
    modifier: Modifier,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState

    val sliderValue = uiState.timer.timeInMillis / uiState.configuredTime.toFloat()

    var timer by remember { mutableStateOf(EntityTimer(timeMillis = 15 * 60 * 1000L)) }
    var editMode by remember { mutableStateOf(false) }

    var showNewTimerDialog by remember { mutableStateOf(false) }

    var selectedTemplate by remember { mutableStateOf<EntityTimer?>(null) }


    DisposableEffect(key1 = true) {
        val timerReceiver = context.registerTimerReceiver { timer, status ->
            viewModel.sendIntent(TimerIntent.UpdateTimer(timer, status))
        }

        onDispose {
            context.unregisterReceiver(timerReceiver)
        }
    }

    FullScreenDialog(
        visible = showNewTimerDialog,
        onDismiss = { showNewTimerDialog = false }
    ) {
        NewTimerDialog(
            timer = timer,
            editMode = editMode,
            onDismiss = { showNewTimerDialog = false },
            onSave = { viewModel.sendIntent(TimerIntent.Insert(it)) },
            onDelete = { viewModel.sendIntent(TimerIntent.Delete(it)) }
        )
    }

    TimerScreenContent(
        modifier = modifier,
        uiState = uiState,
        sliderValue = sliderValue,
        selectedTemplate = selectedTemplate,
        onNewTimerClick = {
            timer = EntityTimer(title = "Timer", timeMillis = 15 * 60 * 1000L)
            editMode = false
            showNewTimerDialog = true
        },
        onEditTimerClick = {
            timer = it
            editMode = true
            showNewTimerDialog = true
        },
        onTemplateClick = {
            viewModel.sendIntent(TimerIntent.ConfigureTime(it.timeMillis))
            selectedTemplate = it
        },
        onTimeChange = {
            viewModel.sendIntent(TimerIntent.ConfigureTime(it))
            selectedTemplate?.let { timer ->
                if (timer.timeMillis != it) {
                    selectedTemplate = null
                }
            }
        },
        onStart = {
            if (uiState.configuredTime > 0) {
                context.startTimerService(uiState.configuredTime)
                context.configureTimerService(uiState.configuredTime)
                context.sendTimerBroadcastAction("start")
            }
        },
        onPause = { context.sendTimerBroadcastAction("pause") },
        onReset = { context.sendTimerBroadcastAction("reset") }
    )
}

@Composable
private fun TimerScreenContent(
    modifier: Modifier,
    uiState: TimerUiState,
    sliderValue: Float,
    selectedTemplate: EntityTimer?,
    onNewTimerClick: () -> Unit,
    onEditTimerClick: (EntityTimer) -> Unit,
    onTimeChange: (Long) -> Unit,
    onTemplateClick: (EntityTimer) -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier.weight(0.5f),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = uiState.status == Timer.IDLE,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    SetupTimer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        timer = uiState.configuredTime.toTime(),
                        onTimeChange = onTimeChange
                    )
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = uiState.status != Timer.IDLE,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    StopwatchTimer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        timer = uiState.timer,
                        sliderValue = sliderValue,
                        animatedValue = 1f,
                        status = uiState.status
                    )
                }
            }
        }

        Column(
            modifier = Modifier.weight(0.5f),
            verticalArrangement = Arrangement.Bottom
        ) {
            AnimatedVisibility(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                visible = uiState.status == Timer.IDLE,
            ) {
                TimerTemplates(
                    timers = uiState.timers,
                    selectedTemplate = selectedTemplate,
                    onNewTimerClick = onNewTimerClick,
                    onTemplateClick = onTemplateClick,
                    onEditTimerClick = onEditTimerClick
                )
            }

            ControlRow(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                status = uiState.status,
                onStart = onStart,
                onPause = onPause,
                onReset = onReset,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimerTemplates(
    modifier: Modifier = Modifier,
    timers: List<EntityTimer>,
    selectedTemplate: EntityTimer?,
    onNewTimerClick: () -> Unit,
    onTemplateClick: (EntityTimer) -> Unit,
    onEditTimerClick: (EntityTimer) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            cells = GridCells.Fixed(3),
        ) {
            items(timers) {
                TimerTemplateItem(
                    timer = it,
                    selected = it == selectedTemplate,
                    onClick = { onTemplateClick(it) },
                    onLongClick = { onEditTimerClick(it) }
                )
            }
            item { NewTimerTemplate(onClick = onNewTimerClick) }
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
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AnimatedVisibility(
            visible = status == Timer.IDLE,
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
            visible = status != Timer.IDLE,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
}
