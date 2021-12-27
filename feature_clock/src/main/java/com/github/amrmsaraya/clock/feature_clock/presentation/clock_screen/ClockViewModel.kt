package com.github.amrmsaraya.clock.feature_clock.presentation.clock_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.amrmsaraya.clock.feature_clock.domain.entity.Clock
import com.github.amrmsaraya.clock.feature_clock.domain.usecase.ClockCRUDUseCase
import com.github.amrmsaraya.clock.feature_clock.utils.convertToWorldClock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ClockViewModel @Inject constructor(
    private val clockCRUDUseCase: ClockCRUDUseCase,
) : ViewModel() {

    private var worldClocks = listOf<Clock>()
    private val timeZones = TimeZone.getAvailableIDs().map { TimeZone.getTimeZone(it) }

    var uiState by mutableStateOf(ClockUiState(timeZones = timeZones))
        private set

    private val intentChannel = Channel<ClockIntent>()

    init {
        handleIntents()
        getLocalClock()
        getWorldClocks()
        emitWorldClocks()
    }

    private fun handleIntents() = viewModelScope.launch {
        intentChannel.consumeAsFlow().collect {
            when (it) {
                is ClockIntent.InsertClock -> insertClock(it.timeZone)
                is ClockIntent.DeleteClocks -> deleteClocks(it.timeZones)
                is ClockIntent.GetClocks -> Unit
            }
        }
    }

    fun sendIntent(clockIntent: ClockIntent) = viewModelScope.launch {
        intentChannel.send(clockIntent)
    }

    private fun insertClock(timeZone: TimeZone) = viewModelScope.launch {
        clockCRUDUseCase.insert(
            Clock(
                id = timeZone.id,
                displayName = timeZone.getDisplayName(false, TimeZone.SHORT)
            )
        )
    }

    private fun deleteClocks(timeZones: List<TimeZone>) = viewModelScope.launch {
        clockCRUDUseCase.delete(
            timeZones.map {
                Clock(
                    id = it.id,
                    displayName = it.getDisplayName(false, TimeZone.SHORT)
                )
            }
        )
    }

    private fun getLocalClock() = viewModelScope.launch {
        val flow = withContext(Dispatchers.Default) {
            flow {
                while (true) {
                    delay(10)
                    emit(TimeZone.getDefault().convertToWorldClock())
                }
            }
        }
        uiState = uiState.copy(localClock = flow)
    }

    private fun getWorldClocks() = viewModelScope.launch(Dispatchers.Default) {
        clockCRUDUseCase.getClocks().collect {
            worldClocks = it
        }
    }

    private fun emitWorldClocks() = viewModelScope.launch {
        val flow = withContext(Dispatchers.Default) {
            flow {
                while (true) {
                    emit(
                        worldClocks.associate {
                            val timeZone = TimeZone.getTimeZone(it.id)
                            val worldClock = timeZone.convertToWorldClock()
                            timeZone to worldClock
                        }
                    )
                    delay(100)
                }
            }
        }
        uiState = uiState.copy(worldClocks = flow)
    }

}

