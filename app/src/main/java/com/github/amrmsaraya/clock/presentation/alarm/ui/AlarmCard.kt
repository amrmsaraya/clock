package com.github.amrmsaraya.clock.presentation.alarm.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
    backgroundColor: Color,
    contentColor: Color,
    ringtone: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (checked) {
            backgroundColor
        } else {
            if (isSystemInDarkTheme()) {
                LocalElevationOverlay.current?.apply(
                    color = MaterialTheme.colors.surface,
                    elevation = LocalAbsoluteElevation.current + 1.dp
                ) ?: MaterialTheme.colors.surface
            } else {
                Color.LightGray
            }
        },
        animationSpec = tween(1000)
    )

    val animateContentColor by animateColorAsState(
        targetValue = if (checked) contentColor else Color.Gray,
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
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = time,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = amPm,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    if (title.isNotEmpty()) {
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = title,
                            color = animateContentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = if (days.isNotEmpty()) days else stringResource(R.string.ring_once),
                    style = MaterialTheme.typography.body2,
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
