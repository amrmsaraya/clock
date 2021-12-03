package com.github.amrmsaraya.clock.presentation.clock.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun AddClock(timeZones: List<TimeZone>, onClick: (TimeZone) -> Unit) {
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

    SearchTextField(
        search = search,
        onSearch = { search = it },
        onClearSearch = { search = "" }
    )

    Spacer(modifier = Modifier.size(16.dp))

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = lazyListState
    ) {
        items(zones) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .clickable {
                        search = ""
                        zones.clear()
                        zones.addAll(timeZones)
                        onClick(it)
                        scope.launch { lazyListState.scrollToItem(0) }
                    },
            ) {
                Column(
                    Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = it.id.substringAfter('/'), fontSize = 18.sp)
                    Text(
                        text = it.getDisplayName(false, TimeZone.SHORT),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
        }
    }
}

@Composable
private fun SearchTextField(
    search: String,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    BasicTextField(
        value = search,
        onValueChange = { onSearch(it) },
        textStyle = TextStyle(color = MaterialTheme.colors.onSurface, fontSize = 18.sp),
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        singleLine = true,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            elevation = 3.dp,
            shape = CircleShape
        ) {
            Row(
                Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Box {
                        if (search.isEmpty()) {
                            Text(
                                text = "City, country or region",
                                color = Color.Gray,
                                fontSize = 18.sp
                            )
                        }
                        it()
                    }
                }
                if (search.isNotEmpty()) {
                    Icon(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    onClearSearch()
                                }
                            ),
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

