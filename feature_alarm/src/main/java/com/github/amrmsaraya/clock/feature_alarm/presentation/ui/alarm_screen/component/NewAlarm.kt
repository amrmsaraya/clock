package com.github.amrmsaraya.clock.feature_alarm.presentation.ui.alarm_screen.component

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.github.amrmsaraya.clock.common.ui.components.FullScreenDialog
import com.github.amrmsaraya.clock.common.ui.components.TimeChooser
import com.github.amrmsaraya.clock.common.utils.mirror
import com.github.amrmsaraya.clock.feature_alarm.R
import com.github.amrmsaraya.clock.feature_alarm.domain.entity.Alarm
import com.github.amrmsaraya.clock.feature_alarm.presentation.ui.alarm_screen.utils.Colors
import com.github.amrmsaraya.clock.feature_alarm.presentation.ui.alarm_screen.utils.Days
import com.github.amrmsaraya.timer.toTime
import com.google.accompanist.pager.ExperimentalPagerApi
import dev.chrisbanes.snapper.ExperimentalSnapperApi

@ExperimentalSnapperApi
@ExperimentalPagerApi
@ExperimentalComposeUiApi
@Composable
fun NewAlarm(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    editMode: Boolean,
    onSave: (Alarm) -> Unit,
    onCancel: () -> Unit,
) {
    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current

    val days = Days.values()
    val selectedDays = remember { mutableStateListOf<Days>() }

    val backgroundColor = Colors.values().map { it.activeBackgroundColor }
    var selectedColor by remember { mutableStateOf(alarm.color) }

    var title by remember { mutableStateOf(alarm.title) }
    var amPm by remember { mutableStateOf(alarm.amPm) }
    var ringtone by remember { mutableStateOf(alarm.ringtone.toUri()) }
    var snooze by remember { mutableStateOf(alarm.snooze) }

    val colorRowScrollState = rememberScrollState()
    val daysRowScrollState = rememberScrollState()

    var selectedHour by remember { mutableStateOf(alarm.hour) }
    var selectedMinute by remember { mutableStateOf(alarm.minute) }

    val getRingtone =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            it.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                ?.let { uri ->
                    ringtone = uri
                }
        }

    var showSnoozeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        if (selectedDays.isEmpty()) {
            selectedDays.addAll(
                alarm.repeatOn.map { ordinal ->
                    Days.values().first { it.ordinal == ordinal }
                }
            )
        }
    }

    SnoozeDialog(
        visible = showSnoozeDialog,
        snooze = snooze,
        onDismiss = { showSnoozeDialog = false },
        onSelect = { snooze = it }
    )

    Column(modifier = modifier.verticalScroll(state = rememberScrollState())) {
        HeaderRow(
            title = title,
            editMode = editMode,
            hour = selectedHour,
            minute = selectedMinute,
            amPm = amPm,
            ringtone = ringtone,
            snooze = snooze,
            selectedColor = selectedColor,
            selectedDays = selectedDays,
            alarm = alarm,
            onCancel = onCancel,
            onSave = onSave
        )

        Spacer(modifier = Modifier.size(16.dp))

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            TimeChooserRow(
                hour = alarm.hour,
                minute = alarm.minute,
                amPm = amPm,
                onTimeChange = { hour, minute ->
                    selectedHour = hour
                    selectedMinute = minute
                },
                onAmPmChange = { amPm = it }
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        DaysRow(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .horizontalScroll(daysRowScrollState),
            days = days,
            selectedDays = selectedDays,
            onDayAdded = { selectedDays.add(it) },
            onDayRemoved = { selectedDays.remove(it) }
        )

        Spacer(modifier = Modifier.size(16.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = { title = it },
            label = { Text(text = stringResource(R.string.title)) },
            textStyle = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                leadingIconColor = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { keyboard?.hide() })
        )

        Spacer(modifier = Modifier.size(16.dp))

        ColorRow(
            scrollState = colorRowScrollState,
            backgroundColor = backgroundColor,
            selectedColor = selectedColor,
            onColorChange = { selectedColor = it }
        )

        Spacer(modifier = Modifier.size(16.dp))

        RingtoneRow(
            ringtone = ringtone,
            onClick = { ringtonePicker(context, getRingtone) }
        )

        Spacer(modifier = Modifier.size(16.dp))

        SnoozeRow(
            snooze = snooze,
            onClick = { showSnoozeDialog = true }
        )
    }
}

@ExperimentalComposeUiApi
@Composable
private fun HeaderRow(
    title: String,
    editMode: Boolean,
    hour: Int,
    minute: Int,
    amPm: Int,
    ringtone: Uri,
    snooze: Long,
    selectedColor: Int,
    selectedDays: SnapshotStateList<Days>,
    alarm: Alarm,
    onCancel: () -> Unit,
    onSave: (Alarm) -> Unit
) {
    val localKeyboard = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {
                localKeyboard?.hide()
                onCancel()
            }
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = null)
        }

        Text(
            text = if (editMode) stringResource(R.string.edit_alarm) else stringResource(R.string.new_alarm),
            style = MaterialTheme.typography.titleLarge
        )

        IconButton(
            onClick = {
                localKeyboard?.hide()
                onSave(
                    alarm.copy(
                        title = title,
                        hour = hour,
                        minute = minute,
                        amPm = amPm,
                        color = selectedColor,
                        repeatOn = selectedDays.map { it.ordinal },
                        ringtone = ringtone.toString(),
                        snooze = snooze
                    )
                )
            }
        ) {
            Icon(imageVector = Icons.Default.Done, contentDescription = null)
        }
    }
}

