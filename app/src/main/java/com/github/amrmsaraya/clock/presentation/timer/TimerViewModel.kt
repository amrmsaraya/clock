package com.github.amrmsaraya.clock.presentation.timer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.amrmsaraya.timer.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private val timer = Timer(viewModelScope)

    private val intentChannel = Channel<TimerIntent>()

    private var _uiState = mutableStateOf(TimerUiState())
    val uiState: State<TimerUiState> = _uiState

    init {
        handleIntent()
        getTimer()
        configure(10_000)
    }

    fun sendIntent(intent: TimerIntent) = viewModelScope.launch {
        intentChannel.send(intent)
    }

    private fun handleIntent() = viewModelScope.launch {
        intentChannel.consumeAsFlow().collect {
            when (it) {
                TimerIntent.Start -> start()
                TimerIntent.Pause -> pause()
                TimerIntent.Reset -> reset()
                is TimerIntent.Configure -> configure(it.timeMillis, it.delay)
            }
        }
    }

    private fun getTimer() = viewModelScope.launch {
        timer.getTimer(onFinish = { reset() }).collect {
            _uiState.value = TimerUiState(
                timer = it,
                configuredTime = timer.configuredTime,
                status = timer.status
            )
        }
    }

    private fun configure(timeMillis: Long, delay: Long = 10) =
        viewModelScope.launch(Dispatchers.Default) {
            timer.configure(timeMillis, delay)
        }

    private fun start() = viewModelScope.launch(Dispatchers.Default) {
        timer.start()
    }

    private fun pause() = viewModelScope.launch(Dispatchers.Default) {
        timer.pause()
    }

    private fun reset() = viewModelScope.launch(Dispatchers.Default) {
        timer.reset()
    }
}