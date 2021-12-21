package com.github.amrmsaraya.clock.presentation.alarm

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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

    private var _uiState = mutableStateOf(AlarmUiState())
    val uiState: State<AlarmUiState> = _uiState

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
            _uiState.value = _uiState.value.copy(alarms = it)
        }
    }
}