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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlarmAdd
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.clock.domain.entity.Alarm
import com.github.amrmsaraya.clock.presentation.alarm.ui.AlarmCard
import com.github.amrmsaraya.clock.presentation.alarm.ui.NewAlarm
import com.github.amrmsaraya.clock.presentation.alarm.utils.Colors
import com.github.amrmsaraya.clock.presentation.alarm.utils.Days
import com.github.amrmsaraya.clock.presentation.common_ui.AddFAB
import com.github.amrmsaraya.clock.presentation.common_ui.DeleteFAB
import com.github.amrmsaraya.clock.presentation.common_ui.FullScreenDialog
import com.github.amrmsaraya.clock.presentation.theme.Purple400
import com.github.amrmsaraya.clock.presentation.theme.Purple900
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun AlarmScreen(
    modifier: Modifier,
    onBackPress: () -> Unit,
    viewModel: AlarmViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    var selectMode by remember { mutableStateOf(false) }
    val selectedAlarms = remember { mutableStateListOf<Alarm>() }
    val deletedAlarms = remember { mutableStateListOf<Alarm>() }

    var isDeleteInProgress by remember { mutableStateOf(false) }

    var showAlarmDialog by remember { mutableStateOf(false) }
    var alarmEditMode by remember { mutableStateOf(false) }
    var alarm by remember { mutableStateOf(Alarm(ringtone = "")) }


    BackHandler {
        when {
            selectMode -> selectMode = false
            else -> onBackPress()
        }
    }

    NewAlarmDialog(
        visible = showAlarmDialog,
        editMode = alarmEditMode,
        alarm = alarm,
        onSave = {
            viewModel.sendIntent(AlarmIntent.InsertAlarm(it))
            showAlarmDialog = false
        },
        onCancel = { showAlarmDialog = false }
    )


    AlarmScreenContent(
        modifier = modifier,
        scaffoldState = scaffoldState,
        alarms = uiState.alarms,
        onCheckedChange = { viewModel.sendIntent(AlarmIntent.InsertAlarm(it)) },
        onShowAlarmDialog = { receivedAlarm, editMode ->
            alarm = receivedAlarm
            alarmEditMode = editMode
            showAlarmDialog = true
        },
        selectMode = selectMode,
        selectedAlarms = selectedAlarms.map { it.id },
        deletedAlarms = deletedAlarms,
        onSelectMode = {
            if (!isDeleteInProgress) {
                selectMode = true
            }
        },
        onSelectAlarm = {
            if (!isDeleteInProgress) {
                selectedAlarms.add(it)
            }
        },
        onUnSelectAlarm = {
            selectedAlarms.remove(it)
            if (selectedAlarms.isEmpty()) {
                selectMode = false
            }
        },
        onDelete = {
            scope.launch {
                isDeleteInProgress = true

                deletedAlarms.addAll(selectedAlarms)
                selectMode = false

                delay((1000 + 1000 * .2f).roundToLong())
                viewModel.sendIntent(AlarmIntent.DeleteAlarms(selectedAlarms))

                isDeleteInProgress = false
            }
        },
        onCleanUp = {
            deletedAlarms.clear()
            selectedAlarms.clear()
        }
    )

}

@Composable
fun NewAlarmDialog(
    visible: Boolean,
    editMode: Boolean,
    alarm: Alarm,
    onSave: (Alarm) -> Unit,
    onCancel: () -> Unit,
) {
    FullScreenDialog(
        visible = visible,
        onDismiss = onCancel
    ) {
        NewAlarm(
            modifier = Modifier.padding(16.dp),
            editMode = editMode,
            alarm = alarm,
            onSave = onSave,
            onCancel = onCancel
        )
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun AlarmScreenContent(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState,
    alarms: List<Alarm>,
    onCheckedChange: (Alarm) -> Unit,
    onShowAlarmDialog: (Alarm, Boolean) -> Unit,
    selectMode: Boolean,
    selectedAlarms: List<Long>,
    deletedAlarms: List<Alarm>,
    onSelectMode: () -> Unit,
    onSelectAlarm: (Alarm) -> Unit,
    onUnSelectAlarm: (Alarm) -> Unit,
    onDelete: () -> Unit,
    onCleanUp: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(key1 = alarms) {
        onCleanUp()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        floatingActionButton = {
            when (selectMode) {
                true -> DeleteFAB { onDelete() }
                false -> {
                    if (listState.firstVisibleItemIndex < 1) {
                        AddFAB {
                            onShowAlarmDialog(
                                Alarm(
                                    ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                                        .toString()
                                ),
                                false
                            )
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = if (selectMode) FabPosition.Center else FabPosition.End
    ) {
        Box(Modifier.fillMaxSize()) {
            if (alarms.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(100.dp),
                        imageVector = Icons.Outlined.AlarmAdd,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Click \"+\" to add a new alarm",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                state = listState
            ) {
                items(alarms) { alarm ->

                    AnimatedVisibility(
                        visible = !deletedAlarms.contains(alarm),
                        enter = EnterTransition.None,
                        exit = shrinkVertically(tween(1000)) + fadeOut()
                    ) {
                        Row(
                            Modifier.padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                backgroundColor = Colors.values()
                                    .filter { it.ordinal == alarm.color }
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
                                        when (selectedAlarms.contains(alarm.id)) {
                                            true -> onUnSelectAlarm(alarm)
                                            false -> onSelectAlarm(alarm)
                                        }
                                    } else {
                                        onShowAlarmDialog(alarm, true)
                                    }
                                },
                                onLongClick = {
                                    if (!selectMode) {
                                        onSelectMode()
                                        onSelectAlarm(alarm)
                                    }
                                }
                            )
                            AnimatedVisibility(selectMode) {
                                Spacer(modifier = Modifier.size(8.dp))
                                Checkbox(
                                    checked = selectedAlarms.contains(alarm.id),
                                    onCheckedChange = {
                                        if (it) onSelectAlarm(alarm) else onUnSelectAlarm(alarm)
                                    },
                                )
                            }
                        }
                    }
//                    Spacer(modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}