package com.github.amrmsaraya.clock.services.timer

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.Ringtone
import android.media.RingtoneManager
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
import com.github.amrmsaraya.timer.Time
import com.github.amrmsaraya.timer.Timer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

private const val NOTIFICATION_ID = 11
private const val NOTIFICATION_CHANNEL_ID = "${BuildConfig.APPLICATION_ID}.TIMER"
const val TIMER_ACTION_ACTION = "${BuildConfig.APPLICATION_ID}.TIMER_ACTION"
const val TIMER_TIME_ACTION = "${BuildConfig.APPLICATION_ID}.TIMER_TIME"

class TimerService : Service() {

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private lateinit var notificationSound: Ringtone

    private val timer = Timer()
    private var isStarted = false

    private val cancelTimerActionReceiver = TimerCancelReceiver()

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getStringExtra("action")?.let {
                when (it) {
                    "start" -> scope.launch { timer.start() }
                    "pause" -> scope.launch { timer.pause() }
                    "reset" -> scope.launch {
                        timer.reset()
                        delay(100)
                        stopForeground(true)
                        stopSelf()
                    }
                    else -> Unit
                }
            }

            intent?.getLongExtra("configuredTime", 0L)?.let { configuredTime ->
                if (configuredTime > 0) {
                    println("CONFIGURE $configuredTime")
                    timer.configure(configuredTime)
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        notificationSound = RingtoneManager.getRingtone(
            this,
            RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION)
        )

        createNotificationChannel(
            id = NOTIFICATION_CHANNEL_ID,
            name = getString(R.string.timer),
            importance = NotificationManager.IMPORTANCE_DEFAULT
        )

        startForeground(NOTIFICATION_ID, createNotification())

        IntentFilter().also {
            it.addAction(TIMER_ACTION_ACTION)
            registerReceiver(broadcastReceiver, it)
        }

        IntentFilter().also {
            it.addAction(TIMER_ACTION_ACTION)
            registerReceiver(cancelTimerActionReceiver, it)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isStarted) {
            isStarted = true
            intent?.getLongExtra("configuredTime", 0L)?.let { configuredTime ->
                if (configuredTime > 0) {
                    scope.launch { timer.configure(configuredTime) }
                }
            }
            scope.launch { timer.start() }
            scope.launch { collectTimer() }
        }
        return super.onStartCommand(intent, flags, startId)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        unregisterReceiver(cancelTimerActionReceiver)
        scope.cancel()
        timer.clear()
        println("onDestroy")
    }

    private suspend fun collectTimer() {
        var job: Job? = null
        var notificationTime: Time

        timer.getTimer {
            scope.launch(Dispatchers.Main) {
                timer.reset()
                notificationSound.play()
                stopSelf()
            }
        }.collect { time ->

            notificationTime = time

            if (job == null) {
                job = scope.launch(Dispatchers.Main) {
                    NotificationManagerCompat.from(this@TimerService).notify(
                        NOTIFICATION_ID,
                        createNotification(
                            stopwatchTimerFormat(time = time, withMillis = false).text
                        )
                    )
                    delay(1000)
                    job = null
                }
            }

            Intent().also { intent ->
                intent.action = TIMER_TIME_ACTION
                intent.putExtra("time", time.timeInMillis)
                intent.putExtra("status", timer.status)
                sendBroadcast(intent)
            }
        }
    }


    private fun createNotification(content: String = ""): Notification {
        val cancelPendingIntent =
            Intent(this, TimerCancelReceiver::class.java).let { intent ->
                intent.action = TIMER_ACTION_ACTION
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
                notificationIntent.putExtra("route", Screens.Timer.route)
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
            title = getString(R.string.timer),
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