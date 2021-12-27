package com.github.amrmsaraya.clock.feature_stopwatch.presentation.ui.stopwatch_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.amrmsaraya.timer.Time
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StopwatchViewModel @Inject constructor() : ViewModel() {

    var uiState by mutableStateOf(StopwatchUiState())
        private set

    fun updateStopwatch(stopwatch: Time, status: Int) {
        uiState = uiState.copy(
            stopwatch = stopwatch,
            status = status
        )
    }

    fun updateLaps(laps: List<Pair<Time, Time>>) {
        uiState = uiState.copy(laps = laps)
    }
}