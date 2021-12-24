package com.github.amrmsaraya.clock.presentation.clock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.amrmsaraya.clock.domain.entity.Clock
import com.github.amrmsaraya.clock.domain.entity.WorldClock
import com.github.amrmsaraya.clock.domain.usecase.ClockCRUDUseCase
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
                    emit(convertToWorldClock(TimeZone.getDefault()))
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
                            val worldClock = convertToWorldClock(timeZone)
                            timeZone to worldClock
                        }
                    )
                    delay(100)
                }
            }
        }
        uiState = uiState.copy(worldClocks = flow)
    }

    private fun convertToWorldClock(timeZone: TimeZone): WorldClock {
        val calendar = Calendar.getInstance(timeZone)
        val seconds =
            270 + (calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND)) * 6 / 1000f
        val minutes =
            270 + calendar.get(Calendar.MINUTE) * 6 + calendar.get(Calendar.SECOND) / 10f
        val hours =
            270 + calendar.get(Calendar.HOUR) * 30 + calendar.get(Calendar.MINUTE) / 2f

        return WorldClock(
            calendar = calendar,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
        )
    }
}

