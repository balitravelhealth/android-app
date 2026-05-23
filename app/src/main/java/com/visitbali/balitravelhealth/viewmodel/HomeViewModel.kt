package com.visitbali.balitravelhealth.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.database.AppDatabase
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import com.visitbali.balitravelhealth.data.repository.AppContentSyncRepository
import com.visitbali.balitravelhealth.data.repository.TravelRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class HomeUiState(
    val userName: String = "",
    val arrivalDate: String = "",
    val departureDate: String = "",
    val daysUntilDeparture: Long? = null,
    val isInBali: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = true
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val userPreferences = UserPreferences(application)
    private val travelRepository = TravelRepository(application)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val contentSyncRepository: AppContentSyncRepository

    private val _isInBali = MutableStateFlow(false)

    init {
        val db = AppDatabase.getDatabase(application)
        contentSyncRepository = AppContentSyncRepository(
            context = application,
            contentApi = RetrofitClient.contentApiService,
            nurseApi = RetrofitClient.nurseApiService,
            healthcareFacilityDao = db.healthcareFacilityDao(),
            guideItemDao = db.guideItemDao(),
            lifeSupportItemDao = db.lifeSupportItemDao(),
            nurseDao = db.nurseDao()
        )
        observeUserData()
        refreshData(syncContent = false)
    }

    private fun observeUserData() {
        viewModelScope.launch {
            combine(
                userPreferences.userProfile,
                travelRepository.arrivalDate,
                travelRepository.departureDate,
                _isInBali
            ) { profile, arrival, departure, inBali ->
                val daysUntilDeparture = calculateDaysUntil(departure)
                
                HomeUiState(
                    userName = profile.name.ifEmpty { "Traveler" },
                    arrivalDate = arrival ?: "Not set",
                    departureDate = departure ?: "Not set",
                    daysUntilDeparture = daysUntilDeparture,
                    isInBali = inBali,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.update { currentState ->
                    newState.copy(
                        isRefreshing = currentState.isRefreshing
                    )
                }
            }
        }
    }

    private fun calculateDaysUntil(dateString: String?): Long? {
        if (dateString.isNullOrEmpty()) return null
        return try {
            // Check for yyyy-MM-dd (Standard storage format)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val targetDate = LocalDate.parse(dateString, formatter)
            val today = LocalDate.now()
            ChronoUnit.DAYS.between(today, targetDate)
        } catch (e: Exception) {
            null
        }
    }

    fun refreshData(syncContent: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val locationTask = async { checkLocation() }
            val syncTask = async {
                if (syncContent) {
                    contentSyncRepository.refreshIfConnected()
                } else {
                    Result.success(Unit)
                }
            }
            locationTask.await()
            syncTask.await()
            _uiState.update { it.copy(isRefreshing = false, isLoading = false) }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun checkLocation() {
        try {
            val locationResult = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                null
            ).await()
            
            if (locationResult != null) {
                val inBali = checkIfInBali(locationResult)
                _isInBali.value = inBali
            }
        } catch (e: Exception) {
            _isInBali.value = false
        }
    }

    private fun checkIfInBali(location: Location): Boolean {
        // Bali rough bounding box
        val minLat = -8.85
        val maxLat = -8.05
        val minLng = 114.4
        val maxLng = 115.75
        
        return location.latitude in minLat..maxLat && 
               location.longitude in minLng..maxLng
    }
}
