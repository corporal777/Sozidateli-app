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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.ui.agreement.EventAgreementBottomSheet
import com.example.ui.components.AppBottomNavigation
import com.example.ui.theme.BottomNavigationBarColor
import com.example.ui.views.dialogs.ProgressDialog
import com.google.accompanist.systemuicontroller.SystemUiController

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()


    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showSplashScreen()
        enableEdgeToEdge()
        setContent {
            SozidateliTheme(dynamicColor = false) {
                val navController = rememberNavController()
                val systemUiColor = rememberSystemUiController()

                SetStatusBarColor(navController, systemUiColor)
                SetNavigationBarColor(systemUiColor)

                SetProgressLoading()

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
    private fun SetProgressLoading() {
        val isLoadingState by viewModel.progressLoading.collectAsState()
        var isLoading by remember { mutableStateOf(false) }
        LaunchedEffect(isLoadingState) { isLoading = isLoadingState }
        ProgressDialog(isLoading)
    }

    @Composable
    private fun SetStatusBarColor(navController: NavController, uiController: SystemUiController) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route ?: ""
        val isSystemInDarkMode = currentRoute != Route.AuthorizationScreen.route
        SideEffect { uiController.setStatusBarColor(Color.Transparent, isSystemInDarkMode) }
    }

    @Composable
    private fun SetNavigationBarColor(uiController: SystemUiController) {
        SideEffect { uiController.setNavigationBarColor(BottomNavigationBarColor, true) }
    }

    private fun showSplashScreen() {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.splashCondition.value
            }
        }
    }
}