package com.github.amrmsaraya.timer

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow

class Stopwatch {
    private val operation = Channel<Operation>()
    private var job: Job? = null

    private var stopwatch = 0L
    private var delay = 10L
    var isRunning = false
        private set

    fun getStopwatch() = flow {
        operation.consumeAsFlow().collect {
            when (it) {
                Operation.START -> {
                    job?.cancel()
                    job = startTimer(delay)
                    isRunning = true
                }
                Operation.PAUSE -> {
                    job?.cancel()
                    isRunning = false
                    emit(stopwatch.toTime())
                }
                Operation.RESET -> {
                    job?.cancel()
                    stopwatch = 0
                    emit(stopwatch.toTime())
                }
                Operation.EMIT -> emit(stopwatch.toTime())
                Operation.CONFIGURE -> Unit
                Operation.FINISH -> Unit
            }
        }
    }

    private fun startTimer(delay: Long) = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
            delay(delay)
            stopwatch += delay
            operation.send(Operation.EMIT)
        }
    }

    suspend fun start() {
        operation.send(Operation.START)
    }

    suspend fun pause() {
        operation.send(Operation.PAUSE)
    }

    suspend fun reset() {
        operation.send(Operation.RESET)
    }

    fun configure(delay: Long) {
        this.delay = delay
    }
}

