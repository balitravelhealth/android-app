package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.dto.Destination
import com.visitbali.balitravelhealth.data.dto.HealthRisk
import com.visitbali.balitravelhealth.data.repository.DestinationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DestinationUiState(
    val destinations: List<Destination> = emptyList(),
    val selectedDestination: Destination? = null,
    val healthRisks: List<HealthRisk> = emptyList(),
    val isLoadingList: Boolean = false,
    val isLoadingRisks: Boolean = false,
    val error: String? = null,
)

class DestinationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DestinationRepository(RetrofitClient.apiService)

    private val _uiState = MutableStateFlow(DestinationUiState())
    val uiState: StateFlow<DestinationUiState> = _uiState.asStateFlow()

    init {
        loadDestinations()
    }

    fun loadDestinations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingList = true, error = null) }
            repository.getDestinations()
                .onSuccess { list ->
                    _uiState.update { it.copy(destinations = list, isLoadingList = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoadingList = false, error = e.message) }
                }
        }
    }

    fun selectDestination(destination: Destination) {
        _uiState.update {
            it.copy(
                selectedDestination = destination,
                healthRisks = emptyList(),
                isLoadingRisks = true,
                error = null,
            )
        }
        loadHealthRisks(destination.id)
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedDestination = null, healthRisks = emptyList()) }
    }

    private fun loadHealthRisks(destinationId: Int) {
        viewModelScope.launch {
            repository.getHealthRisks(destinationId)
                .onSuccess { risks ->
                    _uiState.update { it.copy(healthRisks = risks, isLoadingRisks = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoadingRisks = false, error = e.message) }
                }
        }
    }
}
