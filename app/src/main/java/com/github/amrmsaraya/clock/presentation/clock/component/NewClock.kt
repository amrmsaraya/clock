package com.github.amrmsaraya.clock.presentation.clock.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.amrmsaraya.clock.R
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun NewClock(
    modifier: Modifier = Modifier,
    timeZones: List<TimeZone>,
    onClick: (TimeZone) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val zones = remember { mutableListOf<TimeZone>() }
    var search by remember { mutableStateOf("") }

    if (search.isNotEmpty()) {
        zones.clear()
        zones.addAll(timeZones.filter { it.id.contains(search, ignoreCase = true) })
    } else {
        zones.addAll(timeZones)
    }

    Column(modifier = modifier) {
        SearchTextField(
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            search = search,
            onSearch = { search = it },
            onClearSearch = { search = "" },
        )

        Spacer(modifier = Modifier.size(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 16.dp),
            state = lazyListState
        ) {
            items(zones) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            search = ""
                            zones.clear()
                            zones.addAll(timeZones)
                            onClick(it)
                            scope.launch { lazyListState.scrollToItem(0) }
                        },
                ) {
                    Column(
                        Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = it.id.substringAfter('/'), fontSize = 18.sp)
                        Text(
                            text = it.getDisplayName(false, TimeZone.SHORT),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
}

@Composable
private fun SearchTextField(
    modifier: Modifier = Modifier,
    search: String,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
) {
    BasicTextField(
        value = search,
        onValueChange = { onSearch(it) },
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    ) {
        Surface(
            modifier = modifier,
            shape = CircleShape,
            tonalElevation = 3.dp,
        ) {
            Row(
                Modifier.padding(start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(enabled = false, onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                    Box {
                        if (search.isEmpty()) {
                            Text(
                                text = stringResource(R.string.time_zone_placeholder),
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 18.sp
                            )
                        }
                        it()
                    }
                }
                if (search.isNotEmpty()) {
                    IconButton(onClick = onClearSearch) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

