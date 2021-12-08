package com.github.amrmsaraya.clock.presentation.alarm.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.amrmsaraya.clock.R

@ExperimentalFoundationApi
@Composable
fun AlarmCard(
    modifier: Modifier = Modifier,
    title: String,
    time: String,
    amPm: String,
    days: String,
    activeBackgroundColor: Color,
    inActiveBackgroundColor: Color,
    contentColor: Color,
    ringtone: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (checked) {
            activeBackgroundColor
        } else {
            inActiveBackgroundColor
        },
        animationSpec = tween(1000)
    )

    val animateContentColor by animateColorAsState(
        targetValue = if (checked) contentColor else activeBackgroundColor,
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
                        fontSize = 20.sp
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
                    text = if (days.isNotEmpty()) days else stringResource(R.string.ring_once),
                    style = MaterialTheme.typography.bodyMedium,
                    color = animateContentColor,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
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
