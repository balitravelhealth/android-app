package com.visitbali.balitravelhealth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.SplashViewModel
import com.visitbali.balitravelhealth.ui.screens.LoginScreen
import com.visitbali.balitravelhealth.ui.screens.SetupScreen
import com.visitbali.balitravelhealth.ui.screens.HomeScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.visitbali.balitravelhealth.viewmodel.SetupViewModel


class MainActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        enableEdgeToEdge()
        setContent {
            BaliTravelHealthTheme {
                val navController = rememberNavController()
                val startDestination by viewModel.startDestination.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()

                if (!isLoading) {
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login") {
                            LoginScreen(
                                onNavigateToSetup = {
                                    navController.navigate("setup") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("setup") {
                            val setupViewModel: SetupViewModel = viewModel()
                            SetupScreen(
                                viewModel = setupViewModel,
                                onBackClick = {
                                    setupViewModel.signOut {
                                        navController.navigate("login") {
                                            popUpTo("setup") { inclusive = true }
                                        }
                                    }
                                },
                                onComplete = {
                                    navController.navigate("home") {
                                        popUpTo("setup") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen()
                        }
                    }
                }
            }
        }
    }
}
