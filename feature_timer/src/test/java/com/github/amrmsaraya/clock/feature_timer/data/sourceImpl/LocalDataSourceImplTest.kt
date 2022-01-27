package com.github.amrmsaraya.clock.feature_timer.data.sourceImpl

import com.github.amrmsaraya.clock.database.feature.timer.dao.TimerDao
import com.github.amrmsaraya.clock.database.feature.timer.model.TimerDTO
import com.github.amrmsaraya.clock.feature_timer.data.source.LocalDataSource
import com.google.common.truth.Truth.assertThat
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class LocalDataSourceImplTest {

    private lateinit var timerDao: TimerDao
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setUp() {
        timerDao = mockk(relaxed = true)
        localDataSource = LocalDataSourceImpl(timerDao)
    }

    @Test
    fun `insert() with timer then insert() in TimerDao() should be called`() = runTest {

        // Given
        val timer = TimerDTO()

        // When
        localDataSource.insert(timer)

        // Then
        coVerify { timerDao.insert(timer) }
    }

    @Test
    fun `delete() with timer then delete() in TimerDao() should be called`() = runTest {

        // Given
        val timer = TimerDTO()

        // When
        localDataSource.delete(timer)

        // Then
        coVerify { timerDao.delete(timer) }
    }

    @Test
    fun `getTimers() with timer then return the correct list of timers`() =
        runTest {

            // Given
            val timer1 = TimerDTO(id = 1)
            val timer2 = TimerDTO(id = 2)
            every { timerDao.getTimers() } returns flowOf(listOf(timer1, timer2))

            // When
            val result = localDataSource.getTimers().first()

            // Then
            assertThat(result).containsExactly(timer1, timer2)
        }

}