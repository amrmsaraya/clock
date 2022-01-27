package com.github.amrmsaraya.clock.feature_clock.presentation

import com.github.amrmsaraya.clock.feature_clock.domain.entity.Clock
import com.github.amrmsaraya.clock.feature_clock.domain.usecase.ClockCRUDUseCase
import com.github.amrmsaraya.clock.feature_clock.presentation.clock_screen.ClockIntent
import com.github.amrmsaraya.clock.feature_clock.presentation.clock_screen.ClockViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.*


@ExperimentalCoroutinesApi
class ClockViewModelTest {

    private lateinit var clockCRUDUseCase: ClockCRUDUseCase
    private lateinit var clockViewModel: ClockViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        clockCRUDUseCase = mockk(relaxed = true)
        clockViewModel = ClockViewModel(
            clockCRUDUseCase = clockCRUDUseCase,
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `sendIntent() with ClockIntent_GetClocks then uiState should contain the valid clocks`() =
        runTest {

            // Given
            val clock = Clock(id = TimeZone.getDefault().id)

            coEvery { clockCRUDUseCase.getClocks() } returns flowOf(
                listOf(clock)
            )

            // When
            clockViewModel.sendIntent(ClockIntent.GetClocks)

            // Then
            assertThat(clockViewModel.uiState.worldClocks.first().keys).isEqualTo(
                setOf(TimeZone.getDefault())
            )
        }

    @Test
    fun `sendIntent() with ClockIntent_InsertClock() then clockCRUDUseCase_insert() Function should be called`() =
        runTest {

            // Given
            val timeZone = TimeZone.getDefault()
            val clock = Clock(
                id = timeZone.id,
                displayName = timeZone.getDisplayName(false, TimeZone.SHORT)
            )

            // When
            clockViewModel.sendIntent(ClockIntent.InsertClock(timeZone))

            // Then
            coVerify { clockCRUDUseCase.insert(clock) }
        }

    @Test
    fun `sendIntent() with ClockIntent_DeleteClock() then clockCRUDUseCase_delete Function should be called`() =
        runTest {

            // Given
            val timeZone = TimeZone.getDefault()
            val clock = Clock(
                id = timeZone.id,
                displayName = timeZone.getDisplayName(false, TimeZone.SHORT)
            )

            // When
            clockViewModel.sendIntent(ClockIntent.DeleteClocks(listOf(timeZone)))

            // Then
            coVerify { clockCRUDUseCase.delete(listOf(clock)) }
        }
}