package com.github.amrmsaraya.clock.presentation.stopwatch

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StopwatchViewModel @Inject constructor() : ViewModel() {
    var uiState = mutableStateOf(StopwatchUiState())
}