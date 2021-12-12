package com.github.amrmsaraya.clock.presentation.timer.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.amrmsaraya.clock.domain.entity.Timer
import com.github.amrmsaraya.clock.presentation.common_ui.stopwatchTimerFormat
import com.github.amrmsaraya.timer.toTime
import kotlin.math.ln

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxWithConstraintsScope.TimerTemplateItem(
    timer: Timer,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            val elevation = LocalAbsoluteTonalElevation.current + 3.dp
            val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
            MaterialTheme.colorScheme.primary.copy(alpha = alpha)
                .compositeOver(MaterialTheme.colorScheme.surface)
        },
        animationSpec = tween(500)
    )

    val animatedContentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(500)
    )

    Box(
        modifier = Modifier
            .size(maxWidth / 3)
            .padding(8.dp)
            .clip(CircleShape)
            .background(animatedBackgroundColor)
            .combinedClickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                text = timer.title,
                color = animatedContentColor,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                modifier = Modifier.padding(bottom = 10.dp, start = 10.dp, end = 10.dp),
                text = stopwatchTimerFormat(
                    time = timer.timeMillis.toTime(),
                    color = animatedContentColor,
                    withMillis = false
                ),
                color = animatedContentColor,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
    }
}

@Composable
fun BoxWithConstraintsScope.NewTimerTemplate(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(maxWidth / 3)
            .padding(8.dp),
        shape = CircleShape,
        tonalElevation = 3.dp,
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.padding(maxWidth / 9),
            imageVector = Icons.Default.Add,
            contentDescription = null
        )
    }
}