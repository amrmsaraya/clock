package com.github.amrmsaraya.timer

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow

class Timer {
    private val operation = Channel<Operation>()
    private var job: Job? = null

    private var timer = 0L
    private var delay = 10L

    var isRunning = false
        private set
    var configuredTime = 1L
        private set

    fun getTimer(onFinish: () -> Unit) = flow {
        operation.consumeAsFlow().collect {
            when (it) {
                Operation.CONFIGURE -> {
                    timer = configuredTime
                    emit(configuredTime.toTime())
                }
                Operation.START -> {
                    job?.cancel()
                    job = startStopwatch(delay)
                    isRunning = true
                }
                Operation.PAUSE -> {
                    job?.cancel()
                    isRunning = false
                    emit(timer.toTime())
                }
                Operation.RESET -> {
                    job?.cancel()
                    timer = configuredTime
                    isRunning = false
                    emit(configuredTime.toTime())
                }
                Operation.FINISH -> {
                    job?.cancel()
                    onFinish()
                }
                Operation.EMIT -> emit(timer.toTime())
            }
        }
    }

    private fun startStopwatch(delay: Long) = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
            if (timer > 0) {
                delay(delay)
                timer -= delay
                operation.send(Operation.EMIT)
            } else {
                operation.send(Operation.FINISH)
            }
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

    suspend fun configure(timeMillis: Long, delay: Long = 10) {
        this.delay = delay
        if (timer == 0L) {
            this.configuredTime = timeMillis
        }
        operation.send(Operation.CONFIGURE)
    }
}