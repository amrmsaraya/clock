package com.github.amrmsaraya.clock.presentation.timer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.amrmsaraya.clock.domain.usecase.TimerCRUDUseCase
import com.github.amrmsaraya.timer.Timer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.github.amrmsaraya.clock.domain.entity.Timer as LocalTimer

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timerCRUDUseCase: TimerCRUDUseCase
) : ViewModel() {
    private val timer = Timer(viewModelScope)

    private val intentChannel = Channel<TimerIntent>()

    private var _uiState = mutableStateOf(TimerUiState())
    val uiState: State<TimerUiState> = _uiState

    init {
        handleIntent()
        getTimer()
        getLocalTimers()
        configure(timeMillis = 15 * 60 * 1000)
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
                is TimerIntent.Insert -> insert(it.timer)
                is TimerIntent.Delete -> delete(it.timer)
                TimerIntent.GetTimers -> Unit
            }
        }
    }

    private fun getTimer() = viewModelScope.launch {
        timer.getTimer(onFinish = { reset() }).collect {
            _uiState.value = _uiState.value.copy(
                timer = it,
                status = timer.status
            )
        }
    }

    private fun configure(timeMillis: Long, delay: Long = 10) = viewModelScope.launch {
        timer.configure(timeMillis, delay)
        _uiState.value = _uiState.value.copy(configuredTime = timeMillis)
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

    private fun insert(timer: LocalTimer) = viewModelScope.launch(Dispatchers.Default) {
        timerCRUDUseCase.insert(timer)
    }

    private fun delete(timer: LocalTimer) = viewModelScope.launch(Dispatchers.Default) {
        timerCRUDUseCase.delete(timer)
    }

    private fun getLocalTimers() = viewModelScope.launch(Dispatchers.Default) {
        timerCRUDUseCase.getTimers().collect {
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(timers = it)
            }
        }
    }
}