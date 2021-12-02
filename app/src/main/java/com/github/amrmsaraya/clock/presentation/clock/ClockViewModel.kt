package com.github.amrmsaraya.clock.presentation.clock

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.amrmsaraya.clock.presentation.stopwatch.StopwatchIntent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalStdlibApi
class ClockViewModel : ViewModel() {

    private val timeZones = TimeZone.getAvailableIDs().map { TimeZone.getTimeZone(it) }
    private val intentChannel = Channel<StopwatchIntent>()
    val uiState = mutableStateOf(ClockUiState(timeZones = timeZones))
    val myZones = mutableListOf<TimeZone>()

    init {
        getTime()
        getTimes()
    }

    private fun getTime() = viewModelScope.launch {
        uiState.value = uiState.value.copy(
            time = flow {
                while (true) {
                    delay(1)
                    val calendar = Calendar.getInstance()
                    val seconds =
                        (calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND)) * 6 / 1000f + 270
                    val minutes =
                        calendar.get(Calendar.MINUTE) * 6 + 270 + calendar.get(Calendar.SECOND) / 10f
                    val hours =
                        calendar.get(Calendar.HOUR) * 30 + 270 + calendar.get(Calendar.MINUTE) / 2f

                    emit(
                        Time(
                            timeInMillis = calendar.timeInMillis,
                            hours = hours,
                            minutes = minutes,
                            seconds = seconds,
                        )
                    )
                }
            }
        )
    }

    private fun getTimes() = viewModelScope.launch {
        uiState.value = uiState.value.copy(
            times = flow {
                while (true) {
                    delay(10)
                    emit(
                        buildMap<TimeZone, Time> {
                            myZones.forEach {
                                delay(10)
                                val calendar = Calendar.getInstance(it)
                                val seconds =
                                    (calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND)) * 6 / 1000f + 270
                                val minutes =
                                    calendar.get(Calendar.MINUTE) * 6 + 270 + calendar.get(Calendar.SECOND) / 10f
                                val hours =
                                    calendar.get(Calendar.HOUR) * 30 + 270 + calendar.get(Calendar.MINUTE) / 2f
                                val time = Time(
                                    timeInMillis = calendar.timeInMillis,
                                    hours = hours,
                                    minutes = minutes,
                                    seconds = seconds,
                                )
                                putIfAbsent(
                                    it,
                                    time
                                )
                            }
                        }
                    )
                }
            }
        )
    }
}

