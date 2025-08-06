package com.example.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navigation.NavigationGraph
import com.example.navigation.Route
import com.example.ui.theme.SozidateliTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.ui.components.AppBottomNavigation
import com.example.ui.theme.BottomNavigationBarColor

@AndroidEntryPoint
class SozidateliActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showSplashScreen()
        enableEdgeToEdge()
        setContent {
            SozidateliTheme(dynamicColor = false) {
                val navController = rememberNavController()

                SetSystemBarsColor(navController)

                CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
                    Scaffold(
                        bottomBar = { AppBottomNavigation(navController) }
                    ) { padding ->
                        NavigationGraph(navController, padding, viewModel.startDestination.value)
                    }
                }


            }

        }

    }

    @Composable
    private fun SetSystemBarsColor(navController: NavController) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route ?: ""

        val isSystemInDarkMode = currentRoute != Route.AuthorizationScreen.route
        val systemUiColor = rememberSystemUiController()
        val navBarColor =
            if (currentRoute != Route.AuthorizationScreen.route) BottomNavigationBarColor
            else Color.Transparent

        SideEffect {
            systemUiColor.setNavigationBarColor(
                color = BottomNavigationBarColor,
                darkIcons = true
            )
            systemUiColor.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = isSystemInDarkMode
            )
        }
    }

    private fun showSplashScreen() {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.splashCondition.value
            }
        }
    }
}