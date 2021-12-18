package com.github.amrmsaraya.clock.services.stopwatch

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.github.amrmsaraya.clock.BuildConfig
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.clock.presentation.common_ui.stopwatchTimerFormat
import com.github.amrmsaraya.clock.presentation.main_activity.MainActivity
import com.github.amrmsaraya.clock.presentation.navigation.Screens
import com.github.amrmsaraya.clock.utils.createNotification
import com.github.amrmsaraya.clock.utils.createNotificationChannel
import com.github.amrmsaraya.timer.Stopwatch
import com.github.amrmsaraya.timer.Time
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val NOTIFICATION_ID = 10
private const val NOTIFICATION_CHANNEL_ID = "${BuildConfig.APPLICATION_ID}.STOPWATCH"
const val STOPWATCH_ACTION_ACTION = "${BuildConfig.APPLICATION_ID}.STOPWATCH_ACTION"
const val STOPWATCH_TIME_ACTION = "${BuildConfig.APPLICATION_ID}.STOPWATCH_TIME"
const val STOPWATCH_LAPS_ACTION = "${BuildConfig.APPLICATION_ID}.STOPWATCH_LAPS"

class StopwatchService : Service() {

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    val stopwatch = Stopwatch()
    var isStarted = false

    private val cancelActionReceiver = StopwatchCancelReceiver()
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getStringExtra("action")?.let {
                when (it) {
                    "start" -> scope.launch { stopwatch.start() }
                    "pause" -> scope.launch { stopwatch.pause() }
                    "reset" -> scope.launch {
                        stopwatch.reset()
                        delay(100)
                        stopForeground(true)
                        stopSelf()
                    }
                    "lap" -> scope.launch { stopwatch.lap() }
                    else -> Unit
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel(
            id = NOTIFICATION_CHANNEL_ID,
            name = getString(R.string.stopwatch),
            importance = NotificationManager.IMPORTANCE_DEFAULT
        )

        startForeground(NOTIFICATION_ID, createNotification())

        IntentFilter().also { intent ->
            intent.addAction(STOPWATCH_ACTION_ACTION)
            registerReceiver(broadcastReceiver, intent)
        }


        IntentFilter().also { intent ->
            intent.addAction(STOPWATCH_ACTION_ACTION)
            registerReceiver(cancelActionReceiver, intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isStarted) {
            isStarted = true
            scope.launch { stopwatch.start() }
            scope.launch { collectStopwatch() }
            scope.launch { collectLaps() }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        unregisterReceiver(cancelActionReceiver)
        scope.cancel()
        stopwatch.clear()
    }

    private suspend fun collectLaps() {
        stopwatch.getLaps().collect { laps ->
            val list = Json.encodeToString(
                laps.map { Pair(it.first.timeInMillis, it.second.timeInMillis) }
            )
            Intent().also { intent ->
                intent.action = STOPWATCH_LAPS_ACTION
                intent.putExtra("laps", list)
                sendBroadcast(intent)
            }
        }
    }

    private suspend fun collectStopwatch() {
        var job: Job? = null
        var notificationTime: Time
        stopwatch.getStopwatch().collect { time ->
            notificationTime = time

            if (job == null || job?.isCompleted == true) {
                job = scope.launch(Dispatchers.Main) {
                    updateNotification(notificationTime)
                    delay(1000)
                }
            }

            Intent().also { intent ->
                intent.action = STOPWATCH_TIME_ACTION
                intent.putExtra("time", time.timeInMillis)
                intent.putExtra("status", stopwatch.status)
                sendBroadcast(intent)
            }
        }
    }

    private fun updateNotification(time: Time) {
        NotificationManagerCompat.from(this@StopwatchService).notify(
            NOTIFICATION_ID,
            createNotification(
                stopwatchTimerFormat(time = time, withMillis = false).text
            )
        )
    }

    private fun createNotification(content: String = ""): Notification {
        val cancelPendingIntent =
            Intent(this, StopwatchCancelReceiver::class.java).let { intent ->
                intent.action = STOPWATCH_ACTION_ACTION
                intent.putExtra("action", "cancel")

                PendingIntent.getBroadcast(
                    this,
                    1,
                    intent,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_MUTABLE
                    } else {
                        PendingIntent.FLAG_UPDATE_CURRENT
                    }
                )
            }
        val pendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                notificationIntent.putExtra("route", Screens.Stopwatch.route)
                PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_MUTABLE
                    } else {
                        PendingIntent.FLAG_UPDATE_CURRENT
                    }
                )
            }

        return createNotification(
            channelId = NOTIFICATION_CHANNEL_ID,
            title = getString(R.string.stopwatch),
            content = content,
            icon = R.drawable.ic_norification_logo,
            isSilent = true,
            isOnGoing = true,
            intent = pendingIntent,
            actionTitle = getString(R.string.cancel),
            actionIntent = cancelPendingIntent
        )
    }
}