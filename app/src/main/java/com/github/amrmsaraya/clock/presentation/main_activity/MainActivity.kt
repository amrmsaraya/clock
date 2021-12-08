package com.github.amrmsaraya.clock.presentation.main_activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.github.amrmsaraya.clock.presentation.navigation.BottomNavigationBar
import com.github.amrmsaraya.clock.presentation.navigation.Navigation
import com.github.amrmsaraya.clock.presentation.theme.ClockTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splash = installSplashScreen()
        var keepSplash by mutableStateOf(true)
        splash.setKeepVisibleCondition { keepSplash }

        setContent {
            // Keep splash screen
            LaunchedEffect(true) {
                delay(200)
                keepSplash = false
            }

            App()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    ClockTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            val navController = rememberNavController()
            var showBottomNavigation by remember { mutableStateOf(true) }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (showBottomNavigation) {
                        BottomNavigationBar(navController)
                    }
                }
            ) {
                Navigation(
                    modifier = Modifier.padding(it),
                    navController = navController,
                    onShowBottomNavigation = { isVisible -> showBottomNavigation = isVisible }
                )
            }
        }
    }
}