package com.github.amrmsaraya.clock.presentation.alarm_activity

import android.app.NotificationManager
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.github.amrmsaraya.clock.presentation.alarm.utils.Colors
import com.github.amrmsaraya.clock.presentation.theme.ClockTheme
import com.github.amrmsaraya.clock.presentation.theme.md_theme_dark_onPrimary
import com.github.amrmsaraya.clock.presentation.theme.md_theme_dark_primary
import com.github.amrmsaraya.clock.utils.turnScreenOffAndKeyguardOn
import com.github.amrmsaraya.clock.utils.turnScreenOnAndKeyguardOff
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {

    private lateinit var ringtone: Ringtone

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        turnScreenOnAndKeyguardOff()

        val title = intent.getStringExtra("title") ?: ""
        val hour = intent.getIntExtra("hour", 0)
        val minute = intent.getIntExtra("minute", 0)
        val amPm = intent.getIntExtra("amPm", 0)
        val color = intent.getIntExtra("color", 0)

        val ringtoneUri =
            intent.getStringExtra("ringtone")?.toUri() ?: RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_ALARM
            )

        ringtone = RingtoneManager.getRingtone(this, ringtoneUri)
        ringtone.play()

        setContent {
            App(
                title = title,
                time = "${"%02d".format(hour)}:${"%02d".format(minute)}",
                color = color
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Remove notification
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(intent.getIntExtra("id", 0))

        ringtone.stop()
        turnScreenOffAndKeyguardOn()
    }
}

@Composable
fun App(
    title: String,
    time: String,
    color: Int,
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

    ClockTheme(
        matchSystemBars = backgroundColor
    ) {
        Surface(color = backgroundColor) {
            val context = LocalContext.current

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                var size by remember { mutableStateOf(maxWidth * 0.7f) }
                val animatedSize by animateDpAsState(
                    targetValue = size,
                    animationSpec = tween(3000)
                )

                LaunchedEffect(key1 = true) {
                    while (true) {
                        size = if (size == maxWidth * 0.7f) {
                            maxWidth * 0.9f
                        } else {
                            maxWidth * 0.7f
                        }
                        delay(2000)
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .weight(0.7f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(animatedSize)
                                .border(
                                    3.dp,
                                    color = contentColor,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(16.dp),
                                text = time,
                                style = MaterialTheme.typography.displayLarge,
                                textAlign = TextAlign.Center,
                                color = contentColor
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(0.3f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(contentColor.copy(alpha = 0.2f))
                                .clickable { (context as AlarmActivity).finishAndRemoveTask() }
                                .padding(16.dp),
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = contentColor
                        )
                    }
                }
            }
        }
    }
}
