package com.github.amrmsaraya.clock.presentation.common_ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun BottomDrawerSheet(
    modifier: Modifier = Modifier,
    drawerState: BottomDrawerState,
    drawerContent: @Composable (ColumnScope.() -> Unit),
    content: @Composable () -> Unit
) {
    BottomDrawer(
        modifier = modifier,
        gesturesEnabled = false,
        drawerState = drawerState,
        drawerElevation = 0.dp,
        scrimColor = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        drawerShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        drawerContent = {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Divider(
                    Modifier
                        .fillMaxWidth(.2f)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally),
                    thickness = 5.dp
                )
                Spacer(modifier = Modifier.size(8.dp))
                drawerContent()
            }
        },
        content = content
    )
}