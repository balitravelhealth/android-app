package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.dto.AssessmentResult
import com.visitbali.balitravelhealth.data.dto.ExpertSymptom
import com.visitbali.balitravelhealth.data.dto.NearbyFacility
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import com.visitbali.balitravelhealth.data.repository.AssessmentRepository
import com.visitbali.balitravelhealth.data.repository.AssessmentStateRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AssessmentUiState(
    val symptoms: List<ExpertSymptom> = emptyList(),
    val selectedSymptoms: Set<Int> = emptySet(),
    val result: AssessmentResult? = null,
    val history: List<AssessmentResult> = emptyList(),
    val nearbyFacilities: List<NearbyFacility> = emptyList(),
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
)

class AssessmentViewModel(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitClient.apiService
    private val repository = AssessmentRepository(api)
    private val assessmentStateRepository = AssessmentStateRepository(UserPreferences(application))

    private val _uiState = MutableStateFlow(AssessmentUiState(isLoading = true))
    val uiState: StateFlow<AssessmentUiState> = _uiState.asStateFlow()

    fun load(kategori: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val symptoms = async { runCatching { api.getExpertSymptoms(kategori).data } }
            val history = async { repository.getHistory() }

            _uiState.update {
                it.copy(
                    symptoms = symptoms.await().getOrElse { error ->
                        _uiState.value.error ?: run { error.message ?: "Failed to load symptoms" }
                        emptyList()
                    },
                    history = history.await().getOrNull()?.data.orEmpty(),
                    isLoading = false,
                )
            }
        }
    }

    fun toggleSymptom(id: Int) {
        _uiState.update { state ->
            state.copy(
                selectedSymptoms = if (id in state.selectedSymptoms) {
                    state.selectedSymptoms - id
                } else {
                    state.selectedSymptoms + id
                }
            )
        }
    }

    fun submitAssessment(kategori: String) {
        val symptoms = _uiState.value.selectedSymptoms.toList()
        if (symptoms.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            repository.submitAssessment(symptoms, kategori)
                .onSuccess { result ->
                    assessmentStateRepository.setHealthRiskAssessmentCompleted(true)
                    _uiState.update {
                        it.copy(
                            result = result,
                            history = listOf(result) + it.history,
                            selectedSymptoms = emptySet(),
                            isSubmitting = false,
                        )
                    }
                    if (result.riskLevel == "high") {
                        fetchNearbyFacilities()
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isSubmitting = false, error = e.message ?: "Assessment failed")
                    }
                }
        }
    }

    private fun fetchNearbyFacilities() {
        viewModelScope.launch {
            runCatching {
                api.getFacilitiesNearby(lat = -8.6705, lng = 115.2126, radiusKm = 15f, limit = 5).data
            }.onSuccess { facilities ->
                _uiState.update { it.copy(nearbyFacilities = facilities) }
            }
        }
    }

    fun clearResult() {
        _uiState.update { it.copy(result = null, nearbyFacilities = emptyList()) }
    }
}
