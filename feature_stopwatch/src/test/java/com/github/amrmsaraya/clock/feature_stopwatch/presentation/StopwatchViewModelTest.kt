package com.github.amrmsaraya.clock.feature_stopwatch.presentation

import com.github.amrmsaraya.clock.feature_stopwatch.presentation.ui.stopwatch_screen.StopwatchUiState
import com.github.amrmsaraya.clock.feature_stopwatch.presentation.ui.stopwatch_screen.StopwatchViewModel
import com.github.amrmsaraya.timer.Time
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class StopwatchViewModelTest {

    private lateinit var stopwatchViewModel: StopwatchViewModel

    @Before
    fun setUp() {
        stopwatchViewModel = StopwatchViewModel()
    }


    @Test
    fun `updateStopwatch() with time and status then uiState should be updated`() =
        runTest {

            // Given
            val time = Time(timeInMillis = 1500)
            val status = 15
            // When
            stopwatchViewModel.updateStopwatch(
                stopwatch = time,
                status = status
            )

            // Then
            assertThat(stopwatchViewModel.uiState).isEqualTo(
                StopwatchUiState(
                    stopwatch = time,
                    status = status
                )
            )
        }

    @Test
    fun `updateLaps() with a list of time and elapsed time then uiState should be updated`() =
        runTest {

            // Given
            val pair1 = Pair(Time(timeInMillis = 1000), Time(timeInMillis = 1000))
            val pair2 = Pair(Time(timeInMillis = 2000), Time(timeInMillis = 2000))
            val pair3 = Pair(Time(timeInMillis = 3000), Time(timeInMillis = 3000))

            // When
            stopwatchViewModel.updateLaps(
                listOf(pair1, pair2, pair3)
            )

            // Then
            assertThat(stopwatchViewModel.uiState).isEqualTo(
                StopwatchUiState(laps = listOf(pair1, pair2, pair3))
            )
        }
}