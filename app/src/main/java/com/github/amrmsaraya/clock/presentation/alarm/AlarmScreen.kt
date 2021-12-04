package com.github.amrmsaraya.clock.presentation.alarm

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.amrmsaraya.clock.domain.entity.Alarm
import com.github.amrmsaraya.clock.presentation.alarm.utils.Colors
import com.github.amrmsaraya.clock.presentation.alarm.utils.Days
import com.github.amrmsaraya.clock.presentation.common_ui.AddFAB
import com.github.amrmsaraya.clock.presentation.theme.*
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
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


    BackHandler {
        if (drawerState.isOpen) {
            scope.launch {
                onShowBottomNavigation(true)
                drawerState.close()
            }
        } else {
            onBackPress()
        }
    }

    BottomDrawerSheet(
        modifier = modifier.fillMaxSize(),
        drawerState = drawerState,
        drawerContent = {
            AddAlarm(
                hourState = hourState,
                minuteState = minuteState,
                onSave = { alarm ->
                    viewModel.sendIntent(AlarmIntent.InsertAlarm(alarm))
                    scope.launch {
                        onShowBottomNavigation(true)
                        drawerState.close()
                    }
                },
                onCancel = {
                    scope.launch {
                        onShowBottomNavigation(true)
                        drawerState.close()
                    }
                }
            )
        },
        content = {
            AlarmScreenContent(
                scaffoldState = scaffoldState,
                drawerState = drawerState,
                scope = scope,
                alarms = uiState.alarms,
                onShowBottomNavigation = onShowBottomNavigation
            )
        }
    )

}

