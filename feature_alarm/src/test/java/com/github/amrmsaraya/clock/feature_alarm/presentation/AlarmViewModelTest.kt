package com.github.amrmsaraya.clock.feature_alarm.presentation

import com.github.amrmsaraya.clock.feature_alarm.domain.entity.Alarm
import com.github.amrmsaraya.clock.feature_alarm.domain.usecase.AlarmCRUDUseCase
import com.github.amrmsaraya.clock.feature_alarm.presentation.ui.alarm_screen.AlarmIntent
import com.github.amrmsaraya.clock.feature_alarm.presentation.ui.alarm_screen.AlarmViewModel
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
class AlarmViewModelTest {

    private lateinit var alarmCRUDUseCase: AlarmCRUDUseCase
    private lateinit var alarmViewModel: AlarmViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        alarmCRUDUseCase = mockk(relaxed = true)
        alarmViewModel = AlarmViewModel(
            alarmCRUDUseCase = alarmCRUDUseCase,
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `sendIntent() with AlarmIntent_GetAlarms then uiState should contain the valid alarms`() =
        runTest {

            // Given
            val alarm1 = Alarm(id = 1)
            val alarm2 = Alarm(id = 2)
            val alarm3 = Alarm(id = 3)

            coEvery { alarmCRUDUseCase.getAlarms() } returns flowOf(
                listOf(
                    alarm1,
                    alarm2,
                    alarm3
                )
            )

            // When
            alarmViewModel.sendIntent(AlarmIntent.GetAlarms)

            // Then
            assertThat(alarmViewModel.uiState.alarms).containsExactly(
                alarm1,
                alarm2,
                alarm3
            )
        }

    @Test
    fun `sendIntent() with AlarmIntent_InsertAlarm() then alarmCRUDUseCase_insert() Function should be called`() =
        runTest {

            // Given
            val alarm = Alarm()

            // When
            alarmViewModel.sendIntent(AlarmIntent.InsertAlarm(alarm))

            // Then
            coVerify { alarmCRUDUseCase.insert(alarm) }
        }

    @Test
    fun `sendIntent() with AlarmIntent_DeleteAlarm() then alarmCRUDUseCase_delete Function should be called`() =
        runTest {

            // Given
            val alarm = Alarm()

            // When
            alarmViewModel.sendIntent(AlarmIntent.DeleteAlarms(listOf(alarm)))

            // Then
            coVerify { alarmCRUDUseCase.delete(listOf(alarm)) }
        }
}