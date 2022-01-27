package com.github.amrmsaraya.clock.feature_clock.domain.usecase

import com.github.amrmsaraya.clock.feature_clock.domain.entity.Clock
import com.github.amrmsaraya.clock.feature_clock.domain.repository.ClockRepo
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
class ClockCRUDUseCaseTest {

    private lateinit var clockRepo: ClockRepo
    private lateinit var clockCRUDUseCase: ClockCRUDUseCase

    @Before
    fun setUp() {
        clockRepo = mockk(relaxed = true)
        clockCRUDUseCase = ClockCRUDUseCase(clockRepo)
    }

    @Test
    fun `insert() with clock then insert() in ClockRepo() should be called`() = runTest {

        // Given
        val clock = Clock()

        // When
        clockCRUDUseCase.insert(clock)

        // Then
        coVerify { clockRepo.insert(clock) }
    }

    @Test
    fun `delete() with clock then delete() in ClockRepo() should be called`() = runTest {

        // Given
        val clock = Clock()

        // When
        clockCRUDUseCase.delete(listOf(clock))

        // Then
        coVerify { clockRepo.delete(listOf(clock)) }
    }

    @Test
    fun `getClocks() with clock then return the correct list of clocks`() =
        runTest {

            // Given
            val clock1 = Clock(id = "1")
            val clock2 = Clock(id = "2")
            coEvery { clockRepo.getClocks() } returns flowOf(listOf(clock1, clock2))

            // When
            val result = clockCRUDUseCase.getClocks().first()

            // Then
            Truth.assertThat(result).containsExactly(clock1, clock2)
        }


}