package com.github.amrmsaraya.timer

import com.github.amrmsaraya.timer.Stopwatch.Companion.IDLE
import com.github.amrmsaraya.timer.Stopwatch.Companion.PAUSED
import com.github.amrmsaraya.timer.Stopwatch.Companion.RUNNING
import com.github.amrmsaraya.timer.Timer.Companion.IDLE
import com.github.amrmsaraya.timer.Timer.Companion.PAUSED
import com.github.amrmsaraya.timer.Timer.Companion.RUNNING
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow

/**
 *  Timer class is a simple class that provide all timer functionality,
 *  like [start], [pause], [reset] and [configure] the timer.
 *
 *  Also provide the status of the timer either it's [IDLE], [RUNNING] or [PAUSED]
 */
class Timer {

    companion object {
        /** The current status of timer is idle */
        const val IDLE = 0

        /** The current status of timer is running */
        const val RUNNING = 1

        /** The current status of timer is paused */
        const val PAUSED = 2
    }

    /** The channel responsible for sending actions to the [getTimer] flow */
    private val operationChannel = Channel<Operation>(Channel.UNLIMITED)

    /** The job which holds the actual timer coroutine */
    private var job: Job? = null

    /** The current timer value as milliseconds*/
    private var timer = 0L

    /** The configured delay between every [Time] emission*/
    private var delay = 10L

    /** The initial time to start counting down from*/
    var configuredTime = 1L
        private set

    /** The current status of stopwatch */
    var status = IDLE
        private set

    /**
     * Observe changes to timer
     * @param onFinish is lambda function which gets fired when the timer finish counting down
     * @return Flow of timer as [Time]
     */
    fun getTimer(onFinish: () -> Unit = {}) = flow {
        operationChannel.consumeAsFlow().collect {
            when (it) {
                Operation.CONFIGURE -> {
                    timer = configuredTime
                }
                Operation.START -> {
                    job?.cancel()
                    job = startTimer(delay)
                    status = RUNNING
                }
                Operation.PAUSE -> {
                    job?.cancel()
                    status = PAUSED
                }
                Operation.RESET -> {
                    job?.cancel()
                    timer = configuredTime
                    status = IDLE
                }
                Operation.FINISH -> {
                    job?.cancel()
                    onFinish()
                }
                else -> Unit
            }
            emit(timer.toTime())
        }
    }

    /**
     * Starts a job that emit time within a delay of [delay]
     * @param delay the delay which actual timer emit the value
     */
    private fun startTimer(delay: Long) = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
            if (timer > 0) {
                delay(delay)
                timer -= delay
                operationChannel.send(Operation.EMIT)
            } else {
                operationChannel.send(Operation.FINISH)
            }
        }
    }

    /** Start the timer */
    suspend fun start() {
        operationChannel.send(Operation.START)
    }

    /** Pause the timer */
    suspend fun pause() {
        operationChannel.send(Operation.PAUSE)
    }

    /** Reset the timer */
    suspend fun reset() {
        operationChannel.send(Operation.RESET)
    }

    /** Configure the stopwatch
     * @param timeMillis the time to start counting down from in Milliseconds
     * @param delay the delay which timer emit time
     * */
    suspend fun configure(timeMillis: Long, delay: Long = 10) {
        this.delay = delay
        if (timer == 0L) {
            this.configuredTime = timeMillis
        }
        operationChannel.send(Operation.CONFIGURE)
    }
}