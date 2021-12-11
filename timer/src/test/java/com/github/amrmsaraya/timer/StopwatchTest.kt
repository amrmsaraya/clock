package com.github.amrmsaraya.timer

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class StopwatchTest {

    private lateinit var stopwatch: Stopwatch

    @Before
    fun setUp() {
        stopwatch = Stopwatch()
    }

    @Test
    fun `start() that start stopwatch then the status is RUNNING`() = runBlockingTest {

        // Given
        launch {
            stopwatch.getStopwatch().firstOrNull()
        }

        // When
        stopwatch.start()

        val status = stopwatch.status

        // Then
        assertThat(status).isEqualTo(Timer.RUNNING)
    }

    @Test
    fun `pause() that pause stopwatch then the status is PAUSED`() = runBlockingTest {

        // Given
        launch {
            stopwatch.getStopwatch().firstOrNull()
            stopwatch.start()
        }

        // When
        stopwatch.pause()
        val status = stopwatch.status

        // Then
        assertThat(status).isEqualTo(Timer.PAUSED)
    }

    @Test
    fun `reset() that reset stopwatch then the status is IDLE`() = runBlockingTest {

        // Given
        launch {
            stopwatch.getStopwatch().firstOrNull()
            stopwatch.start()
        }

        // When
        stopwatch.reset()
        val status = stopwatch.status

        // Then
        assertThat(status).isEqualTo(Timer.IDLE)
    }

    @Test
    fun `getStopwatch() that get stopwatch time then the stopwatch time is 0 ms`() =
        runBlockingTest {
            // Given
            stopwatch.start()

            // When
            val time = async { stopwatch.getStopwatch().firstOrNull() }
            val value = time.await()

            // Then
            assertThat(value).isEqualTo(Time(timeInMillis = 0))
        }

    @Test
    fun `lap() that add current time to lap list list then the lap list contains it`() =
        runBlockingTest {
            // Given
            var laps = emptyList<Time>()
            val job = launch {
                stopwatch.getLaps().collect { laps = it }
            }
            stopwatch.start()

            // When
            stopwatch.lap()
            stopwatch.lap()
            job.cancelAndJoin()

            // Then
            assertThat(laps.size).isEqualTo(2)
        }
}