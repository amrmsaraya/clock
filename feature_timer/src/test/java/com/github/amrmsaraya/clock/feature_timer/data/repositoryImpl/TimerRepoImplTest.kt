package com.github.amrmsaraya.clock.feature_timer.data.repositoryImpl

import com.github.amrmsaraya.clock.database.feature.timer.model.TimerDTO
import com.github.amrmsaraya.clock.feature_timer.data.source.LocalDataSource
import com.github.amrmsaraya.clock.feature_timer.domain.repository.TimerRepo
import com.google.common.truth.Truth.assertThat
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class TimerRepoImplTest {

    private lateinit var localDataSource: LocalDataSource
    private lateinit var timerRepo: TimerRepo

    private val standardTestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        localDataSource = mockk(relaxed = true)
        timerRepo = TimerRepoImpl(
            localDataSource = localDataSource,
            dispatcher = standardTestDispatcher
        )
    }

    @Test
    fun `insert() with timer then insert() in LocalDataSource() should be called`() = runTest {

        // Given
        val timer = TimerDTO()

        // When
        localDataSource.insert(timer)

        // Then
        coVerify { localDataSource.insert(timer) }
    }

    @Test
    fun `delete() with timer then delete() in LocalDataSource() should be called`() = runTest {

        // Given
        val timer = TimerDTO()

        // When
        localDataSource.delete(timer)

        // Then
        coVerify { localDataSource.delete(timer) }
    }

    @Test
    fun `getTimers() with timer then return the correct list of timers`() =
        runTest {

            // Given
            val timer1 = TimerDTO(id = 1)
            val timer2 = TimerDTO(id = 2)
            every { localDataSource.getTimers() } returns flowOf(listOf(timer1, timer2))

            // When
            val result = localDataSource.getTimers().first()

            // Then
            assertThat(result).containsExactly(timer1, timer2)
        }

}