@ExperimentalPagerApi
@ExperimentalSnapperApi
@Composable
private fun TimeChooserRow(
    modifier: Modifier = Modifier,
    hour: Int,
    minute: Int,
    amPm: Int,
    onTimeChange: (Int, Int) -> Unit,
    onAmPmChange: (Int) -> Unit
) {
    var selectedHour by remember { mutableStateOf(hour) }
    var selectedMinute by remember { mutableStateOf(minute) }

    LaunchedEffect(key1 = selectedHour, key2 = selectedMinute) {
        onTimeChange(selectedHour, selectedMinute)
    }

    Row(
        modifier
            .fillMaxWidth()
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TimeChooser(
            modifier = modifier
                .weight(0.375f)
                .fillMaxSize(),
            initial = if (hour == 0) 12 - 1 else hour - 1,
            items = (1..12).toList(),
            leadingZeros = false,
            onTimeChange = { selectedHour = it }
        )
        Text(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 16.dp, end = 16.dp),
            text = ":",
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center
        )
        TimeChooser(
            modifier = modifier
                .weight(0.375f)
                .fillMaxSize(),
            initial = minute,
            items = (0..59).toList(),
            leadingZeros = true,
            onTimeChange = { selectedMinute = it }
        )
        Column(
            modifier = Modifier
                .weight(0.25f)
                .fillMaxSize()
                .padding(start = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    color = MaterialTheme.colorScheme.primary,
                    width = 1.dp,
                    shape = RoundedCornerShape(8.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxSize()
                    .background(
                        animateColorAsState(
                            if (amPm == 0) MaterialTheme.colorScheme.primary else Color.Transparent
                        ).value
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { onAmPmChange(0) }
                    )
                    .padding(4.dp),
                text = stringResource(id = R.string.am),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                textAlign = TextAlign.Center,
                color = animateColorAsState(
                    if (amPm == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                ).value
            )
            Text(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxSize()
                    .background(
                        animateColorAsState(
                            if (amPm == 1) MaterialTheme.colorScheme.primary else Color.Transparent,
                        ).value
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { onAmPmChange(1) }
                    )
                    .padding(4.dp),
                text = stringResource(id = R.string.pm),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                textAlign = TextAlign.Center,
                color = animateColorAsState(
                    targetValue = if (amPm == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    animationSpec = tween(500)
                ).value
            )
        }
    }
}

@Composable
private fun DaysRow(
    modifier: Modifier,
    days: Array<Days>,
    selectedDays: SnapshotStateList<Days>,
    onDayAdded: (Days) -> Unit,
    onDayRemoved: (Days) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        days.forEach { day ->

            val backgroundColor by animateColorAsState(
                if (day in selectedDays) MaterialTheme.colorScheme.primary else Color.Transparent,
            )

            Box(
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = if (day in selectedDays) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                    .background(color = backgroundColor)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            if (day in selectedDays) {
                                onDayRemoved(day)
                            } else {
                                onDayAdded(day)
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = day.letter),
                    color = animateColorAsState(
                        targetValue = if (day in selectedDays) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface,
                        animationSpec = tween(500)
                    ).value,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
            if (days.last() != day) {
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
}

@Composable
private fun ColorRow(
    scrollState: ScrollState,
    backgroundColor: List<Color>,
    selectedColor: Int,
    onColorChange: (Int) -> Unit,
) {
    Column {
        Text(text = stringResource(R.string.color))
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            Modifier
                .horizontalScroll(scrollState)
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            backgroundColor.forEachIndexed { index, color ->
                val animatedSize by animateDpAsState(
                    targetValue = if (index == selectedColor) 45.dp else 35.dp,
                    animationSpec = tween(500)
                )
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(animatedSize)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { onColorChange(index) }
                ) {
                    if (index == selectedColor) {
                        Icon(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RingtoneRow(
    ringtone: Uri,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val ringtoneTitle =
        RingtoneManager.getRingtone(context, ringtone).getTitle(context)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = stringResource(R.string.ringtone))
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = ringtoneTitle,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Icon(
            modifier = Modifier.mirror(),
            imageVector = Icons.Default.NavigateNext,
            contentDescription = null
        )
    }
}

@Composable
private fun SnoozeRow(
    snooze: Long,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = stringResource(R.string.snooze))
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "${snooze.toTime().minutes} ${stringResource(R.string.minutes)}",
                color = MaterialTheme.colorScheme.outline
            )
        }
        Icon(
            modifier = Modifier.mirror(),
            imageVector = Icons.Default.NavigateNext,
            contentDescription = null
        )
    }
}

@ExperimentalComposeUiApi
@Composable
fun SnoozeDialog(
    visible: Boolean,
    snooze: Long,
    onDismiss: () -> Unit,
    onSelect: (Long) -> Unit
) {
    val snoozeList = remember {
        mutableStateListOf<Long>(
            5 * 60 * 1000,
            10 * 60 * 1000,
            15 * 60 * 1000,
            20 * 60 * 1000,
            30 * 60 * 1000,
        )
    }

    FullScreenDialog(visible = visible, onDismiss = onDismiss) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(id = R.string.snooze),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.size(8.dp))

            LazyColumn(Modifier.fillMaxWidth()) {
                items(snoozeList) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = {
                                    onSelect(it)
                                    onDismiss()
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = snooze == it,
                            onClick = {
                                onSelect(it)
                                onDismiss()
                            }
                        )
                        Text(text = "${it.toTime().minutes} ${stringResource(R.string.minutes)}")
                    }
                }
            }
        }
    }
}

private fun ringtonePicker(
    context: Context,
    getRingtone: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
) {
    val existingRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
        putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, context.getString(R.string.set_alarm_tone))
        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL)
        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, existingRingtoneUri)
        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
    }

    val pendingIntent = IntentSenderRequest.Builder(
        PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    ).build()

    getRingtone.launch(pendingIntent)
}