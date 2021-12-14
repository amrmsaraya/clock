package com.github.amrmsaraya.clock.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
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
    NavigationBar {
        for (screen in screens) {
            NavigationBarItem(
                selected = currentDestination?.route == screen.route,
                icon = {
                    when (currentDestination?.route == screen.route) {
                        true -> androidx.compose.material3.Icon(
                            imageVector = screen.activeIcon,
                            contentDescription = null
                        )
                        false -> androidx.compose.material3.Icon(
                            imageVector = screen.inactiveIcon,
                            contentDescription = null
                        )
                    }
                },
                label = {
                    androidx.compose.material3.Text(
                        text = stringResource(id = screen.title),
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                ),
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}