package com.github.amrmsaraya.clock.presentation.timer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.amrmsaraya.clock.domain.usecase.TimerCRUDUseCase
import com.github.amrmsaraya.timer.Time
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
    private val intentChannel = Channel<TimerIntent>()

    private var _uiState = mutableStateOf(TimerUiState(configuredTime = 15 * 60 * 1000))
    val uiState: State<TimerUiState> = _uiState

    init {
        handleIntent()
        getLocalTimers()
    }

    fun sendIntent(intent: TimerIntent) = viewModelScope.launch {
        intentChannel.send(intent)
    }

    private fun handleIntent() = viewModelScope.launch {
        intentChannel.consumeAsFlow().collect {
            when (it) {
                is TimerIntent.UpdateTimer -> updateTimer(it.timer, it.status)
                is TimerIntent.ConfigureTime -> configureTime(it.timeMillis)
                is TimerIntent.Insert -> insert(it.timer)
                is TimerIntent.Delete -> delete(it.timer)
                TimerIntent.GetTimers -> Unit
            }
        }
    }

    private fun updateTimer(timer: Time, status: Int) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(
            timer = timer,
            status = status
        )
    }

    private fun configureTime(timeMillis: Long) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(
            configuredTime = timeMillis
        )
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