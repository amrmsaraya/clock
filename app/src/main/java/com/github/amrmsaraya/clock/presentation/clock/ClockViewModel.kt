package com.github.amrmsaraya.clock.presentation.clock

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.amrmsaraya.clock.domain.entity.Clock
import com.github.amrmsaraya.clock.domain.usecase.ClockCRUDUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ClockViewModel @Inject constructor(
    private val clockCRUDUseCase: ClockCRUDUseCase,
) : ViewModel() {

    private val timeZones = TimeZone.getAvailableIDs().map { TimeZone.getTimeZone(it) }
    val uiState = mutableStateOf(ClockUiState(timeZones = timeZones))
    private val intentChannel = Channel<ClockIntent>()
    private val clocksChannel = Channel<Map<TimeZone, Time>> { }
    private var clocks = listOf<Clock>()

    init {
        handleIntents()
        getTime()
        getClocks()
        emitClocks()
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

    private fun getTime() = viewModelScope.launch {
        uiState.value = uiState.value.copy(
            time = flow {
                while (true) {
                    delay(10)
                    emit(getClock(TimeZone.getDefault()))
                }
            }
        )
    }

    private fun getClocks() = viewModelScope.launch(Dispatchers.Default) {
        clockCRUDUseCase.getClocks().collect {
            clocks = it
        }
    }

    private fun emitClocks() = viewModelScope.launch {
        uiState.value = uiState.value.copy(
            clocks = flow {
                while (true) {
                    delay(10)
                    emit(
                        clocks.associate {
                            val timeZone = TimeZone.getTimeZone(it.id)
                            val time = getClock(timeZone)
                            timeZone to time
                        }
                    )
                }
            }
        )
    }

    private suspend fun clockJob(myZones: List<Clock>) =
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                delay(10)
                clocksChannel.send(
                    myZones.associate {
                        val timeZone = TimeZone.getTimeZone(it.id)
                        val time = getClock(timeZone)
                        timeZone to time
                    }
                )
            }
        }

    private fun getClock(timeZone: TimeZone): Time {
        val calendar = Calendar.getInstance(timeZone)
        val seconds =
            (calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND)) * 6 / 1000f + 270
        val minutes =
            calendar.get(Calendar.MINUTE) * 6 + 270 + calendar.get(Calendar.SECOND) / 10f
        val hours =
            calendar.get(Calendar.HOUR) * 30 + 270 + calendar.get(Calendar.MINUTE) / 2f

        return Time(
            calendar = calendar,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
        )
    }

}

