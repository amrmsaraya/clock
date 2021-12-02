package com.github.amrmsaraya.clock.presentation.stopwatch

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.amrmsaraya.timer.Stopwatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class StopwatchViewModel : ViewModel() {
    private val stopwatch = Stopwatch()

    private val intentChannel = Channel<StopwatchIntent>()

    private var _uiState = mutableStateOf(StopwatchUiState())
    val uiState: State<StopwatchUiState> = _uiState

    init {
        handleIntent()
        getStopwatch()
    }

    fun sendIntent(intent: StopwatchIntent) = viewModelScope.launch {
        intentChannel.send(intent)
    }

    private fun handleIntent() = viewModelScope.launch {
        intentChannel.consumeAsFlow().collect {
            when (it) {
                StopwatchIntent.Start -> start()
                StopwatchIntent.Pause -> pause()
                StopwatchIntent.Reset -> reset()
                is StopwatchIntent.Configure -> configure(it.delay)
            }
        }
    }

    private fun getStopwatch() = viewModelScope.launch {
        stopwatch.getStopwatch().collect {
            _uiState.value = StopwatchUiState(
                stopwatch = it,
                isRunning = stopwatch.isRunning
            )
        }
    }

    fun configure(delay: Long) {
        stopwatch.configure(delay)
    }

    private fun start() = viewModelScope.launch(Dispatchers.Default) {
        stopwatch.start()
    }

    private fun pause() = viewModelScope.launch(Dispatchers.Default) {
        stopwatch.pause()
    }

    private fun reset() = viewModelScope.launch(Dispatchers.Default) {
        stopwatch.reset()
    }
}