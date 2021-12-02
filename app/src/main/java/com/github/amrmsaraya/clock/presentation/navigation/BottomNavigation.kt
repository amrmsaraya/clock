package com.github.amrmsaraya.clock.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.github.amrmsaraya.clock.presentation.navigation.Screens.*

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    val screens = listOf(
        Alarm,
        Clock,
        Stopwatch,
        Timer,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (currentDestination?.route in screens.map { it.route }) {
        Column {
            Divider(thickness = 1.dp)
            BottomNavigation(
                navController = navController,
                currentDestination = currentDestination,
                screens = screens
            )
        }
    }
}

@Composable
private fun BottomNavigation(
    navController: NavHostController,
    currentDestination: NavDestination?,
    screens: List<Screens>
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    ) {
        for (screen in screens) {
            BottomNavigationItem(
                selected = currentDestination?.route == screen.route,
                icon = {
                    when (currentDestination?.route == screen.route) {
                        true -> Icon(imageVector = screen.activeIcon, contentDescription = null)
                        false -> Icon(imageVector = screen.inactiveIcon, contentDescription = null)
                    }
                },
                label = { Text(text = stringResource(id = screen.title), maxLines = 1) },
                selectedContentColor = MaterialTheme.colors.secondary,
                unselectedContentColor = Color.Gray,
                onClick = {
                    if (currentDestination?.route != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}