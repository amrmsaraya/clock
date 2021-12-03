package com.github.amrmsaraya.clock.presentation.main_activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.navigation.compose.rememberNavController
import com.github.amrmsaraya.clock.presentation.navigation.BottomNavigationBar
import com.github.amrmsaraya.clock.presentation.navigation.Navigation
import com.github.amrmsaraya.clock.presentation.theme.ClockTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalComposeUiApi
@ExperimentalUnitApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalUnitApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun App() {
    ClockTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {

            val scaffoldState = rememberScaffoldState()
            val navController = rememberNavController()
            var showBottomNavigation by remember { mutableStateOf(true) }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                scaffoldState = scaffoldState,
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