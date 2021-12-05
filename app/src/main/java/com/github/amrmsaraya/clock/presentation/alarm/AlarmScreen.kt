package com.github.amrmsaraya.clock.presentation.alarm

import android.media.RingtoneManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.clock.domain.entity.Alarm
import com.github.amrmsaraya.clock.presentation.alarm.ui.AddAlarm
import com.github.amrmsaraya.clock.presentation.alarm.ui.AlarmCard
import com.github.amrmsaraya.clock.presentation.alarm.utils.Colors
import com.github.amrmsaraya.clock.presentation.alarm.utils.Days
import com.github.amrmsaraya.clock.presentation.common_ui.AddFAB
import com.github.amrmsaraya.clock.presentation.common_ui.BottomDrawerSheet
import com.github.amrmsaraya.clock.presentation.common_ui.DeleteFAB
import com.github.amrmsaraya.clock.presentation.theme.Purple400
import com.github.amrmsaraya.clock.presentation.theme.Purple900
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun AlarmScreen(
    modifier: Modifier,
    onShowBottomNavigation: (Boolean) -> Unit,
    onBackPress: () -> Unit,
    viewModel: AlarmViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState
    val scaffoldState = rememberScaffoldState()
    val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val hourState = rememberLazyListState()
    val minuteState = rememberLazyListState()

    var selectMode by remember { mutableStateOf(false) }
    val selectedAlarms = remember { mutableStateListOf<Alarm>() }
    val deletedAlarms = remember { mutableStateListOf<Alarm>() }

    var alarm by remember {
        mutableStateOf(
            Alarm(
                ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()
            )
        )
    }

    BackHandler {
        when {
            drawerState.isOpen -> {
                scope.launch {
                    onShowBottomNavigation(true)
                    drawerState.close()
                }
            }
            selectMode -> selectMode = false
            else -> onBackPress()
        }
    }

    BottomDrawerSheet(
        modifier = modifier.fillMaxSize(),
        drawerState = drawerState,
        drawerContent = {
            AddAlarm(
                hourState = hourState,
                minuteState = minuteState,
                alarm = alarm,
                onSave = { alarm ->
                    viewModel.sendIntent(AlarmIntent.InsertAlarm(alarm))
                    scope.launch {
                        onShowBottomNavigation(true)
                        drawerState.close()
                    }
                }
            ) {
                scope.launch {
                    onShowBottomNavigation(true)
                    drawerState.close()
                }
            }
        },
        content = {
            AlarmScreenContent(
                scaffoldState = scaffoldState,
                drawerState = drawerState,
                scope = scope,
                alarms = uiState.alarms,
                onShowBottomNavigation = onShowBottomNavigation,
                onCheckedChange = { viewModel.sendIntent(AlarmIntent.InsertAlarm(it)) },
                onAlarmChange = { alarm = it },
                selectMode = selectMode,
                selectedAlarms = selectedAlarms,
                deletedAlarms = deletedAlarms,
                onSelectMode = { selectMode = true },
                onSelectAlarm = { selectedAlarms.add(it) },
                onUnSelectAlarm = {
                    selectedAlarms.remove(it)
                    if (selectedAlarms.isEmpty()) {
                        selectMode = false
                    }
                },
                onDelete = {
                    scope.launch {
                        deletedAlarms.addAll(selectedAlarms)
                        selectMode = false

                        delay((1000 + 1000 * .5f).roundToLong())
                        viewModel.sendIntent(AlarmIntent.DeleteAlarms(selectedAlarms))

                        delay(1000)
                        deletedAlarms.clear()
                        selectedAlarms.clear()
                    }
                }
            )
        }
    )

}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun AlarmScreenContent(
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
    drawerState: BottomDrawerState,
    alarms: List<Alarm>,
    onShowBottomNavigation: (Boolean) -> Unit,
    onCheckedChange: (Alarm) -> Unit,
    onAlarmChange: (Alarm) -> Unit,
    selectMode: Boolean,
    selectedAlarms: List<Alarm>,
    deletedAlarms: List<Alarm>,
    onSelectMode: () -> Unit,
    onSelectAlarm: (Alarm) -> Unit,
    onUnSelectAlarm: (Alarm) -> Unit,
    onDelete: () -> Unit
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        floatingActionButton = {
            when (selectMode) {
                true -> DeleteFAB { onDelete() }
                false -> {
                    AddFAB {
                        scope.launch {
                            onAlarmChange(
                                Alarm(
                                    ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                                        .toString()
                                )
                            )
                            delay(100)
                            onShowBottomNavigation(false)
                            drawerState.expand()
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = if (selectMode) FabPosition.Center else FabPosition.End
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            items(alarms) { alarm ->

                AnimatedVisibility(
                    visible = !deletedAlarms.contains(alarm),
                    enter = EnterTransition.None,
                    exit = shrinkVertically(tween(1000)) + fadeOut()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AlarmCard(
                            modifier = Modifier.weight(1f),
                            title = alarm.title,
                            time = buildString {
                                append(alarm.hour)
                                append(":")
                                append("%02d".format(alarm.minute))
                            },
                            amPm = if (alarm.amPm == 0) stringResource(R.string.am) else stringResource(
                                R.string.pm
                            ),
                            days = Days.values().filter { it.ordinal in alarm.repeatOn }.map {
                                stringResource(id = it.stringRes)
                            }.joinToString("  "),
                            backgroundColor = Colors.values().filter { it.ordinal == alarm.color }
                                .map {
                                    it.background
                                }.firstOrNull() ?: Purple400,
                            contentColor = Colors.values().filter { it.ordinal == alarm.color }
                                .map {
                                    it.onBackground
                                }.firstOrNull() ?: Purple900,
                            ringtone = alarm.ringtone,
                            checked = alarm.enabled,
                            onCheckedChange = { onCheckedChange(alarm.copy(enabled = it)) },
                            onClick = {
                                if (selectMode) {
                                    when (selectedAlarms.contains(alarm)) {
                                        true -> onUnSelectAlarm(alarm)
                                        false -> onSelectAlarm(alarm)
                                    }
                                } else {
                                    scope.launch {
                                        onAlarmChange(alarm)
                                        delay(100)
                                        onShowBottomNavigation(false)
                                        drawerState.expand()
                                    }
                                }
                            },
                            onLongClick = {
                                if(!selectMode) {
                                    onSelectMode()
                                    onSelectAlarm(alarm)
                                }
                            }
                        )
                        AnimatedVisibility(selectMode) {
                            Spacer(modifier = Modifier.size(8.dp))
                            Checkbox(
                                checked = selectedAlarms.contains(alarm),
                                onCheckedChange = {
                                    if (it) onSelectAlarm(alarm) else onUnSelectAlarm(alarm)
                                },
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}