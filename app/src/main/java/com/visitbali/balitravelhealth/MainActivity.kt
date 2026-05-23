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
import com.visitbali.balitravelhealth.ui.screens.SetupTravelScreen
import com.visitbali.balitravelhealth.ui.screens.HomeScreen
import com.visitbali.balitravelhealth.ui.screens.PreTravelScreen
import com.visitbali.balitravelhealth.ui.screens.PostTravelScreen
import com.visitbali.balitravelhealth.ui.screens.DuringTravelScreen
import com.visitbali.balitravelhealth.ui.screens.NursingCareScreen
import com.visitbali.balitravelhealth.ui.screens.GuideScreen
import com.visitbali.balitravelhealth.ui.screens.ProfileScreen
import com.visitbali.balitravelhealth.viewmodel.ProfileViewModel
import com.visitbali.balitravelhealth.data.repository.NurseRepository
import com.visitbali.balitravelhealth.viewmodel.NursingCareViewModel
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.ui.screens.NurseDetailScreen
import com.visitbali.balitravelhealth.viewmodel.TravelViewModel
import com.visitbali.balitravelhealth.viewmodel.SetupViewModel
import com.visitbali.balitravelhealth.data.model.Nurse
import com.google.gson.Gson
import com.visitbali.balitravelhealth.ui.screens.HealthcareScreen
import com.visitbali.balitravelhealth.data.repository.HealthcareFacilityRepository
import com.visitbali.balitravelhealth.data.repository.GuideRepository
import com.visitbali.balitravelhealth.data.repository.LifeSupportRepository
import com.visitbali.balitravelhealth.viewmodel.HealthcareFacilityViewModel
import com.visitbali.balitravelhealth.viewmodel.GuideViewModel
import com.visitbali.balitravelhealth.viewmodel.LifeSupportViewModel
import com.visitbali.balitravelhealth.data.database.AppDatabase
import androidx.lifecycle.viewmodel.compose.viewModel


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
                                    navController.navigate("travel_setup") {
                                        popUpTo("setup") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("travel_setup") {
                            SetupTravelScreen(
                                onNext = {
                                    navController.navigate("home") {
                                        popUpTo("travel_setup") { inclusive = true }
                                    }
                                },
                                onBack = {
                                    navController.navigate(route = "setup") {
                                        popUpTo(route = "travel_setup") {inclusive = true}
                                    }
                                },
                                onSkip = {
                                    navController.navigate("home") {
                                        popUpTo("travel_setup") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                onNavigateToPreTravel = {
                                    navController.navigate("pre_travel")
                                },
                                onNavigateToDuringTravel = {
                                    navController.navigate("during_travel")
                                },
                                onNavigateToPostTravel = {
                                    navController.navigate("post_travel")
                                },
                                onNavigateToNursingCare = {
                                    navController.navigate("nursing_care")
                                },
                                onNavigateToGuide = {
                                    navController.navigate("guide")
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile")
                                }
                            )
                        }
                        composable("profile") {
                            ProfileScreen(
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onNavigateToGuide = {
                                    navController.navigate("guide") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                },
                                onLoggedOut = {
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("guide") {
                            val context = androidx.compose.ui.platform.LocalContext.current
                            val db = AppDatabase.getDatabase(context)
                            val guideViewModel: GuideViewModel = viewModel(
                                factory = GuideViewModel.Factory(GuideRepository(db.guideItemDao()))
                            )
                            GuideScreen(
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                viewModel = guideViewModel
                            )
                        }
                        composable("pre_travel") {
                            PreTravelScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToHealthcare = {
                                    navController.navigate("healthcare")
                                }
                            )
                        }
                        composable("post_travel") {
                            PostTravelScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("nursing_care") {
                            val context = androidx.compose.ui.platform.LocalContext.current
                            val db = AppDatabase.getDatabase(context)
                            val repository = NurseRepository(
                                api = RetrofitClient.nurseApiService,
                                nurseDao = db.nurseDao()
                            )
                            val nursingViewModel: NursingCareViewModel = viewModel(
                                factory = NursingCareViewModel.Factory(repository)
                            )
                            NursingCareScreen(
                                viewModel = nursingViewModel,
                                onBack = { navController.popBackStack() },
                                onNurseClick = { nurse ->
                                    val nurseJson = java.net.URLEncoder.encode(Gson().toJson(nurse), "UTF-8")
                                    navController.navigate("nurse_detail/$nurseJson")
                                }
                            )
                        }
                        composable("nurse_detail/{nurseJson}") { backStackEntry ->
                            val nurseJson = backStackEntry.arguments?.getString("nurseJson")
                            val nurse = Gson().fromJson(nurseJson, Nurse::class.java)
                            val travelViewModel: TravelViewModel = viewModel()
                            val travelUiState by travelViewModel.uiState.collectAsState()
                            
                            val context = androidx.compose.ui.platform.LocalContext.current
                            val db = AppDatabase.getDatabase(context)
                            val nurseRepository = NurseRepository(
                                api = RetrofitClient.nurseApiService,
                                nurseDao = db.nurseDao()
                            )
                            val nursingViewModel: NursingCareViewModel = viewModel(
                                factory = NursingCareViewModel.Factory(nurseRepository)
                            )

                            NurseDetailScreen(
                                nurse = nurse,
                                arrivalDate = travelUiState.arrivalDate,
                                departureDate = travelUiState.departureDate,
                                onBack = { navController.popBackStack() },
                                onBookingComplete = {
                                    navController.popBackStack("nursing_care", inclusive = false)
                                },
                                viewModel = nursingViewModel
                            )
                        }
                        composable("during_travel") {
                            val context = androidx.compose.ui.platform.LocalContext.current
                            val db = AppDatabase.getDatabase(context)
                            val repository = HealthcareFacilityRepository(db.healthcareFacilityDao())
                            val healthcareViewModel: HealthcareFacilityViewModel = viewModel(
                                factory = HealthcareFacilityViewModel.Factory(repository)
                            )
                            val lifeSupportViewModel: LifeSupportViewModel = viewModel(
                                factory = LifeSupportViewModel.Factory(
                                    LifeSupportRepository(db.lifeSupportItemDao())
                                )
                            )
                            DuringTravelScreen(
                                onBack = { navController.popBackStack() },
                                onSeeMoreFacilities = {
                                    navController.navigate("healthcare")
                                },
                                viewModel = healthcareViewModel,
                                lifeSupportViewModel = lifeSupportViewModel
                            )
                        }
                        composable("healthcare") {
                            val context = androidx.compose.ui.platform.LocalContext.current
                            val db = AppDatabase.getDatabase(context)
                            val repository = HealthcareFacilityRepository(db.healthcareFacilityDao())
                            val healthcareViewModel: HealthcareFacilityViewModel = viewModel(
                                factory = HealthcareFacilityViewModel.Factory(repository)
                            )
                            HealthcareScreen(
                                viewModel = healthcareViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
