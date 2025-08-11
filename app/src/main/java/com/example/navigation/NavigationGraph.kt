package com.example.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navOptions
import androidx.navigation.navigation
import com.example.extensions.animComposable
import com.example.extensions.enterTransition
import com.example.ui.auth.authorization.AuthorizationScreen
import com.example.ui.auth.login.LoginScreen
import com.example.ui.event.EventDetailScreen
import com.example.ui.home.HomeScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    startDestination : String
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(route = Route.HomeScreen.route) {
            HomeScreen(paddingValues){ navController.navigate(it) }
        }


        animComposable(route = Route.AuthorizationScreen.route) {
            AuthorizationScreen(paddingValues) {
                navController.navigate(Route.LoginScreen.route)
            }
        }
        animComposable(route = Route.LoginScreen.route) {
            LoginScreen(paddingValues){
                if (it == Route.HomeScreen.route) navigatePopUp(navController, it)
                else navController.navigate(it)
            }
        }

        animComposable(route = Route.EventDetailScreen.route) { stack ->
            val eventId = stack.arguments?.getString("id") ?: "0"
            EventDetailScreen(eventId, paddingValues)
        }
    }
}

private fun navigatePopUp(navController: NavController, route: String) {
    navController.navigate(route, navOptions {
        navController.graph.startDestinationRoute?.let { start ->
            popUpTo(start) { inclusive = true }
        }
    })

//    navController.navigate(route) {
//        navController.graph.startDestinationRoute?.let { start ->
//            popUpTo(start) {
//                saveState = true
//            }
//        }
//        launchSingleTop = true
//        restoreState = true
//    }
}