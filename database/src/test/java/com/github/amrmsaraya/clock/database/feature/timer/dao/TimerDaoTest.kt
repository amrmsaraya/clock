package com.github.amrmsaraya.clock.database.feature.timer.dao

import androidx.room.Room
import com.github.amrmsaraya.clock.database.database.AppDatabase
import com.github.amrmsaraya.clock.database.feature.timer.model.TimerDTO
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
class TimerDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var timerDao: TimerDao

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication().applicationContext,
            AppDatabase::class.java
        ).build()
        timerDao = database.timerDao()
    }

    @After
    fun closeDB() = database.close()

    @Test
    fun insertTimer_thenItShouldBeInserted() = runTest {

        // Given
        val timer = TimerDTO(id = 1, title = "timer")

        // When
        timerDao.insert(timer)
        val result = timerDao.getTimers().first()

        // Then
        assertThat(result).contains(timer)
    }

    @Test
    fun deleteTimer_thenItShouldBeDeleted() = runTest {

        // Given
        val timer1 = TimerDTO(id = 1, title = "timer1")
        val timer2 = TimerDTO(id = 2, title = "timer2")
        timerDao.insert(timer1)
        timerDao.insert(timer2)

        // When
        timerDao.delete(timer1)
        val result = timerDao.getTimers().first()

        // Then
        assertThat(result).doesNotContain(timer1)
    }

    @Test
    fun getTimers_thenWeShouldHaveAListOfExistingTimers() = runTest {

        // Given
        val timer1 = TimerDTO(id = 1, title = "timer1")
        val timer2 = TimerDTO(id = 2, title = "timer2")
        timerDao.insert(timer1)
        timerDao.insert(timer2)

        // When
        val result = timerDao.getTimers().first()

        // Then
        assertThat(result).containsExactly(timer1, timer2)
    }

    @Test
    fun getTimers_withEmptyTable_thenWeShouldHaveAnEmptyList() = runTest {

        // When
        val result = timerDao.getTimers().first()

        // Then
        assertThat(result).isEmpty()
    }
}