package com.github.amrmsaraya.clock.presentation.alarm

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.clock.domain.entity.Alarm
import com.github.amrmsaraya.clock.presentation.alarm.component.AlarmCard
import com.github.amrmsaraya.clock.presentation.alarm.component.EmptyAlarms
import com.github.amrmsaraya.clock.presentation.alarm.component.NewAlarm
import com.github.amrmsaraya.clock.presentation.alarm.utils.Colors
import com.github.amrmsaraya.clock.presentation.alarm.utils.Days
import com.github.amrmsaraya.clock.presentation.common_ui.AddFAB
import com.github.amrmsaraya.clock.presentation.common_ui.DeleteFAB
import com.github.amrmsaraya.clock.presentation.common_ui.FullScreenDialog
import com.github.amrmsaraya.clock.presentation.theme.Purple200
import com.github.amrmsaraya.clock.presentation.theme.Purple900
import com.github.amrmsaraya.clock.services.alarm.AlarmReceiver
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun AlarmScreen(
    modifier: Modifier,
    viewModel: AlarmViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectMode by remember { mutableStateOf(false) }
    val selectedAlarms = remember { mutableStateListOf<Alarm>() }
    val deletedAlarms = remember { mutableStateListOf<Alarm>() }

    var isDeleteInProgress by remember { mutableStateOf(false) }

    var showAlarmDialog by remember { mutableStateOf(false) }
    var alarmEditMode by remember { mutableStateOf(false) }
    var alarm by remember { mutableStateOf(Alarm(ringtone = "")) }


    BackHandler {
        when {
            selectMode -> {
                selectMode = false
                selectedAlarms.clear()
            }
            else -> (context as Activity).finish()
        }
    }

    NewAlarmDialog(
        visible = showAlarmDialog,
        editMode = alarmEditMode,
        alarm = alarm,
        onSave = {
            setAlarm(context, it)
            viewModel.sendIntent(AlarmIntent.InsertAlarm(it))
            showAlarmDialog = false
        },
        onCancel = { showAlarmDialog = false }
    )


    AlarmScreenContent(
        modifier = modifier,
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

                delay(300 * 2)
                viewModel.sendIntent(AlarmIntent.DeleteAlarms(selectedAlarms))

                isDeleteInProgress = false
            }
        },
        onCleanUp = {
            scope.launch {
                delay(300 * 2)
                deletedAlarms.clear()
                selectedAlarms.clear()
            }
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun AlarmScreenContent(
    modifier: Modifier = Modifier,
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
        floatingActionButton = {
            when (selectMode) {
                true -> DeleteFAB { onDelete() }
                false -> {
                    AnimatedVisibility(
                        visible = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 100,
                        enter = fadeIn(tween(500)),
                        exit = fadeOut(tween(500))
                    ) {
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
            EmptyAlarms(visible = alarms.isEmpty())

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
                        exit = shrinkVertically() + fadeOut()
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
                                activeBackgroundColor = Colors.values()
                                    .filter { it.ordinal == alarm.color }
                                    .map {
                                        it.activeBackgroundColor
                                    }.firstOrNull() ?: Purple200,
                                contentColor = Colors.values().filter { it.ordinal == alarm.color }
                                    .map {
                                        it.contentColor
                                    }.firstOrNull() ?: Purple900,
                                checked = alarm.enabled,
                                selectMode = selectMode,
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
                                androidx.compose.material3.Checkbox(
                                    checked = selectedAlarms.contains(alarm.id),
                                    onCheckedChange = {
                                        if (it) onSelectAlarm(alarm) else onUnSelectAlarm(alarm)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun setAlarm(context: Context, alarm: Alarm) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val pendingIntent = Intent(context, AlarmReceiver::class.java).let { intent ->

        intent.putExtra("id", alarm.id.toInt())
        intent.putExtra("title", alarm.title)
        intent.putExtra("hour", alarm.hour)
        intent.putExtra("minute", alarm.minute)
        intent.putExtra("amPm", alarm.amPm)
        intent.putExtra("ringtone", alarm.ringtone)
        intent.putExtra("color", alarm.color)

        PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

    }

    val calendar = Calendar.getInstance().apply {

        if (alarm.hour > get(Calendar.HOUR)) {
            set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1)
        } else {
            if (alarm.minute > get(Calendar.MINUTE) && alarm.hour != get(Calendar.HOUR)) {
                set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1)
            }
        }

        set(Calendar.HOUR, alarm.hour)
        set(Calendar.MINUTE, alarm.minute)
        set(Calendar.AM_PM, alarm.amPm)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    alarmManager.setAlarmClock(
        AlarmManager.AlarmClockInfo(
            calendar.timeInMillis,
            pendingIntent
        ),
        pendingIntent
    )

    println(alarmManager.nextAlarmClock.triggerTime)
}

fun cancelAlarm(context: Context, alarm: Alarm) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val pendingIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
        PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
    }

    alarmManager.cancel(pendingIntent)
}