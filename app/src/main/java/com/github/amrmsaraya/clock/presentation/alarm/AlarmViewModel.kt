package com.github.amrmsaraya.clock.presentation.alarm

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

class AlarmViewModel : ViewModel() {
    private val intentChannel = Channel<AlarmIntent>()

    private var _uiState = mutableStateOf(AlarmUiState())
    val uiState: State<AlarmUiState> = _uiState

    init {
        handleIntent()
    }

    fun sendIntent(intent: AlarmIntent) = viewModelScope.launch {
        intentChannel.send(intent)
    }

    private fun handleIntent() = viewModelScope.launch {
        intentChannel.consumeAsFlow().collect {
            when (it) {

            }
        }
    }
}