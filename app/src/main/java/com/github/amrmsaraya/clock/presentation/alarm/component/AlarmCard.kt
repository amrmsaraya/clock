package com.github.amrmsaraya.clock.presentation.alarm.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.timer.toTime
import kotlin.math.ln

@ExperimentalFoundationApi
@Composable
fun AlarmCard(
    modifier: Modifier = Modifier,
    title: String,
    time: String,
    amPm: String,
    days: String,
    ringTime: Long,
    activeBackgroundColor: Color,
    contentColor: Color,
    selectMode: Boolean,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (checked) {
            activeBackgroundColor
        } else {
            val elevation = LocalAbsoluteTonalElevation.current + 3.dp
            val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
            MaterialTheme.colorScheme.primary.copy(alpha = alpha)
                .compositeOver(MaterialTheme.colorScheme.surface)
        },
        animationSpec = tween(1000)
    )

    val ringIn = ringTime - System.currentTimeMillis()

    val animateContentColor by animateColorAsState(
        targetValue = if (checked) contentColor else MaterialTheme.colorScheme.outline,
        animationSpec = tween(1000)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        backgroundColor = animatedBackgroundColor,
        elevation = 0.dp,
        shape = RoundedCornerShape(15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$time $amPm",
                        color = animateContentColor,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    if (title.isNotEmpty()) {
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = title,
                            color = animateContentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = when {
                        days.split(" ").size == 7 -> stringResource(R.string.everyday)
                        days.isNotEmpty() -> days
                        days.isEmpty() -> stringResource(R.string.ring_once)
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = animateContentColor.copy(alpha = 0.8f),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                AnimatedVisibility(checked && ringIn > 0) {
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = buildString {
                            append(stringResource(R.string.ring_in))
                            if (ringIn / (1000 * 60 * 60 * 24) > 0) {
                                append(" ${ringIn / (1000 * 60 * 60 * 24)} d")
                            }
                            if (ringIn.toTime().hours % 24 > 0) {
                                append(" ${ringIn.toTime().hours % 24} h")
                            }
                            if (ringIn.toTime().minutes > 0) {
                                append(" ${ringIn.toTime().minutes} min")
                            } else {
                                append(" ${stringResource(R.string.few_seconds)}")
                            }

                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = animateContentColor.copy(alpha = 0.8f),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
            if (!selectMode) {
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = animateContentColor,
                        checkedTrackColor = animateContentColor,
                        uncheckedThumbColor = animateContentColor,
                        uncheckedTrackColor = animateContentColor
                    )
                )
            }
        }
    }
}
