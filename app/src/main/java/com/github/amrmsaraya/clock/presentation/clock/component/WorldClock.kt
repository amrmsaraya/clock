package com.github.amrmsaraya.clock.presentation.clock.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.amrmsaraya.clock.domain.entity.WorldClock
import java.util.*


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorldClockRow(
    modifier: Modifier = Modifier,
    worldClock: WorldClock,
    timeZone: TimeZone,
    selected: Boolean,
    selectMode: Boolean,
    onSelectMode: () -> Unit,
    onSelect: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
            .combinedClickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    if (selectMode) {
                        onSelect(!selected)
                    }
                },
                onLongClick = {
                    onSelectMode()
                    onSelect(true)
                },
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnalogClock(
                modifier = Modifier.size(50.dp),
                worldClock = worldClock,
                color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column {
                Text(text = timeZone.id.substringAfter('/'), fontSize = 18.sp)
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = timeZone.getDisplayName(false, TimeZone.SHORT),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(!selectMode) {
                Text(
                    text = buildString {
                        append(
                            if (worldClock.calendar.get(Calendar.HOUR) == 0) "12"
                            else worldClock.calendar.get(Calendar.HOUR)
                        )
                        append(":")
                        append("%02d".format(worldClock.calendar.get(Calendar.MINUTE)))
                        append(" ")
                        append(if (worldClock.calendar.get(Calendar.AM_PM) == 0) "AM" else "PM")
                    },

                    fontSize = 20.sp
                )
            }
            AnimatedVisibility(selectMode) {
                Spacer(modifier = Modifier.size(8.dp))
                Checkbox(
                    checked = selected,
                    onCheckedChange = onSelect,
                )
            }
        }
    }
}