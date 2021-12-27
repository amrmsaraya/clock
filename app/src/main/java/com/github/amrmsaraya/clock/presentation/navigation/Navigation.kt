package com.github.amrmsaraya.clock.presentation.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.amrmsaraya.clock.feature_alarm.presentation.ui.alarm_screen.AlarmScreen
import com.github.amrmsaraya.clock.feature_clock.presentation.clock_screen.ClockScreen
import com.github.amrmsaraya.clock.feature_stopwatch.presentation.ui.stopwatch_screen.StopwatchScreen
import com.github.amrmsaraya.clock.feature_timer.presentation.ui.timer_screen.TimerScreen
import com.github.amrmsaraya.clock.presentation.navigation.Screens.*
import com.github.amrmsaraya.clock.utils.*
import com.google.accompanist.pager.ExperimentalPagerApi
import dev.chrisbanes.snapper.ExperimentalSnapperApi

@ExperimentalFoundationApi
@ExperimentalSnapperApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@ExperimentalPagerApi
@Composable
fun Navigation(
    modifier: Modifier,
    navController: NavHostController,
    onShowBottomNavigation: (Boolean) -> Unit
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Alarm.route
    ) {
        composable(Alarm.route) {
            AlarmScreen(modifier = modifier,
                onSetAlarm = { context.setAlarm(it) },
                onCancelAlarm = { context.cancelAlarm(it) })
        }

        composable(Clock.route) {
            ClockScreen(
                modifier = modifier,
                onShowBottomNavigation = onShowBottomNavigation,
                onBackPress = {
                    navController.popBackStack()
                }
            )
        }

        composable(Stopwatch.route) {
            StopwatchScreen(
                modifier = modifier,
                onRegisterStopwatchReceiver = { context.registerStopwatchReceiver(it) },
                onRegisterLapsReceiver = { context.registerLapsReceiver(it) },
                onStartStopwatchService = { context.startStopwatchService() },
                onSendStopwatchBroadcastAction = { context.sendStopwatchBroadcastAction(it) }
            )
        }

        composable(Timer.route) {
            TimerScreen(modifier = modifier,
                onRegisterTimerReceiver = { context.registerTimerReceiver(it) },
                onStartTimerService = { context.startTimerService(it) },
                onConfigureTimerService = { context.configureTimerService(it) },
                onSendTimerBroadcastAction = { context.sendTimerBroadcastAction(it) }
            )
        }
    }
}
