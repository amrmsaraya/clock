package com.github.amrmsaraya.clock.feature_timer.domain.usecase

import com.github.amrmsaraya.clock.feature_timer.domain.entity.Timer
import com.github.amrmsaraya.clock.feature_timer.domain.repository.TimerRepo
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
class TimerCRUDUseCaseTest {

    private lateinit var timerRepo: TimerRepo
    private lateinit var timerCRUDUseCase: TimerCRUDUseCase

    @Before
    fun setUp() {
        timerRepo = mockk(relaxed = true)
        timerCRUDUseCase = TimerCRUDUseCase(timerRepo)
    }

    @Test
    fun `insert() with timer then insert() in TimerRepo() should be called`() = runTest {

        // Given
        val timer = Timer()

        // When
        timerCRUDUseCase.insert(timer)

        // Then
        coVerify { timerRepo.insert(timer) }
    }

    @Test
    fun `delete() with timer then delete() in TimerRepo() should be called`() = runTest {

        // Given
        val timer = Timer()

        // When
        timerCRUDUseCase.delete(timer)

        // Then
        coVerify { timerRepo.delete(timer) }
    }

    @Test
    fun `getTimers() with timer then return the correct list of timers`() =
        runTest {

            // Given
            val timer1 = Timer(id = 1)
            val timer2 = Timer(id = 2)
            coEvery { timerRepo.getTimers() } returns flowOf(listOf(timer1, timer2))

            // When
            val result = timerCRUDUseCase.getTimers().first()

            // Then
            Truth.assertThat(result).containsExactly(timer1, timer2)
        }


}