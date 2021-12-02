package com.github.amrmsaraya.clock.presentation.clock

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.amrmsaraya.clock.presentation.alarm.BottomDrawerSheet
import com.github.amrmsaraya.clock.presentation.alarm.FAB
import com.github.amrmsaraya.clock.presentation.theme.Red500
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@ExperimentalStdlibApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun ClockScreen(
    modifier: Modifier,
    onShowBottomNavigation: (Boolean) -> Unit,
    onBackPress: () -> Unit,
    viewModel: ClockViewModel = viewModel(),
) {
    val uiState by viewModel.uiState
    val time by uiState.time.collectAsState(initial = Time())
    val times by uiState.times.collectAsState(initial = mapOf())

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
                    viewModel.myZones.add(it)
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
                times = times
            ) {
                scope.launch {
                    onShowBottomNavigation(false)
                    drawerState.expand()
                }
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
private fun ClockScreenContent(
    scaffoldState: ScaffoldState,
    time: Time,
    times: Map<TimeZone, Time>,
    onAddClock: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FAB {
                onAddClock()
            }
        }
    ) {
        Column(Modifier.fillMaxSize()) {
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
                items(times.keys.toList()) {
                    ClockItem(times[it]!!, it)
                }
            }
        }
    }
}

@Composable
private fun ClockItem(time: Time, timeZone: TimeZone) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Clock(
                modifier = Modifier.size(60.dp),
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
        Text(
            text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(time.timeInMillis),
            fontSize = 22.sp
        )
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