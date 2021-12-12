package com.github.amrmsaraya.clock.presentation.stopwatch

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.amrmsaraya.timer.Stopwatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StopwatchViewModel @Inject constructor() : ViewModel() {
    private val stopwatch = Stopwatch(viewModelScope)

    private val intentChannel = Channel<StopwatchIntent>()

    private var _uiState = mutableStateOf(StopwatchUiState())
    val uiState: State<StopwatchUiState> = _uiState

    init {
        handleIntent()
        getStopwatch()
        getLaps()
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
                StopwatchIntent.Lap -> lap()
                is StopwatchIntent.Configure -> configure(it.delay)
            }
        }
    }

    private fun getStopwatch() = viewModelScope.launch {
        stopwatch.getStopwatch().collect {
            _uiState.value = _uiState.value.copy(
                stopwatch = it,
                status = stopwatch.status
            )
        }
    }

    private fun getLaps() = viewModelScope.launch {
        stopwatch.getLaps().collect {
            _uiState.value = _uiState.value.copy(laps = it)
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

    private fun lap() = viewModelScope.launch(Dispatchers.Default) {
        stopwatch.lap()
    }
}