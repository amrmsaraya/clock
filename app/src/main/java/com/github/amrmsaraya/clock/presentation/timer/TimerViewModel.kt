package com.github.amrmsaraya.clock.presentation.timer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.amrmsaraya.clock.domain.usecase.TimerCRUDUseCase
import com.github.amrmsaraya.timer.Time
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.github.amrmsaraya.clock.domain.entity.Timer as LocalTimer

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timerCRUDUseCase: TimerCRUDUseCase
) : ViewModel() {

    private val intentChannel = Channel<TimerIntent>()

    var uiState by mutableStateOf(TimerUiState(configuredTime = 15 * 60 * 1000))
        private set

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
        uiState = uiState.copy(
            timer = timer,
            status = status
        )
    }

    private fun configureTime(timeMillis: Long) = viewModelScope.launch {
        uiState = uiState.copy(configuredTime = timeMillis)
    }

    private fun insert(timer: LocalTimer) = viewModelScope.launch {
        timerCRUDUseCase.insert(timer)
    }

    private fun delete(timer: LocalTimer) = viewModelScope.launch {
        timerCRUDUseCase.delete(timer)
    }

    private fun getLocalTimers() = viewModelScope.launch {
        timerCRUDUseCase.getTimers().collect {
            uiState = uiState.copy(timers = it)
        }
    }
}