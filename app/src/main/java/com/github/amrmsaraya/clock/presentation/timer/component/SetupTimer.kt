package com.github.amrmsaraya.clock.presentation.timer.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.github.amrmsaraya.clock.presentation.alarm.component.TimeChooser
import com.github.amrmsaraya.timer.Time

@Composable
fun SetupTimer(
    modifier: Modifier = Modifier,
    timer: Time,
    onTimeChange: (Long) -> Unit
) {

    var hours by remember { mutableStateOf(timer.hours) }
    var minutes by remember { mutableStateOf(timer.minutes) }
    var seconds by remember { mutableStateOf(timer.seconds) }

    LaunchedEffect(key1 = hours, key2 = minutes, key3 = seconds) {
        onTimeChange((hours * 60 * 60 * 1000L) + (minutes * 60 * 1000) + (seconds * 1000))
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeChooser(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxSize(),
                initial = timer.hours,
                leadingZeros = true,
                items = (0..99).toList(),
                onTimeChange = { hours = it }
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                text = ":",
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )
            TimeChooser(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxSize(),
                initial = timer.minutes,
                leadingZeros = true,
                items = (0..59).toList(),
                onTimeChange = { minutes = it }
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                text = ":",
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )
            TimeChooser(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxSize(),
                initial = timer.seconds,
                items = (0..59).toList(),
                leadingZeros = true,
                onTimeChange = { seconds = it }
            )
        }
    }
}