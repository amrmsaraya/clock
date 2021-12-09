package com.github.amrmsaraya.clock.presentation.alarm.component

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.clock.domain.entity.Alarm
import com.github.amrmsaraya.clock.presentation.alarm.utils.Colors
import com.github.amrmsaraya.clock.presentation.alarm.utils.Days
import com.github.amrmsaraya.clock.presentation.alarm.utils.TimeType

@OptIn(ExperimentalComposeUiApi::class)
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
    if (selectedDays.isEmpty()) {
        selectedDays.addAll(
            alarm.repeatOn.map { ordinal ->
                Days.values().first { it.ordinal == ordinal }
            }
        )
    }

    val backgroundColor = Colors.values().map { it.activeBackgroundColor }
    var selectedColor by remember { mutableStateOf(alarm.color) }

    var title by remember { mutableStateOf(alarm.title) }
    var amPm by remember { mutableStateOf(alarm.amPm) }
    var ringtone by remember { mutableStateOf(alarm.ringtone.toUri()) }

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

    Column(modifier = modifier.verticalScroll(state = rememberScrollState())) {
        HeaderRow(
            title = title,
            editMode = editMode,
            hour = selectedHour,
            minute = selectedMinute,
            amPm = amPm,
            ringtone = ringtone,
            selectedColor = selectedColor,
            selectedDays = selectedDays,
            alarm = alarm,
            onCancel = onCancel,
            onSave = onSave
        )

        Spacer(modifier = Modifier.size(16.dp))

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

        Spacer(modifier = Modifier.size(16.dp))

        DaysRow(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .horizontalScroll(daysRowScrollState),
            days = days,
            selectedDays = selectedDays
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
    }
}

@Composable
private fun RingtoneRow(
    ringtone: Uri,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
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
                text = RingtoneManager.getRingtone(
                    context,
                    ringtone
                ).getTitle(context),
                color = MaterialTheme.colorScheme.outline
            )
        }
        Icon(imageVector = Icons.Default.NavigateNext, contentDescription = null)
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
        Text(text = stringResource(R.string.select_color))
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
                        .background(color.compositeOver(androidx.compose.material3.MaterialTheme.colorScheme.surface))
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
private fun DaysRow(
    modifier: Modifier,
    days: Array<Days>,
    selectedDays: SnapshotStateList<Days>
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        days.forEach { day ->

            val backgroundColor by animateColorAsState(
                targetValue = if (day in selectedDays) MaterialTheme.colorScheme.primary else Color.Transparent,
                animationSpec = tween(500)
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
                                selectedDays.remove(day)
                            } else {
                                selectedDays.add(day)
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = day.stringRes),
                    color = animateColorAsState(
                        targetValue = if (day in selectedDays) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
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
            type = TimeType.HOUR,
            onTimeChange = { selectedHour = it },
            initial = hour,
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
            type = TimeType.MINUTE,
            onTimeChange = { selectedMinute = it },
            initial = minute
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
                            targetValue = if (amPm == 0) MaterialTheme.colorScheme.primary else Color.Transparent,
                            animationSpec = tween(500)

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
                    targetValue = if (amPm == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    animationSpec = tween(500)
                ).value
            )
            Text(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxSize()
                    .background(
                        animateColorAsState(
                            targetValue = if (amPm == 1) MaterialTheme.colorScheme.primary else Color.Transparent,
                            animationSpec = tween(500)
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun HeaderRow(
    title: String,
    editMode: Boolean,
    hour: Int,
    minute: Int,
    amPm: Int,
    ringtone: Uri,
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
                        enabled = true,
                    )
                )
            }
        ) {
            Icon(imageVector = Icons.Default.Done, contentDescription = null)
        }
    }
}

private fun ringtonePicker(
    context: Context,
    getRingtone: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
) {
    val existingRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)

    intent.putExtra(
        RingtoneManager.EXTRA_RINGTONE_TITLE,
        context.getString(R.string.set_alarm_tone)
    )
    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, existingRingtoneUri)
    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)

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