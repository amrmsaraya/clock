package com.github.amrmsaraya.clock.presentation.alarm_activity

import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.clock.domain.entity.Alarm
import com.github.amrmsaraya.clock.presentation.alarm.utils.Colors
import com.github.amrmsaraya.clock.presentation.theme.ClockTheme
import com.github.amrmsaraya.clock.presentation.theme.md_theme_dark_onPrimary
import com.github.amrmsaraya.clock.presentation.theme.md_theme_dark_primary
import com.github.amrmsaraya.clock.services.alarm.ALARM_NOTIFICATION_ID
import com.github.amrmsaraya.clock.utils.setAlarm
import com.github.amrmsaraya.clock.utils.turnScreenOffAndKeyguardOn
import com.github.amrmsaraya.clock.utils.turnScreenOnAndKeyguardOff
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {

    private lateinit var ringtone: Ringtone

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        turnScreenOnAndKeyguardOff()

        val viewModel by viewModels<AlarmActivityViewModel>()
        val calendar = Calendar.getInstance()
        val alarm = Alarm(
            id = intent.getIntExtra("id", 0).toLong(),
            title = intent.getStringExtra("title") ?: "",
            hour = intent.getIntExtra("hour", 0),
            minute = intent.getIntExtra("minute", 0),
            amPm = intent.getIntExtra("amPm", 0),
            color = intent.getIntExtra("color", 0),
            repeatOn = intent.getIntArrayExtra("repeatOn")?.toList() ?: intArrayOf().toList(),
            ringtone = intent.getStringExtra("ringtone") ?: "",
        )

        val ringtoneUri =
            intent.getStringExtra("ringtone")?.toUri() ?: RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_ALARM
            )

        ringtone = RingtoneManager.getRingtone(this, ringtoneUri).apply {
            audioAttributes =
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                isLooping = true
            }
            play()
        }

        setContent {
            val scope = rememberCoroutineScope()
            App(
                title = alarm.title,
                hour = calendar.get(Calendar.HOUR),
                minute = calendar.get(Calendar.MINUTE),
                color = alarm.color,
                onSnooze = {
                    scope.launch {
                        val ringTime = setAlarm(alarm = alarm, snooze = true)
                        viewModel.insertAlarm(alarm.copy(ringTime = ringTime, enabled = true))
                        finish()
                    }
                },
                onStop = { finishAndRemoveTask() }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Remove notification
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ALARM_NOTIFICATION_ID)

        ringtone.stop()
        turnScreenOffAndKeyguardOn()
    }
}

@Composable
fun App(
    title: String,
    hour: Int,
    minute: Int,
    color: Int,
    onSnooze: () -> Unit,
    onStop: () -> Unit
) {
    val backgroundColor by remember {
        mutableStateOf(
            Colors.values().firstOrNull { it.ordinal == color }?.activeBackgroundColor
                ?: md_theme_dark_primary
        )
    }

    val contentColor by remember {
        mutableStateOf(
            Colors.values().firstOrNull { it.ordinal == color }?.contentColor
                ?: md_theme_dark_onPrimary
        )
    }

    ClockTheme(systemBarsColor = backgroundColor) {
        Surface(color = backgroundColor) {
            AlarmContent(
                title = title,
                hour = hour,
                minute = minute,
                contentColor = contentColor,
                onSnooze = onSnooze,
                onStop = onStop
            )
        }
    }
}


@Composable
private fun AlarmContent(
    title: String,
    hour: Int,
    minute: Int,
    contentColor: Color,
    onSnooze: () -> Unit,
    onStop: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        var size by remember { mutableStateOf(maxWidth * 0.6f) }
        val animatedSize by animateDpAsState(
            targetValue = size,
            animationSpec = tween(3000)
        )

        LaunchedEffect(key1 = animatedSize) {
            if (animatedSize == maxWidth * 0.6f) {
                size = maxWidth * 0.8f
            } else if (animatedSize == maxWidth * 0.8f) {
                size = maxWidth * 0.6f
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TimeAndDate(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxSize(),
                hour = hour,
                minute = minute,
                contentColor = contentColor
            )

            TitleAndSnooze(
                modifier = Modifier.weight(0.5f),
                title = title,
                contentColor = contentColor,
                animatedSize = animatedSize,
                maxWidth = this@BoxWithConstraints.maxWidth * 0.7f,
                onSnooze = onSnooze
            )

            StopButton(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxSize(),
                contentColor = contentColor,
                onStop = onStop
            )
        }
    }
}

@Composable
private fun TimeAndDate(
    modifier: Modifier = Modifier,
    hour: Int,
    minute: Int,
    contentColor: Color
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = hour.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = contentColor,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = ":",
                style = MaterialTheme.typography.displayLarge,
                color = contentColor,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "%02d".format(minute),
                style = MaterialTheme.typography.displayLarge,
                color = contentColor,
            )
        }
        Text(
            text = SimpleDateFormat(
                "E, MMM dd",
                Locale.getDefault()
            ).format(System.currentTimeMillis()),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TitleAndSnooze(
    modifier: Modifier = Modifier,
    title: String,
    contentColor: Color,
    animatedSize: Dp,
    maxWidth: Dp,
    onSnooze: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(animatedSize)
                .background(contentColor.copy(alpha = 0.075f), shape = CircleShape)
        )
        Column(
            Modifier
                .size(maxWidth)
                .clip(CircleShape)
                .background(contentColor.copy(alpha = 0.1f))
                .clickable { onSnooze() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (title.isNotEmpty()) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.size(8.dp))
            }
            Text(
                modifier = Modifier.fillMaxWidth(0.8f),
                text = stringResource(R.string.tap_to_snooze),
                color = contentColor,
                textAlign = TextAlign.Center,
                style = if (title.isNotEmpty()) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.titleLarge
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StopButton(
    modifier: Modifier = Modifier,
    contentColor: Color,
    onStop: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(contentColor.copy(alpha = 0.1f))
                .clickable { onStop() }
                .padding(16.dp),
            imageVector = Icons.Default.Close,
            contentDescription = null,
            tint = contentColor
        )
    }
}