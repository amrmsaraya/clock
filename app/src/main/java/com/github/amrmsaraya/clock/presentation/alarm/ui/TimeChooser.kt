package com.github.amrmsaraya.clock.presentation.alarm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalUnitApi::class, ExperimentalSnapperApi::class)
@Composable
fun TimeChooser(
    modifier: Modifier,
    type: String,
    default: Int,
    onTimeChange: (Int) -> Unit
) {
    val state = rememberLazyListState()
    val list = if (type == "hour") (1..12).toList() else (0..59).toList()
    var showList by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(default) }

    LaunchedEffect(selectedItem) {
        onTimeChange(selectedItem)
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { showList = true }
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(16.dp),
            text = "%02d".format(selectedItem),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 22.sp
        )

        if (showList) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                horizontalAlignment = Alignment.CenterHorizontally,
                state = state,
                verticalArrangement = Arrangement.SpaceEvenly,
                flingBehavior = rememberSnapperFlingBehavior(lazyListState = state)
            ) {
                items(list) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedItem = it
                                showList = false
                            }
                            .padding(4.dp),
                        text = "%02d".format(it),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}