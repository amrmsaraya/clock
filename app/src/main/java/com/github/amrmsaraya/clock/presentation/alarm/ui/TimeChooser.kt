package com.github.amrmsaraya.clock.presentation.alarm.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalUnitApi::class, ExperimentalSnapperApi::class)
@Composable
fun TimeChooser(
    modifier: Modifier,
    state: LazyListState,
    size: Int,
    type: String,
    default: Int,
) {

    LaunchedEffect(default) {
        state.scrollToItem(if (type == "hour") default - 1 else default)
    }

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        state = state,
        verticalArrangement = Arrangement.SpaceEvenly,
        flingBehavior = rememberSnapperFlingBehavior(lazyListState = state)
    ) {
        items(size) {
            val viewedItems = state.layoutInfo.visibleItemsInfo

            val textSize = when (viewedItems.isNotEmpty()) {
                true -> when (viewedItems[(viewedItems.lastIndex) / 2].index) {
                    it -> 32.sp
                    else -> 18.sp
                }
                false -> 18.sp
            }
            val animatedColor by animateColorAsState(
                targetValue = if (viewedItems.isNotEmpty()) {
                    if (viewedItems[(viewedItems.size - 1) / 2].index == it) {
                        MaterialTheme.colors.onSurface
                    } else {
                        Color.Gray
                    }
                } else {
                    Color.Gray
                }
            )
            Text(
                modifier = Modifier.padding(top = 14.dp, bottom = 14.dp),
                text = if (it > size - 2 || it - 1 < 0) "" else {
                    "${if (type == "hour") it else "%02d".format(it - 1)}"
                },
                fontSize = textSize,
                color = animatedColor
            )
        }
    }
}