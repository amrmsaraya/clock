package com.github.amrmsaraya.timer

import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class TimerTest {

    private lateinit var timer: Timer

    @Before
    fun setUp() {
        timer = Timer()
    }

    @Test
    fun `start() that start timer then the status is RUNNING`() = runBlockingTest {

        // Given
        launch {
            timer.getTimer().firstOrNull()
        }

        // When
        timer.start()
        val status = timer.status

        // Then
        Truth.assertThat(status).isEqualTo(Timer.RUNNING)
    }

    @Test
    fun `pause() that pause stopwatch then the status is PAUSED`() = runBlockingTest {

        // Given
        launch {
            timer.getTimer().firstOrNull()
            timer.start()
        }

        // When
        timer.pause()
        val status = timer.status

        // Then
        Truth.assertThat(status).isEqualTo(Timer.PAUSED)
    }

    @Test
    fun `reset() that reset stopwatch then the status is IDLE`() = runBlockingTest {

        // Given
        launch {
            timer.getTimer().firstOrNull()
            timer.start()
        }

        // When
        timer.reset()
        val status = timer.status

        // Then
        Truth.assertThat(status).isEqualTo(Timer.IDLE)
    }

    @Test
    fun `getTimer() that get stopwatch time then the stopwatch time is 0 ms`() =
        runBlockingTest {
            // Given
            timer.configure(1000)

            // When
            val time = async { timer.getTimer().firstOrNull() }
            val value = time.await()

            // Then
            Truth.assertThat(value).isEqualTo(Time(timeInMillis = 1000, seconds = 1))
        }
}