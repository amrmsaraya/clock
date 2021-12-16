package com.github.amrmsaraya.clock.presentation.clock

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.amrmsaraya.clock.domain.entity.WorldClock
import com.github.amrmsaraya.clock.presentation.clock.component.AddClock
import com.github.amrmsaraya.clock.presentation.clock.component.MaterialClock
import com.github.amrmsaraya.clock.presentation.clock.component.WorldClockRow
import com.github.amrmsaraya.clock.presentation.common_ui.AddFAB
import com.github.amrmsaraya.clock.presentation.common_ui.DeleteFAB
import com.github.amrmsaraya.clock.presentation.common_ui.getSurfaceColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun ClockScreen(
    modifier: Modifier,
    onShowBottomNavigation: (Boolean) -> Unit,
    onBackPress: () -> Unit,
    viewModel: ClockViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState
    val localClock by uiState.localClock.collectAsState(initial = WorldClock())
    val worldClocks by uiState.worldClocks.collectAsState(initial = mapOf())
    var selectMode by remember { mutableStateOf(false) }
    val localKeyboard = LocalSoftwareKeyboardController.current

    var showDialog by remember { mutableStateOf(false) }

    BackHandler {
        when {
            showDialog -> {
                showDialog = false
                onShowBottomNavigation(true)
            }
            selectMode -> selectMode = false
            else -> onBackPress()
        }
    }

    AnimatedVisibility(
        visible = showDialog,
        enter = slideInHorizontally(
            initialOffsetX = { it },
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { it }
        )
    ) {
        AddClock(
            timeZones = uiState.timeZones,
            onClick = {
                viewModel.sendIntent(ClockIntent.InsertClock(it))
                localKeyboard?.hide()
                showDialog = false
                onShowBottomNavigation(true)
            }
        )
    }

    AnimatedVisibility(
        visible = !showDialog,
        enter = slideInHorizontally(
            initialOffsetX = { -it },
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { -it }
        )
    ) {
        ClockScreenContent(
            modifier = modifier,
            worldClock = localClock,
            times = worldClocks,
            selectMode = selectMode,
            isDeleted = uiState.isDeleted,
            onAddClock = {
                showDialog = true
                onShowBottomNavigation(false)
            },
            onDeleteClocks = { viewModel.sendIntent(ClockIntent.DeleteClocks(it)) },
            onSelectMode = { selectMode = it },
            onResetDeleteFlag = { viewModel.sendIntent(ClockIntent.ResetDeleteFlag) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClockScreenContent(
    modifier: Modifier = Modifier,
    worldClock: WorldClock,
    times: Map<TimeZone, WorldClock>,
    selectMode: Boolean,
    isDeleted: Boolean,
    onResetDeleteFlag: () -> Unit,
    onAddClock: () -> Unit,
    onDeleteClocks: (List<TimeZone>) -> Unit,
    onSelectMode: (Boolean) -> Unit,
) {
    val selectedItems = remember { mutableStateListOf<TimeZone>() }
    val deletedItems = remember { mutableStateListOf<TimeZone>() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            delay(300 * 2)
            deletedItems.clear()
            selectedItems.clear()
            onResetDeleteFlag()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (selectMode) {
                DeleteFAB {
                    scope.launch {
                        deletedItems.addAll(selectedItems)
                        onSelectMode(false)

                        delay(300 * 2)
                        onDeleteClocks(selectedItems)
                    }
                }
            } else {
                AnimatedVisibility(
                    visible = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 50,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut(tween(500))
                ) {
                    AddFAB { onAddClock() }
                }
            }
        },
        floatingActionButtonPosition = if (selectMode) FabPosition.Center else FabPosition.End
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = if (times.isEmpty()) Arrangement.Center else Arrangement.Top
        ) {
            MaterialClock(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .align(CenterHorizontally),
                worldClock = worldClock,
                frameColor = MaterialTheme.colorScheme.primary,
                handleColor = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(16.dp))
            LazyColumn(
                state = listState,
                horizontalAlignment = CenterHorizontally
            ) {
                items(times.keys.toList()) { timeZone ->
                    AnimatedVisibility(
                        visible = !deletedItems.contains(timeZone),
                        enter = EnterTransition.None,
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        WorldClockRow(
                            worldClock = times[timeZone]!!,
                            timeZone = timeZone,
                            selected = timeZone in selectedItems,
                            selectMode = selectMode,
                            onSelectMode = {
                                onSelectMode(true)
                                selectedItems.clear()
                            },
                            onSelect = { selected ->
                                if (selected) {
                                    selectedItems.add(timeZone)
                                } else {
                                    selectedItems.remove(timeZone)
                                    onSelectMode(selectedItems.isNotEmpty())
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}