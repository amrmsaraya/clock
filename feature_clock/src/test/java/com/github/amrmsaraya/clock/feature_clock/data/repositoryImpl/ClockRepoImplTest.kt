package com.github.amrmsaraya.clock.feature_clock.data.repositoryImpl

import com.github.amrmsaraya.clock.database.feature.clock.model.ClockDTO
import com.github.amrmsaraya.clock.feature_clock.data.source.LocalDataSource
import com.github.amrmsaraya.clock.feature_clock.domain.repository.ClockRepo
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
class ClockRepoImplTest {

    private lateinit var localDataSource: LocalDataSource
    private lateinit var clockRepo: ClockRepo

    private val standardTestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        localDataSource = mockk(relaxed = true)
        clockRepo = ClockRepoImpl(
            localDataSource = localDataSource,
            dispatcher = standardTestDispatcher
        )
    }

    @Test
    fun `insert() with clock then insert() in LocalDataSource() should be called`() = runTest {

        // Given
        val clock = ClockDTO()

        // When
        localDataSource.insert(clock)

        // Then
        coVerify { localDataSource.insert(clock) }
    }

    @Test
    fun `delete() with clock then delete() in LocalDataSource() should be called`() = runTest {

        // Given
        val clock = ClockDTO()

        // When
        localDataSource.delete(listOf(clock))

        // Then
        coVerify { localDataSource.delete(listOf(clock)) }
    }

    @Test
    fun `getClocks() with clock then return the correct list of clocks`() =
        runTest {

            // Given
            val clock1 = ClockDTO(id = "1")
            val clock2 = ClockDTO(id = "2")
            every { localDataSource.getClocks() } returns flowOf(listOf(clock1, clock2))

            // When
            val result = localDataSource.getClocks().first()

            // Then
            assertThat(result).containsExactly(clock1, clock2)
        }

}