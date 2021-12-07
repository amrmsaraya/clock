package com.github.amrmsaraya.clock.presentation.alarm.ui

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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.clock.domain.entity.Alarm
import com.github.amrmsaraya.clock.presentation.alarm.utils.Colors
import com.github.amrmsaraya.clock.presentation.alarm.utils.Days
import com.github.amrmsaraya.clock.presentation.theme.Purple400

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

    val backgroundColor = Colors.values().map { it.background }
    var selectedColor by remember { mutableStateOf(alarm.color) }

    var title by remember { mutableStateOf(alarm.title) }
    var amPm by remember { mutableStateOf(alarm.amPm) }
    var ringtone by remember { mutableStateOf(alarm.ringtone.toUri()) }

    val colorRowScrollState = rememberScrollState()
    val daysRowScrollState = rememberScrollState()

    val hourState = rememberLazyListState()
    val minuteState = rememberLazyListState()

    val getRingtone =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            it.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                ?.let { uri ->
                    ringtone = uri
                }
        }

    Column(modifier = modifier.verticalScroll(state = rememberScrollState())) {
        HeaderRow(
            hourState = hourState,
            minuteState = minuteState,
            editMode = editMode,
            title = title,
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
            modifier = modifier,
            hourState = hourState,
            minuteState = minuteState,
            hour = alarm.hour,
            minute = alarm.minute,
            amPm = amPm,
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
            maxLines = 1,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onAny = { keyboard?.hide() })
        )

        Spacer(modifier = Modifier.size(16.dp))

        ColorRow(
            scrollState = colorRowScrollState,
            backgroundColor = backgroundColor,
            selectedColor = selectedColor,
            onColorChange = { selectedColor = it }
        )

        Spacer(modifier = Modifier.size(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { ringtonePicker(context, getRingtone) }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Ringtone")
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = RingtoneManager.getRingtone(
                        context,
                        ringtone
                    ).getTitle(context),
                    color = Color.Gray
                )
            }
            Icon(imageVector = Icons.Default.NavigateNext, contentDescription = null)
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
                    targetValue = if (index == selectedColor) 50.dp else 35.dp,
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
private fun DaysRow(
    modifier: Modifier,
    days: Array<Days>,
    selectedDays: SnapshotStateList<Days>
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        days.forEachIndexed { index, it ->

            val backgroundColor by animateColorAsState(
                targetValue = if (it in selectedDays) Purple400 else Color.Transparent,
                animationSpec = tween(500)
            )

            Text(
                modifier =
                Modifier
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(
                            topStart = if (index == 0 || days[index - 1] !in selectedDays) 8.dp else 0.dp,
                            bottomStart = if (index == 0 || days[index - 1] !in selectedDays) 8.dp else 0.dp,
                            topEnd = if (index == days.lastIndex || days[index + 1] !in selectedDays) 8.dp else 0.dp,
                            bottomEnd = if (index == days.lastIndex || days[index + 1] !in selectedDays) 8.dp else 0.dp,
                        )
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            if (it in selectedDays) {
                                selectedDays.remove(it)
                            } else {
                                selectedDays.add(it)
                            }
                        }
                    )
                    .padding(10.dp),
                text = stringResource(id = it.stringRes)
            )
        }
    }
}

@Composable
private fun TimeChooserRow(
    modifier: Modifier,
    hourState: LazyListState,
    minuteState: LazyListState,
    hour: Int,
    minute: Int,
    amPm: Int,
    onAmPmChange: (Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(200.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TimeChooser(
            modifier = modifier
                .fillMaxSize()
                .weight(0.35f),
            state = hourState,
            size = 14,
            type = "hour",
            default = hour,
        )
        Text(
            modifier = Modifier.weight(0.1f),
            text = ":",
            fontSize = 26.sp,
            textAlign = TextAlign.Center
        )
        TimeChooser(
            modifier = modifier
                .fillMaxSize()
                .weight(0.35f),
            state = minuteState,
            size = 62,
            type = "minute",
            default = minute
        )
        Column(
            modifier = Modifier.weight(0.2f),
        ) {
            Text(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        animateColorAsState(
                            targetValue = if (amPm == 0) MaterialTheme.colors.primary else Color.Transparent,
                            animationSpec = tween(500)
                        ).value
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { onAmPmChange(0) }
                    )
                    .padding(8.dp),
                text = stringResource(id = R.string.am),
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        animateColorAsState(
                            targetValue = if (amPm == 1) MaterialTheme.colors.primary else Color.Transparent,
                            animationSpec = tween(500)
                        ).value
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { onAmPmChange(1) }
                    )
                    .padding(8.dp),
                text = stringResource(id = R.string.pm),
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun HeaderRow(
    hourState: LazyListState,
    minuteState: LazyListState,
    editMode: Boolean,
    title: String,
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
            style = MaterialTheme.typography.h6
        )

        IconButton(
            onClick = {
                val hourVisibleItems = hourState.layoutInfo.visibleItemsInfo
                val minuteVisibleItems = minuteState.layoutInfo.visibleItemsInfo

                val hour = hourVisibleItems[hourVisibleItems.lastIndex / 2].index
                val minute =
                    minuteVisibleItems[minuteVisibleItems.lastIndex / 2].index - 1

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

    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select alarm tone")
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