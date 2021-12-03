package com.github.amrmsaraya.clock.presentation.clock

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.amrmsaraya.clock.presentation.alarm.AddFAB
import com.github.amrmsaraya.clock.presentation.alarm.BottomDrawerSheet
import com.github.amrmsaraya.clock.presentation.alarm.DeleteFAB
import com.github.amrmsaraya.clock.presentation.theme.Red500
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun ClockScreen(
    modifier: Modifier,
    onShowBottomNavigation: (Boolean) -> Unit,
    onBackPress: () -> Unit,
    viewModel: ClockViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState
    val time by uiState.time.collectAsState(initial = Time())
    val clocks by uiState.clocks.collectAsState(initial = mapOf())

    LaunchedEffect(key1 = true) {
        viewModel.sendIntent(ClockIntent.GetClocks)
    }

    val scaffoldState = rememberScaffoldState()
    val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val localKeyboard = LocalSoftwareKeyboardController.current

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
            AddClock(
                timeZones = uiState.timeZones,
                onClick = {
                    viewModel.sendIntent(ClockIntent.InsertClock(it))
                    scope.launch {
                        localKeyboard?.hide()
                        onShowBottomNavigation(true)
                        drawerState.close()
                    }
                }
            )
        },
        content = {
            ClockScreenContent(
                scaffoldState = scaffoldState,
                time = time,
                times = clocks,
                onAddClock = {
                    scope.launch {
                        onShowBottomNavigation(false)
                        drawerState.expand()
                    }
                },
                onDeleteClocks = {
                    viewModel.sendIntent(ClockIntent.DeleteClocks(it))
                }
            )
        }
    )
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun ClockScreenContent(
    scaffoldState: ScaffoldState,
    time: Time,
    times: Map<TimeZone, Time>,
    onAddClock: () -> Unit,
    onDeleteClocks: (List<TimeZone>) -> Unit
) {
    var selectMode by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<TimeZone>() }
    val deletedItems = remember { mutableStateListOf<TimeZone>() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        floatingActionButton = {
            if (selectMode) {
                DeleteFAB {
                    deletedItems.addAll(selectedItems)
                    scope.launch {
                        delay(1001)
                        onDeleteClocks(selectedItems)
                        selectedItems.clear()
                    }
                    selectMode = false
                }
            } else {
                AddFAB { onAddClock() }
            }
        },
        floatingActionButtonPosition = if (selectMode) FabPosition.Center else FabPosition.End
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = if (times.isEmpty()) Arrangement.Center else Arrangement.Top
        ) {
            Clock(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.8f)
                    .align(CenterHorizontally),
                time = time,
                color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
            )
            Spacer(modifier = Modifier.size(16.dp))
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                itemsIndexed(times.keys.toList()) { index, timeZone ->
                    AnimatedVisibility(
                        visible = timeZone !in deletedItems,
                        enter = expandVertically(animationSpec = tween(durationMillis = 1000)),
                        exit = shrinkVertically(animationSpec = tween(durationMillis = 1000))
                    ) {
                        ClockItem(
                            time = times[timeZone]!!,
                            timeZone = timeZone,
                            selected = timeZone in selectedItems,
                            selectMode = selectMode,
                            onSelectMode = { selectMode = true },
                            onSelect = {
                                if (it) {
                                    selectedItems.add(timeZone)
                                } else {
                                    selectedItems.remove(timeZone)
                                    selectMode = selectedItems.isNotEmpty()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun ClockItem(
    time: Time,
    timeZone: TimeZone,
    selected: Boolean,
    selectMode: Boolean,
    onSelectMode: () -> Unit,
    onSelect: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
            .combinedClickable(
                onClick = {
                    if (selectMode) {
                        onSelect(!selected)
                    }
                },
                onLongClick = {
                    onSelectMode()
                    onSelect(true)
                },
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Clock(
                modifier = Modifier.size(50.dp),
                time = time,
                color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column {
                Text(text = timeZone.id.substringAfter('/'), fontSize = 18.sp)
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = timeZone.getDisplayName(false, TimeZone.SHORT),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${time.calendar.get(Calendar.HOUR)}:${
                    "%02d".format(
                        time.calendar.get(
                            Calendar.MINUTE
                        )
                    )
                } ${
                    if (time.calendar.get(
                            Calendar.AM_PM
                        ) == 0
                    ) "AM" else "PM"
                }",

                fontSize = 22.sp
            )
            if (selectMode) {
                Spacer(modifier = Modifier.size(8.dp))
                Checkbox(checked = selected, onCheckedChange = onSelect)
            }
        }
    }
}

@Composable
private fun Clock(
    modifier: Modifier = Modifier,
    time: Time,
    color: Color
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(if (maxWidth < maxHeight) maxWidth else maxHeight)) {
            val radius = size.width / 2

            hoursDial(radius, color)
            minutesDial(radius, color)
            hourHand(
                hour = time.hours,
                radius = radius,
                color = color
            )
            minutesHand(
                minute = time.minutes,
                radius = radius,
                color = color
            )
            secondsHand(
                second = time.seconds,
                radius = radius,
                color = Red500
            )
            drawCircle(
                color = color,
                radius = radius * .03f,
                center = center
            )
        }
    }
}

private fun DrawScope.hoursDial(radius: Float, color: Color) {
    for (angle in 0..360 step 30) {
        drawLine(
            color = color,
            start = Offset(
                x = center.x + (radius) * cos(angle * Math.PI.toFloat() / 180),
                y = center.y + (radius) * sin(angle * Math.PI.toFloat() / 180)
            ),
            end = Offset(
                x = center.x + (radius - radius * 0.125f) * cos(angle * Math.PI.toFloat() / 180),
                y = center.y + (radius - radius * 0.125f) * sin(angle * Math.PI.toFloat() / 180)
            ),
            strokeWidth = radius * 0.01f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.minutesDial(radius: Float, color: Color) {
    for (angle in 0..360 step 6) {
        drawLine(
            color = color,
            start = Offset(
                x = center.x + (radius) * cos(angle * Math.PI.toFloat() / 180),
                y = center.y + (radius) * sin(angle * Math.PI.toFloat() / 180)
            ),
            end = Offset(
                x = center.x + (radius - radius * 0.05f) * cos(angle * Math.PI.toFloat() / 180),
                y = center.y + (radius - radius * 0.05f) * sin(angle * Math.PI.toFloat() / 180)
            ),
            strokeWidth = radius * 0.01f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.hourHand(
    hour: Float,
    radius: Float,
    color: Color,
) {
    drawLine(
        color = color,
        start = Offset(x = center.x, y = center.y),
        end = Offset(
            x = center.x + (radius * 0.533f) * cos(hour * Math.PI.toFloat() / 180),
            y = center.y + (radius * 0.533f) * sin(hour * Math.PI.toFloat() / 180)
        ),
        strokeWidth = radius * 0.015f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.minutesHand(
    minute: Float,
    radius: Float,
    color: Color,
) {
    drawLine(
        color = color,
        start = Offset(x = center.x, y = center.y),
        end = Offset(
            x = center.x + (radius * 0.8f) * cos(minute * Math.PI.toFloat() / 180),
            y = center.y + (radius * 0.8f) * sin(minute * Math.PI.toFloat() / 180)
        ),
        strokeWidth = radius * 0.015f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.secondsHand(
    second: Float,
    radius: Float,
    color: Color,
) {
    drawLine(
        color = color,
        start = Offset(x = center.x, y = center.y),
        end = Offset(
            x = center.x + (radius * 0.66f) * cos(second * Math.PI.toFloat() / 180),
            y = center.y + (radius * 0.66f) * sin(second * Math.PI.toFloat() / 180)
        ),
        strokeWidth = radius * 0.01f,
        cap = StrokeCap.Round
    )
}

@Composable
fun AddClock(timeZones: List<TimeZone>, onClick: (TimeZone) -> Unit) {
    val zones = remember { mutableListOf<TimeZone>() }
    var search by remember { mutableStateOf("") }

    if (search.isNotEmpty()) {
        zones.clear()
        zones.addAll(timeZones.filter { it.id.contains(search, ignoreCase = true) })
    } else {
        zones.addAll(timeZones)
    }

    BasicTextField(
        value = search,
        onValueChange = { search = it },
        textStyle = TextStyle(color = MaterialTheme.colors.onSurface, fontSize = 18.sp),
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        singleLine = true,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            elevation = 3.dp,
            shape = CircleShape
        ) {
            Row(Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp)) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.size(8.dp))
                Box {
                    if (search.isEmpty()) {
                        Text(text = "City, country or region", color = Color.Gray, fontSize = 18.sp)
                    }
                    it()
                }
            }
        }
    }

    Spacer(modifier = Modifier.size(16.dp))

    LazyColumn(Modifier.fillMaxWidth()) {
        items(zones) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .clickable {
                        search = ""
                        zones.clear()
                        zones.addAll(timeZones)
                        onClick(it)
                    },
            ) {
                Column(
                    Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = it.id.substringAfter('/'), fontSize = 18.sp)
                    Text(
                        text = it.getDisplayName(false, TimeZone.SHORT),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
        }
    }
}