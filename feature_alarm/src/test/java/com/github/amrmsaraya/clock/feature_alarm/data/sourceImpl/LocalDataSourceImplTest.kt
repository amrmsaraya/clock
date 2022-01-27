package com.github.amrmsaraya.clock.feature_alarm.data.sourceImpl

import com.github.amrmsaraya.clock.database.feature.alarm.dao.AlarmDao
import com.github.amrmsaraya.clock.database.feature.alarm.model.AlarmDTO
import com.github.amrmsaraya.clock.feature_alarm.data.source.LocalDataSource
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

    private lateinit var alarmDao: AlarmDao
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setUp() {
        alarmDao = mockk(relaxed = true)
        localDataSource = LocalDataSourceImpl(alarmDao)
    }

    @Test
    fun `insert() with alarm then insert() in AlarmDao() should be called`() = runTest {

        // Given
        val alarm = AlarmDTO()

        // When
        localDataSource.insert(alarm)

        // Then
        coVerify { alarmDao.insert(alarm) }
    }

    @Test
    fun `delete() with alarm then delete() in AlarmDao() should be called`() = runTest {

        // Given
        val alarm = AlarmDTO()

        // When
        localDataSource.delete(listOf(alarm))

        // Then
        coVerify { alarmDao.delete(listOf(alarm)) }
    }

    @Test
    fun `getAlarms() with alarm then return the correct list of alarms`() =
        runTest {

            // Given
            val alarm1 = AlarmDTO(id = 1)
            val alarm2 = AlarmDTO(id = 2)
            every { alarmDao.getAlarms() } returns flowOf(listOf(alarm1, alarm2))

            // When
            val result = localDataSource.getAlarms().first()

            // Then
            assertThat(result).containsExactly(alarm1, alarm2)
        }

}