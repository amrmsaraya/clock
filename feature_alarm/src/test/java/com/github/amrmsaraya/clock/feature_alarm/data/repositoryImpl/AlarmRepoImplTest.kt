package com.github.amrmsaraya.clock.feature_alarm.data.repositoryImpl

import com.github.amrmsaraya.clock.database.feature.alarm.model.AlarmDTO
import com.github.amrmsaraya.clock.feature_alarm.data.repoImpl.AlarmRepoImpl
import com.github.amrmsaraya.clock.feature_alarm.data.source.LocalDataSource
import com.github.amrmsaraya.clock.feature_alarm.domain.repository.AlarmRepo
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
class AlarmRepoImplTest {

    private lateinit var localDataSource: LocalDataSource
    private lateinit var alarmRepo: AlarmRepo

    private val standardTestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        localDataSource = mockk(relaxed = true)
        alarmRepo = AlarmRepoImpl(
            localDataSource = localDataSource,
            dispatcher = standardTestDispatcher
        )
    }

    @Test
    fun `insert() with alarm then insert() in LocalDataSource() should be called`() = runTest {

        // Given
        val alarm = AlarmDTO()

        // When
        localDataSource.insert(alarm)

        // Then
        coVerify { localDataSource.insert(alarm) }
    }

    @Test
    fun `delete() with alarm then delete() in LocalDataSource() should be called`() = runTest {

        // Given
        val alarm = AlarmDTO()

        // When
        localDataSource.delete(listOf(alarm))

        // Then
        coVerify { localDataSource.delete(listOf(alarm)) }
    }

    @Test
    fun `getAlarms() with alarm then return the correct list of alarms`() =
        runTest {

            // Given
            val alarm1 = AlarmDTO(id = 1)
            val alarm2 = AlarmDTO(id = 2)
            every { localDataSource.getAlarms() } returns flowOf(listOf(alarm1, alarm2))

            // When
            val result = localDataSource.getAlarms().first()

            // Then
            assertThat(result).containsExactly(alarm1, alarm2)
        }

}