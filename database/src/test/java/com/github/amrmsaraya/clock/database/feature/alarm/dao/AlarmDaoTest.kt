package com.github.amrmsaraya.clock.database.feature.alarm.dao

import androidx.room.Room
import com.github.amrmsaraya.clock.database.database.AppDatabase
import com.github.amrmsaraya.clock.database.feature.alarm.model.AlarmDTO
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class AlarmDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var alarmDao: AlarmDao

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication().applicationContext,
            AppDatabase::class.java
        ).build()
        alarmDao = database.alarmDao()
    }

    @After
    fun closeDB() = database.close()

    @Test
    fun insertAlarm_thenItShouldBeInserted() = runTest {

        // Given
        val alarm = AlarmDTO(id = 1, title = "alarm")

        // When
        alarmDao.insert(alarm)
        val result = alarmDao.getAlarms().first()

        // Then
        assertThat(result).contains(alarm)
    }

    @Test
    fun deleteAlarm_thenItShouldBeDeleted() = runTest {

        // Given
        val alarm1 = AlarmDTO(id = 1, title = "alarm1")
        val alarm2 = AlarmDTO(id = 2, title = "alarm2")
        alarmDao.insert(alarm1)
        alarmDao.insert(alarm2)

        // When
        alarmDao.delete(listOf(alarm1))
        val result = alarmDao.getAlarms().first()

        // Then
        assertThat(result).doesNotContain(alarm1)
    }

    @Test
    fun getAlarms_thenWeShouldHaveAListOfExistingAlarms() = runTest {

        // Given
        val alarm1 = AlarmDTO(id = 1, title = "alarm1")
        val alarm2 = AlarmDTO(id = 2, title = "alarm2")
        alarmDao.insert(alarm1)
        alarmDao.insert(alarm2)

        // When
        val result = alarmDao.getAlarms().first()

        // Then
        assertThat(result).containsExactly(alarm1, alarm2)
    }

    @Test
    fun getAlarms_withEmptyTable_thenWeShouldHaveAnEmptyList() = runTest {

        // When
        val result = alarmDao.getAlarms().first()

        // Then
        assertThat(result).isEmpty()
    }
}