package com.github.amrmsaraya.clock.presentation.stopwatch

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.github.amrmsaraya.timer.Time
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StopwatchViewModel @Inject constructor() : ViewModel() {

    private val _uiState = mutableStateOf(StopwatchUiState())
    val uiState: State<StopwatchUiState> = _uiState

    fun updateStopwatch(stopwatch: Time, status: Int) {
        _uiState.value = _uiState.value.copy(
            stopwatch = stopwatch,
            status = status
        )
    }

    fun updateLaps(laps: List<Pair<Time, Time>>) {
        _uiState.value = _uiState.value.copy(laps = laps)
    }
}