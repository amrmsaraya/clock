package com.github.amrmsaraya.clock.feature_alarm.domain.usecase

import com.github.amrmsaraya.clock.feature_alarm.domain.entity.Alarm
import com.github.amrmsaraya.clock.feature_alarm.domain.repository.AlarmRepo
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AlarmCRUDUseCaseTest {

    private lateinit var alarmRepo: AlarmRepo
    private lateinit var alarmCRUDUseCase: AlarmCRUDUseCase

    @Before
    fun setUp() {
        alarmRepo = mockk(relaxed = true)
        alarmCRUDUseCase = AlarmCRUDUseCase(alarmRepo)
    }

    @Test
    fun `insert() with alarm then insert() in AlarmRepo() should be called`() = runTest {

        // Given
        val alarm = Alarm()

        // When
        alarmCRUDUseCase.insert(alarm)

        // Then
        coVerify { alarmRepo.insert(alarm) }
    }

    @Test
    fun `delete() with alarm then delete() in AlarmRepo() should be called`() = runTest {

        // Given
        val alarm = Alarm()

        // When
        alarmCRUDUseCase.delete(listOf(alarm))

        // Then
        coVerify { alarmRepo.delete(listOf(alarm)) }
    }

    @Test
    fun `getAlarms() with alarm then return the correct list of alarms`() =
        runTest {

            // Given
            val alarm1 = Alarm(id = 1)
            val alarm2 = Alarm(id = 2)
            coEvery { alarmRepo.getAlarms() } returns flowOf(listOf(alarm1, alarm2))

            // When
            val result = alarmCRUDUseCase.getAlarms().first()

            // Then
            Truth.assertThat(result).containsExactly(alarm1, alarm2)
        }


}