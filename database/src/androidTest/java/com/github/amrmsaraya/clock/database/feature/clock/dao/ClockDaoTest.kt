package com.github.amrmsaraya.clock.database.feature.clock.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.amrmsaraya.clock.database.database.AppDatabase
import com.github.amrmsaraya.clock.database.feature.clock.model.ClockDTO
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ClockDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var clockDao: ClockDao

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        clockDao = database.clockDao()
    }

    @After
    fun closeDB() = database.close()


    @Test
    fun insertClock_thenItShouldBeInserted() = runTest {

        // Given
        val clock = ClockDTO(id = "1")

        // When
        clockDao.insert(clock)
        val result = clockDao.getClocks().first()

        // Then
        assertThat(result).contains(clock)
    }

    @Test
    fun deleteClock_thenItShouldBeDeleted() = runTest {

        // Given
        val clock1 = ClockDTO(id = "1")
        val clock2 = ClockDTO(id = "2")
        clockDao.insert(clock1)
        clockDao.insert(clock2)

        // When
        clockDao.delete(listOf(clock1))
        val result = clockDao.getClocks().first()

        // Then
        assertThat(result).doesNotContain(clock1)
    }

    @Test
    fun getClocks_thenWeShouldHaveAListOfExistingClocks() = runTest {

        // Given
        val clock1 = ClockDTO(id = "1")
        val clock2 = ClockDTO(id = "2")
        clockDao.insert(clock1)
        clockDao.insert(clock2)

        // When
        val result = clockDao.getClocks().first()

        // Then
        assertThat(result).containsExactly(clock1, clock2)
    }

    @Test
    fun getClocks_withEmptyTable_thenWeShouldHaveAnEmptyList() = runTest {

        // When
        val result = clockDao.getClocks().first()

        // Then
        assertThat(result).isEmpty()
    }
}