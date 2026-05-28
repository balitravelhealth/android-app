package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.dto.ExpertSymptom
import com.visitbali.balitravelhealth.data.dto.HealthResponse
import com.visitbali.balitravelhealth.data.dto.LocationClassificationResponse
import com.visitbali.balitravelhealth.data.dto.NearbyFacility
import com.visitbali.balitravelhealth.data.repository.ServiceCenterRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ServiceCenterUiState(
    val health: HealthResponse? = null,
    val location: LocationClassificationResponse? = null,
    val nearbyFacilities: List<NearbyFacility> = emptyList(),
    val preTravelSymptoms: List<ExpertSymptom> = emptyList(),
    val postTravelSymptoms: List<ExpertSymptom> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class ServiceCenterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ServiceCenterRepository(RetrofitClient.apiService)

    private val _uiState = MutableStateFlow(ServiceCenterUiState(isLoading = true))
    val uiState: StateFlow<ServiceCenterUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh(
        lat: Double = DENPASAR_LAT,
        lng: Double = DENPASAR_LNG,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val health = async { repository.health() }
            val location = async { repository.classifyLocation(lat, lng) }
            val nearby = async { repository.nearbyFacilities(lat, lng) }
            val preSymptoms = async { repository.expertSymptoms("pre_travel") }
            val postSymptoms = async { repository.expertSymptoms("post_travel") }

            val failures = mutableListOf<String>()
            val healthResult = health.await().onFailure { failures += it.message ?: "Health check failed" }.getOrNull()
            val locationResult = location.await().onFailure { failures += it.message ?: "Location classification failed" }.getOrNull()
            val nearbyResult = nearby.await().onFailure { failures += it.message ?: "Nearby facilities failed" }.getOrNull().orEmpty()
            val preResult = preSymptoms.await().onFailure { failures += it.message ?: "Pre-travel symptoms failed" }.getOrNull().orEmpty()
            val postResult = postSymptoms.await().onFailure { failures += it.message ?: "Post-travel symptoms failed" }.getOrNull().orEmpty()

            _uiState.update {
                it.copy(
                    health = healthResult,
                    location = locationResult,
                    nearbyFacilities = nearbyResult,
                    preTravelSymptoms = preResult,
                    postTravelSymptoms = postResult,
                    isLoading = false,
                    error = failures.firstOrNull(),
                )
            }
        }
    }

    private companion object {
        const val DENPASAR_LAT = -8.6705
        const val DENPASAR_LNG = 115.2126
    }
}