@ExperimentalMaterialApi
@Composable
private fun AlarmScreenContent(
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
    drawerState: BottomDrawerState,
    alarms: List<Alarm>,
    onShowBottomNavigation: (Boolean) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        floatingActionButton = {
            AddFAB {
                scope.launch {
                    onShowBottomNavigation(false)
                    drawerState.expand()
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            items(alarms) { alarm ->
                var checked by remember { mutableStateOf(true) }
                AlarmCard(
                    name = alarm.title,
                    time = buildString {
                        append(alarm.hour)
                        append(":")
                        append("%02d".format(alarm.minute))
                        append(" ")
                        append(if (alarm.amPm == 0) "AM" else "PM")
                    },
                    days = Days.values().filter { it.ordinal in alarm.repeatOn }.map {
                        stringResource(id = it.stringRes)
                    }.joinToString("  "),
                    backgroundColor = Colors.values().filter { it.ordinal == alarm.color }.map {
                        it.background
                    }.firstOrNull() ?: Purple400,
                    contentColor = Colors.values().filter { it.ordinal == alarm.color }.map {
                        it.onBackground
                    }.firstOrNull() ?: Purple900,
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun AlarmCard(
    name: String,
    time: String,
    days: String,
    backgroundColor: Color,
    contentColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (checked) {
            backgroundColor
        } else {
            if (isSystemInDarkTheme()) {
                LocalElevationOverlay.current?.apply(
                    color = MaterialTheme.colors.surface,
                    elevation = LocalAbsoluteElevation.current + 1.dp
                ) ?: MaterialTheme.colors.surface
            } else {
                Color.LightGray
            }
        },
        animationSpec = tween(700)
    )

    val animateContentColor by animateColorAsState(
        targetValue = if (checked) contentColor else Color.Gray,
        animationSpec = tween(700)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {},
        backgroundColor = animatedBackgroundColor,
        elevation = 0.dp,
        shape = RoundedCornerShape(15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.body1,
                    color = Color.White
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = time,
                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Normal),
                    color = Color.White
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = days,
                    style = MaterialTheme.typography.body2,
                    color = animateContentColor
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = animateContentColor,
                    checkedTrackColor = animateContentColor,
                    uncheckedThumbColor = animateContentColor,
                    uncheckedTrackColor = animateContentColor
                )
            )
        }
    }
}

@Composable
fun AddAlarm(
    modifier: Modifier = Modifier,
    hourState: LazyListState,
    minuteState: LazyListState,
    onSave: (Alarm) -> Unit,
    onCancel: () -> Unit,
) {
    val days = Days.values()
    val selectedDays = remember { mutableStateListOf<Days>() }

    val backgroundColor = listOf(Purple400, Teal400, Orange400, BlueGray400, Blue400, Pink400)
    var selectedColor by remember { mutableStateOf(0) }

    var title by remember { mutableStateOf("") }

    val colorRowScrollState = rememberScrollState()
    val daysRowScrollState = rememberScrollState()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onCancel) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                )
            }

            Text(
                text = "Add Alarm",
                style = MaterialTheme.typography.h6
            )

            TextButton(
                onClick = {
                    val hourVisibileItems = hourState.layoutInfo.visibleItemsInfo
                    val minuteVisibileItems = minuteState.layoutInfo.visibleItemsInfo

                    val hour = hourVisibileItems[hourVisibileItems.lastIndex / 2].index
                    val minute =
                        minuteVisibileItems[minuteVisibileItems.lastIndex / 2].index - 1

                    onSave(
                        Alarm(
                            title = title,
                            hour = hour,
                            minute = minute,
                            amPm = 0,
                            color = selectedColor,
                            repeatOn = selectedDays.map { it.ordinal },
                            enabled = true,
                        )
                    )
                }
            ) {
                Text(
                    text = "Save",
                    fontSize = 16.sp,
                )
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

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
                type = "hour"
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
                type = "minute"
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Row(
            Modifier
                .align(CenterHorizontally)
                .horizontalScroll(daysRowScrollState),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            days.forEachIndexed { index, it ->
                Text(
                    modifier =
                    Modifier
                        .background(
                            color = if (it in selectedDays) Purple400 else Color.Transparent,
                            shape = RoundedCornerShape(
                                topStart = if (index == 0 || days[index - 1] !in selectedDays) 8.dp else 0.dp,
                                bottomStart = if (index == 0 || days[index - 1] !in selectedDays) 8.dp else 0.dp,
                                topEnd = if (index == days.lastIndex || days[index + 1] !in selectedDays) 8.dp else 0.dp,
                                bottomEnd = if (index == days.lastIndex || days[index + 1] !in selectedDays) 8.dp else 0.dp,
                            )
                        )
                        .clickable {
                            if (it in selectedDays) {
                                selectedDays.remove(it)
                            } else {
                                selectedDays.add(it)
                            }
                        }
                        .padding(10.dp),
                    text = stringResource(id = it.stringRes)
                )
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = { title = it },
            label = { Text(text = "Title") },
            maxLines = 1,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column {
            Text(text = "Select Color", fontSize = 20.sp)
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                Modifier
                    .horizontalScroll(colorRowScrollState)
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
                            .padding(6.dp)
                            .size(animatedSize)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { selectedColor = index }
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
}

@OptIn(ExperimentalUnitApi::class, ExperimentalSnapperApi::class)
@Composable
private fun TimeChooser(
    modifier: Modifier,
    state: LazyListState,
    size: Int,
    type: String,
    default: Int = if (type == "hour") 8 else 0
) {

    LaunchedEffect(key1 = true) {
        state.scrollToItem(default)
    }
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = CenterHorizontally,
        state = state,
        verticalArrangement = Arrangement.SpaceEvenly,
        flingBehavior = rememberSnapperFlingBehavior(lazyListState = state)
    ) {
        items(size) {
            val viewedItems = state.layoutInfo.visibleItemsInfo

            val animatedSp by animateValueAsState(
                targetValue = if (viewedItems.isNotEmpty()) {
                    if (viewedItems[(viewedItems.lastIndex) / 2].index == it) {
                        32.sp
                    } else {
                        18.sp
                    }
                } else {
                    18.sp
                },
                typeConverter = TwoWayConverter(
                    convertToVector = { AnimationVector1D(it.value) },
                    convertFromVector = { TextUnit(it.value, TextUnitType.Sp) }
                )
            )
            val animatedColor by animateColorAsState(
                targetValue = if (viewedItems.isNotEmpty()) {
                    if (viewedItems[(viewedItems.size - 1) / 2].index == it) {
                        MaterialTheme.colors.onSurface
                    } else {
                        Color.Gray
                    }
                } else {
                    Color.Gray
                }
            )
            Text(
                modifier = Modifier.padding(20.dp),
                text = if (it > size - 2 || it - 1 < 0) "" else "${
                    if (type == "hour") it else "%02d".format(
                        it - 1
                    )
                }",
                fontSize = animatedSp,
                color = animatedColor
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun BottomDrawerSheet(
    modifier: Modifier = Modifier,
    drawerState: BottomDrawerState,
    drawerContent: @Composable() (ColumnScope.() -> Unit),
    content: @Composable () -> Unit
) {
    BottomDrawer(
        modifier = modifier,
        gesturesEnabled = false,
        drawerState = drawerState,
        drawerElevation = 0.dp,
        scrimColor = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        drawerShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        drawerContent = {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Divider(
                    Modifier
                        .fillMaxWidth(.2f)
                        .clip(CircleShape)
                        .align(CenterHorizontally),
                    thickness = 5.dp
                )
                Spacer(modifier = Modifier.size(8.dp))
                drawerContent()
            }
        },
        content = content
    )
}