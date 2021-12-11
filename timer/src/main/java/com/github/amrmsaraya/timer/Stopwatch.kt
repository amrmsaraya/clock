package com.github.amrmsaraya.timer

import com.github.amrmsaraya.timer.Stopwatch.Companion.IDLE
import com.github.amrmsaraya.timer.Stopwatch.Companion.PAUSED
import com.github.amrmsaraya.timer.Stopwatch.Companion.RUNNING
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow

/**
 *  Stopwatch class is a simple class that provide all stopwatch functionality,
 *  like [start], [pause], [reset], [lap] and [configure] the stopwatch.
 *
 *  Also provide the status of the stopwatch either it's [IDLE], [RUNNING] or [PAUSED]
 */
class Stopwatch {
    companion object {
        /** The current status of stopwatch is idle */
        const val IDLE = 0

        /** The current status of stopwatch is running */
        const val RUNNING = 1

        /** The current status of stopwatch is paused */
        const val PAUSED = 2
    }

    /** The channel responsible for sending actions to the [getStopwatch] flow */
    private val operationChannel = Channel<Operation>(Channel.UNLIMITED)

    /** The channel responsible for sending actions to the [getLaps] flow */
    private val lapsChannel = Channel<Operation>(Channel.UNLIMITED)

    /** The job which holds the actual stopwatch coroutine */
    private var job: Job? = null

    /** The current stopwatch value as milliseconds*/
    private var stopwatch = 0L

    /** The configured delay between every [Time] emission*/
    private var delay = 10L

    /** Mutable list that contains laps */
    private val laps = mutableListOf<Pair<Time, Time>>()

    /** The current status of stopwatch */
    var status = IDLE
        private set

    /**
     * Observe changes to stopwatch
     *
     * @return Flow of stopwatch as [Time]
     */
    fun getStopwatch() = flow {
        operationChannel.consumeAsFlow().collect {
            when (it) {
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
                    stopwatch = 0
                    laps.clear()
                    lapsChannel.send(Operation.RESET)
                    status = IDLE
                }
                else -> Unit
            }
            emit(stopwatch.toTime())
        }
    }

    /**
     * Observe changes to laps
     * @return Flow contains list of laps as Pair<[Lap][Time],[Interval][Time]>
     */
    fun getLaps(): Flow<List<Pair<Time, Time>>> = flow {
        lapsChannel.consumeAsFlow().collect {
            when (it) {
                Operation.LAP -> {
                    val interval = if (laps.isEmpty()) {
                        stopwatch.toTime()
                    } else {
                        (stopwatch - laps.last().first.timeInMillis).toTime()
                    }
                    laps.add(Pair(stopwatch.toTime(), interval))
                    emit(laps.toList())
                }
                Operation.RESET -> emit(laps.toList())
                else -> Unit
            }
        }
    }

    /**
     * Starts a job that emit time within a delay of [delay]
     * @param delay the delay which actual stopwatch emit the value
     */
    private fun startTimer(delay: Long) = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
            delay(delay)
            stopwatch += delay
            operationChannel.send(Operation.EMIT)
        }
    }

    /** Start the stopwatch */
    suspend fun start() {
        operationChannel.send(Operation.START)
    }

    /** Pause the stopwatch */
    suspend fun pause() {
        operationChannel.send(Operation.PAUSE)
    }

    /** Reset the stopwatch */
    suspend fun reset() {
        operationChannel.send(Operation.RESET)
    }

    /** Record a lap at the current time */
    suspend fun lap() {
        lapsChannel.send(Operation.LAP)
    }

    /** Configure the stopwatch
     * @param delay the delay which stopwatch emit time
     * */
    fun configure(delay: Long) {
        this.delay = delay
    }
}

