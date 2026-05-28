package com.visitbali.balitravelhealth.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.gson.Gson
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.database.AppDatabase
import com.visitbali.balitravelhealth.data.model.Nurse
import com.visitbali.balitravelhealth.data.repository.GuideRepository
import com.visitbali.balitravelhealth.data.repository.HealthcareFacilityRepository
import com.visitbali.balitravelhealth.data.repository.LifeSupportRepository
import com.visitbali.balitravelhealth.data.repository.NurseRepository
import com.visitbali.balitravelhealth.ui.screens.*
import com.visitbali.balitravelhealth.viewmodel.*

@Composable
fun BaliTravelHealthNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)

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
                        popUpTo(route = "travel_setup") { inclusive = true }
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
                onNavigateToHealthProfile = {
                    navController.navigate("health_profile")
                },
                onLoggedOut = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("guide") {
            val guideViewModel: GuideViewModel = viewModel(
                factory = GuideViewModel.Factory(
                    GuideRepository(
                        dao = db.guideItemDao(),
                        lifeSupportDao = db.lifeSupportItemDao(),
                        api = RetrofitClient.apiService,
                    )
                )
            )
            GuideScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onFlowClick = { flowId ->
                    navController.navigate("emergency_flow/$flowId")
                },
                onGuideClick = { categoryId ->
                    navController.navigate("guide_detail/${Uri.encode(categoryId)}")
                },
                viewModel = guideViewModel
            )
        }
        composable("guide_detail/{guideId}") { backStackEntry ->
            val guideViewModel: GuideViewModel = viewModel(
                factory = GuideViewModel.Factory(
                    GuideRepository(
                        dao = db.guideItemDao(),
                        lifeSupportDao = db.lifeSupportItemDao(),
                        api = RetrofitClient.apiService,
                    )
                )
            )
            GuideDetailScreen(
                categoryId = backStackEntry.arguments?.getString("guideId") ?: "CEK_NAPAS",
                onBack = { navController.popBackStack() },
                viewModel = guideViewModel,
            )
        }
        composable("assessment/{kategori}") { backStackEntry ->
            val kategori = backStackEntry.arguments?.getString("kategori") ?: "pre_travel"
            AssessmentScreen(
                kategori = kategori,
                onBack = { navController.popBackStack() },
            )
        }
        composable("vaccination") {
            VaccinationScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable("health_profile") {
            HealthProfileScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable("service_center") {
            ServiceCenterScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable("destinations") {
            DestinationScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable("emergency_flows") {
            EmergencyGuideFlowScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable("emergency_flow/{flowId}") { backStackEntry ->
            val flowId = backStackEntry.arguments?.getString("flowId")?.toIntOrNull()
            EmergencyGuideFlowScreen(
                initialFlowId = flowId,
                onBack = { navController.popBackStack() },
            )
        }
        composable("pre_travel") {
            PreTravelScreen(
                onBack = { navController.popBackStack() },
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
                onNavigateToProfile = {
                    navController.navigate("profile") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onNavigateToAssessment = {
                    navController.navigate("assessment/pre_travel")
                },
                onNavigateToVaccination = {
                    navController.navigate("vaccination")
                }
            )
        }
        composable("post_travel") {
            PostTravelScreen(
                onBack = { navController.popBackStack() },
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
                onNavigateToProfile = {
                    navController.navigate("profile") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onNavigateToAssessment = {
                    navController.navigate("assessment/post_travel")
                },
            )
        }
        composable("nursing_care") {
            val repository = NurseRepository(
                api = RetrofitClient.apiService,
                nurseDao = db.nurseDao(),
            )
            val nursingViewModel: NursingCareViewModel = viewModel(
                factory = NursingCareViewModel.Factory(repository)
            )
            NursingCareScreen(
                viewModel = nursingViewModel,
                onBack = { navController.popBackStack() },
                onRecordsClick = {
                    navController.navigate("nursing_records")
                },
                onNurseClick = { nurse ->
                    val nurseJson = Uri.encode(Gson().toJson(nurse))
                    navController.navigate("nurse_detail/$nurseJson")
                }
            )
        }
        composable("nurse_detail/{nurseJson}") { backStackEntry ->
            val nurseJson = backStackEntry.arguments?.getString("nurseJson")
            val nurse = Gson().fromJson(nurseJson, Nurse::class.java)
            val travelViewModel: TravelViewModel = viewModel()
            val travelUiState by travelViewModel.uiState.collectAsState()

            val nurseRepository = NurseRepository(
                api = RetrofitClient.apiService,
                nurseDao = db.nurseDao(),
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
                onNavigateToEmergencyFlows = {
                    navController.navigate("emergency_flows")
                },
                onNavigateToFlow = { flowId ->
                    navController.navigate("emergency_flow/$flowId")
                },
                viewModel = healthcareViewModel,
                lifeSupportViewModel = lifeSupportViewModel
            )
        }
        composable("nursing_records") {
            NursingRecordsScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable("healthcare") {
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
