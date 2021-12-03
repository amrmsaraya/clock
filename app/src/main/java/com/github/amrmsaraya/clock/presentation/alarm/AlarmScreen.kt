package com.github.amrmsaraya.clock.presentation.alarm

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
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
    viewModel: AlarmViewModel = viewModel(),
) {
    val uiState by viewModel.uiState
    val scaffoldState = rememberScaffoldState()
    val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
    val scope = rememberCoroutineScope()


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
                onSave = {
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
            item {
                var checked by remember { mutableStateOf(true) }
                AlarmCard(
                    name = "Doctor",
                    time = "04:30 PM",
                    days = "Sat  Mon  Thu",
                    backgroundColor = Purple400,
                    contentColor = Purple900,
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
                Spacer(modifier = Modifier.size(16.dp))
            }

            item {
                var checked by remember { mutableStateOf(true) }
                AlarmCard(
                    name = "Wake up",
                    time = "09:00 AM",
                    days = "Mon  Tue  Wed  Thu  Fri",
                    backgroundColor = Teal400,
                    contentColor = Teal900,
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
                Spacer(modifier = Modifier.size(16.dp))
            }

            item {
                var checked by remember { mutableStateOf(true) }
                AlarmCard(
                    name = "Work",
                    time = "07:30 AM",
                    days = "Mon  Wed  Thu  Fri",
                    backgroundColor = Orange400,
                    contentColor = Orange900,
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
                Spacer(modifier = Modifier.size(16.dp))
            }

            item {
                var checked by remember { mutableStateOf(true) }
                AlarmCard(
                    name = "Wake up",
                    time = "09:00 AM",
                    days = "Mon  Tue  Wed  Thu  Fri",
                    backgroundColor = BlueGray400,
                    contentColor = BlueGray900,
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
                Spacer(modifier = Modifier.size(16.dp))
            }

            item {
                var checked by remember { mutableStateOf(true) }
                AlarmCard(
                    name = "Work",
                    time = "07:30 AM",
                    days = "Mon  Wed  Thu  Fri",
                    backgroundColor = Blue400,
                    contentColor = Blue900,
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
                Spacer(modifier = Modifier.size(16.dp))
            }

            item {
                var checked by remember { mutableStateOf(true) }
                AlarmCard(
                    name = "Work",
                    time = "07:30 AM",
                    days = "Mon  Wed  Thu  Fri",
                    backgroundColor = Pink400,
                    contentColor = Pink900,
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
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    val hoursState = rememberLazyListState()
    val minutesState = rememberLazyListState()

    val days = listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")
    val selectedDays = remember { mutableStateListOf<String>() }

    val colors = listOf(Purple400, Teal400, Orange400, BlueGray400, Blue400, Pink400)
    var selectedColor by remember { mutableStateOf(colors.first()) }

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

            TextButton(onClick = { onSave() }) {
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
                .fillMaxHeight(0.3f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TimeChooser(
                modifier
                    .fillMaxSize()
                    .weight(0.45f),
                hoursState,
                27
            )
            Text(
                modifier = Modifier.weight(0.1f),
                text = ":",
                fontSize = 26.sp,
                textAlign = TextAlign.Center
            )
            TimeChooser(
                modifier
                    .fillMaxSize()
                    .weight(0.45f), minutesState, 63
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
                    text = it
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

        Column() {
            Text(text = "Select Color", fontSize = 20.sp)
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                Modifier
                    .horizontalScroll(colorRowScrollState)
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                colors.forEach {
                    val animatedSize by animateDpAsState(
                        targetValue = if (it == selectedColor) 50.dp else 35.dp,
                        animationSpec = tween(500)
                    )
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .size(animatedSize)
                            .clip(CircleShape)
                            .background(it)
                            .clickable {
                                selectedColor = it
                            }
                    ) {
                        if (it == selectedColor) {
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
) {
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
                modifier = Modifier.padding(16.dp),
                text = if (it > size - 3 || it - 1 < 0) "" else "${it - 1}",
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