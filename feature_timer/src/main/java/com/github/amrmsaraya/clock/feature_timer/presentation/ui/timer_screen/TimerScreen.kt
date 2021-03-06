package com.github.amrmsaraya.clock.feature_timer.presentation.ui.timer_screen

import android.content.BroadcastReceiver
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
import com.github.amrmsaraya.clock.common.ui.components.FullScreenDialog
import com.github.amrmsaraya.clock.common.ui.components.StopwatchTimer
import com.github.amrmsaraya.clock.feature_timer.R
import com.github.amrmsaraya.clock.feature_timer.presentation.ui.timer_screen.component.NewTimerDialog
import com.github.amrmsaraya.clock.feature_timer.presentation.ui.timer_screen.component.NewTimerTemplate
import com.github.amrmsaraya.clock.feature_timer.presentation.ui.timer_screen.component.SetupTimer
import com.github.amrmsaraya.clock.feature_timer.presentation.ui.timer_screen.component.TimerTemplateItem
import com.github.amrmsaraya.timer.Time
import com.github.amrmsaraya.timer.Timer
import com.github.amrmsaraya.timer.toTime
import com.google.accompanist.pager.ExperimentalPagerApi
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import com.github.amrmsaraya.clock.feature_timer.domain.entity.Timer as EntityTimer

@ExperimentalPagerApi
@ExperimentalComposeUiApi
@ExperimentalSnapperApi
@ExperimentalFoundationApi
@Composable
fun TimerScreen(
    modifier: Modifier,
    onRegisterTimerReceiver: ((Time, Int) -> Unit) -> BroadcastReceiver,
    onStartTimerService: (Long) -> Unit,
    onSendTimerBroadcastAction: (String) -> Unit,
    onConfigureTimerService: (Long) -> Unit,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val uiState = viewModel.uiState

    val sliderValue = uiState.timer.timeInMillis / uiState.configuredTime.toFloat()

    var timer by remember {
        mutableStateOf(
            EntityTimer(
                title = context.getString(R.string.timer_title),
                timeMillis = 15 * 60 * 1000L
            )
        )
    }
    var editMode by remember { mutableStateOf(false) }

    var showNewTimerDialog by remember { mutableStateOf(false) }

    var selectedTemplate by remember { mutableStateOf<EntityTimer?>(null) }


    DisposableEffect(key1 = true) {
        val timerReceiver = onRegisterTimerReceiver { timer, status ->
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
            timer = EntityTimer(
                title = context.getString(R.string.timer_title),
                timeMillis = 15 * 60 * 1000L
            )
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
                onStartTimerService(uiState.configuredTime)
                onConfigureTimerService(uiState.configuredTime)
                onSendTimerBroadcastAction("start")
            }
        },
        onPause = { onSendTimerBroadcastAction("pause") },
        onReset = { onSendTimerBroadcastAction("reset") }
    )
}

@ExperimentalSnapperApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
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

@ExperimentalFoundationApi
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
                    onClick = { if (status == Timer.RUNNING) onPause() else onStart() }
                ) {
                    Text(
                        text = if (status == Timer.RUNNING) {
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
