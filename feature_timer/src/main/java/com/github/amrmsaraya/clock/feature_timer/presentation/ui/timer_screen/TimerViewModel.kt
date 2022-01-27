package com.github.amrmsaraya.clock.feature_timer.presentation.ui.timer_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.amrmsaraya.clock.feature_timer.domain.entity.Timer
import com.github.amrmsaraya.clock.feature_timer.domain.usecase.TimerCRUDUseCase
import com.github.amrmsaraya.timer.Time
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timerCRUDUseCase: TimerCRUDUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val intentChannel = Channel<TimerIntent>()

    var uiState by mutableStateOf(TimerUiState(configuredTime = 15 * 60 * 1000))
        private set

    init {
        handleIntent()
        getLocalTimers()
    }

    fun sendIntent(intent: TimerIntent) = viewModelScope.launch(dispatcher) {
        intentChannel.send(intent)
    }

    private fun handleIntent() = viewModelScope.launch(dispatcher) {
        intentChannel.consumeAsFlow().collect {
            when (it) {
                is TimerIntent.UpdateTimer -> updateTimer(it.timer, it.status)
                is TimerIntent.ConfigureTime -> configureTime(it.timeMillis)
                is TimerIntent.Insert -> insert(it.timer)
                is TimerIntent.Delete -> delete(it.timer)
                TimerIntent.GetTimers -> getLocalTimers()
            }
        }
    }

    private fun updateTimer(timer: Time, status: Int) = viewModelScope.launch(dispatcher) {
        uiState = uiState.copy(
            timer = timer,
            status = status
        )
    }

    private fun configureTime(timeMillis: Long) = viewModelScope.launch(dispatcher) {
        uiState = uiState.copy(configuredTime = timeMillis)
    }

    private fun insert(timer: Timer) = viewModelScope.launch(dispatcher) {
        timerCRUDUseCase.insert(timer)
    }

    private fun delete(timer: Timer) = viewModelScope.launch(dispatcher) {
        timerCRUDUseCase.delete(timer)
    }

    private fun getLocalTimers() = viewModelScope.launch(dispatcher) {
        timerCRUDUseCase.getTimers().collect {
            uiState = uiState.copy(timers = it)
        }
    }
}