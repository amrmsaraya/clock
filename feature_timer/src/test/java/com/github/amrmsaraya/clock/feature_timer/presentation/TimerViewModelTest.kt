package com.github.amrmsaraya.clock.feature_timer.presentation

import com.github.amrmsaraya.clock.feature_timer.domain.entity.Timer
import com.github.amrmsaraya.clock.feature_timer.domain.usecase.TimerCRUDUseCase
import com.github.amrmsaraya.clock.feature_timer.presentation.ui.timer_screen.TimerIntent
import com.github.amrmsaraya.clock.feature_timer.presentation.ui.timer_screen.TimerViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class TimerViewModelTest {

    private lateinit var timerCRUDUseCase: TimerCRUDUseCase
    private lateinit var timerViewModel: TimerViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        timerCRUDUseCase = mockk(relaxed = true)
        timerViewModel = TimerViewModel(
            timerCRUDUseCase = timerCRUDUseCase,
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `sendIntent() with TimerIntent_GetTimers then uiState should contain the valid timers`() =
        runTest {

            // Given
            val timer1 = Timer(id = 1)
            val timer2 = Timer(id = 2)
            val timer3 = Timer(id = 3)

            coEvery { timerCRUDUseCase.getTimers() } returns flowOf(
                listOf(
                    timer1,
                    timer2,
                    timer3
                )
            )

            // When
            timerViewModel.sendIntent(TimerIntent.GetTimers)

            // Then
            assertThat(timerViewModel.uiState.timers).containsExactly(
                timer1,
                timer2,
                timer3
            )
        }

    @Test
    fun `sendIntent() with TimerIntent_InsertTimer() then timerCRUDUseCase_insert() Function should be called`() =
        runTest {

            // Given
            val timer = Timer()

            // When
            timerViewModel.sendIntent(TimerIntent.Insert(timer))

            // Then
            coVerify { timerCRUDUseCase.insert(timer) }
        }

    @Test
    fun `sendIntent() with TimerIntent_DeleteTimer() then timerCRUDUseCase_delete Function should be called`() =
        runTest {

            // Given
            val timer = Timer()

            // When
            timerViewModel.sendIntent(TimerIntent.Delete(timer))

            // Then
            coVerify { timerCRUDUseCase.delete(timer) }
        }
}