package com.github.amrmsaraya.clock.feature_alarm.presentation.ui.alarm_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.amrmsaraya.clock.feature_alarm.domain.entity.Alarm
import com.github.amrmsaraya.clock.feature_alarm.domain.usecase.AlarmCRUDUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmCRUDUseCase: AlarmCRUDUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val intentChannel = Channel<AlarmIntent>()

    var uiState by mutableStateOf(AlarmUiState())
        private set

    init {
        handleIntent()
        getAlarms()
    }

    fun sendIntent(intent: AlarmIntent) = viewModelScope.launch(dispatcher) {
        intentChannel.send(intent)
    }

    private fun handleIntent() = viewModelScope.launch(dispatcher) {
        intentChannel.consumeAsFlow().collect {
            when (it) {
                is AlarmIntent.InsertAlarm -> insertAlarm(it.alarm)
                is AlarmIntent.DeleteAlarms -> deleteAlarm(it.alarms)
                is AlarmIntent.GetAlarms -> getAlarms()
            }
        }
    }

    private fun insertAlarm(alarm: Alarm) = viewModelScope.launch(dispatcher) {
        alarmCRUDUseCase.insert(alarm)
    }

    private fun deleteAlarm(alarms: List<Alarm>) = viewModelScope.launch(dispatcher) {
        alarmCRUDUseCase.delete(alarms)
    }

    private fun getAlarms() = viewModelScope.launch(dispatcher) {
        alarmCRUDUseCase.getAlarms().collect {
            uiState = uiState.copy(alarms = it)
        }
    }
}