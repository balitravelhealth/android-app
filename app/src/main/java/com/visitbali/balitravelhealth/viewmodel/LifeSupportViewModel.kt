package com.visitbali.balitravelhealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.dto.EmergencyGuideFlowSummary
import com.visitbali.balitravelhealth.data.repository.LifeSupportRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LifeSupportUiState(
    val flows: List<EmergencyGuideFlowSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LifeSupportViewModel(
    private val repository: LifeSupportRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LifeSupportUiState())
    val uiState: StateFlow<LifeSupportUiState> = _uiState.asStateFlow()

    init {
        observeItems()
        loadFlows()
    }

    private fun observeItems() {
        repository.items
            .onEach { items ->
                _uiState.update { state ->
                    state.copy(
                        flows = items.map { it.toSummary() }.filter { flow ->
                            flow.kategori.lowercase() == "bls" || flow.kategori.lowercase() == "emergency"
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun com.visitbali.balitravelhealth.data.model.LifeSupportItem.toSummary(): EmergencyGuideFlowSummary =
        EmergencyGuideFlowSummary(
            id = id,
            title = title,
            kategori = kategori,
            deskripsi = deskripsi,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

    fun loadFlows() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = _uiState.value.flows.isEmpty()) }
            runCatching { RetrofitClient.apiService.getEmergencyGuideFlows() }
                .onSuccess { response ->
                    // Sync repository is handled in Splash, 
                    // but we can also sync here if needed.
                    // For now, Splash handles the global sync.
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    class Factory(private val repository: LifeSupportRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LifeSupportViewModel::class.java)) {
                return LifeSupportViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
