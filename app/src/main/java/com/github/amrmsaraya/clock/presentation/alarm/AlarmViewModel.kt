package com.github.amrmsaraya.clock.presentation.alarm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.amrmsaraya.clock.domain.entity.Alarm
import com.github.amrmsaraya.clock.domain.usecase.AlarmCRUDUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmCRUDUseCase: AlarmCRUDUseCase,
) : ViewModel() {

    private val intentChannel = Channel<AlarmIntent>()

    var uiState by mutableStateOf(AlarmUiState())
        private set

    init {
        handleIntent()
        getAlarms()
    }

    fun sendIntent(intent: AlarmIntent) = viewModelScope.launch {
        intentChannel.send(intent)
    }

    private fun handleIntent() = viewModelScope.launch {
        intentChannel.consumeAsFlow().collect {
            when (it) {
                is AlarmIntent.InsertAlarm -> insertAlarm(it.alarm)
                is AlarmIntent.DeleteAlarms -> deleteAlarm(it.alarms)
                is AlarmIntent.GetClocks -> Unit
            }
        }
    }

    private fun insertAlarm(alarm: Alarm) = viewModelScope.launch {
        alarmCRUDUseCase.insert(alarm)
    }

    private fun deleteAlarm(alarms: List<Alarm>) = viewModelScope.launch {
        alarmCRUDUseCase.delete(alarms)
    }

    private fun getAlarms() = viewModelScope.launch {
        alarmCRUDUseCase.getClocks().collect {
            uiState = uiState.copy(alarms = it)
        }
    }
}