package com.github.amrmsaraya.clock.feature_clock.data.sourceImpl

import com.github.amrmsaraya.clock.database.feature.clock.dao.ClockDao
import com.github.amrmsaraya.clock.database.feature.clock.model.ClockDTO
import com.github.amrmsaraya.clock.feature_clock.data.source.LocalDataSource
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

    private lateinit var clockDao: ClockDao
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setUp() {
        clockDao = mockk(relaxed = true)
        localDataSource = LocalDataSourceImpl(clockDao)
    }

    @Test
    fun `insert() with clock then insert() in ClockDao() should be called`() = runTest {

        // Given
        val clock = ClockDTO()

        // When
        localDataSource.insert(clock)

        // Then
        coVerify { clockDao.insert(clock) }
    }

    @Test
    fun `delete() with clock then delete() in ClockDao() should be called`() = runTest {

        // Given
        val clock = ClockDTO()

        // When
        localDataSource.delete(listOf(clock))

        // Then
        coVerify { clockDao.delete(listOf(clock)) }
    }

    @Test
    fun `getClocks() with clock then return the correct list of clocks`() =
        runTest {

            // Given
            val clock1 = ClockDTO(id = "1")
            val clock2 = ClockDTO(id = "2")
            every { clockDao.getClocks() } returns flowOf(listOf(clock1, clock2))

            // When
            val result = localDataSource.getClocks().first()

            // Then
            assertThat(result).containsExactly(clock1, clock2)
        }

}