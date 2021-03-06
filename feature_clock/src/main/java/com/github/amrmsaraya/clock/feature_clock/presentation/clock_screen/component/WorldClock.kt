package com.github.amrmsaraya.clock.feature_clock.presentation.clock_screen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.amrmsaraya.clock.common.utils.format
import com.github.amrmsaraya.clock.feature_clock.R
import com.github.amrmsaraya.clock.feature_clock.domain.entity.WorldClock
import java.util.*

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
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
            MaterialClock(
                modifier = Modifier.size(50.dp),
                worldClock = worldClock,
                frameColor = MaterialTheme.colorScheme.primary,
                handleColor = MaterialTheme.colorScheme.primary,
                showSeconds = false
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column {
                Text(text = timeZone.id.substringAfter('/'), fontSize = 18.sp)
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = timeZone.getDisplayName(false, TimeZone.SHORT),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(!selectMode) {
                Text(
                    text = buildString {
                        append(
                            if (worldClock.calendar.get(Calendar.HOUR) == 0) 12.format()
                            else worldClock.calendar.get(Calendar.HOUR).format()
                        )
                        append(":")
                        append("%02d".format(worldClock.calendar.get(Calendar.MINUTE)))
                        append(" ")
                        append(
                            if (worldClock.calendar.get(Calendar.AM_PM) == 0) stringResource(id = R.string.am) else stringResource(
                                id = R.string.pm
                            )
                        )
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