package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.dto.CreateVaccinationRequest
import com.visitbali.balitravelhealth.data.dto.Vaccination
import com.visitbali.balitravelhealth.data.repository.VaccinationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VaccinationUiState(
    val vaccinations: List<Vaccination> = emptyList(),
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
)

class VaccinationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VaccinationRepository(RetrofitClient.apiService)

    private val _uiState = MutableStateFlow(VaccinationUiState())
    val uiState: StateFlow<VaccinationUiState> = _uiState.asStateFlow()

    init {
        loadVaccinations()
    }

    fun loadVaccinations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getVaccinations()
                .onSuccess { list ->
                    _uiState.update { it.copy(vaccinations = list, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun addVaccination(
        jenisVaksin: String,
        tanggal: String,
        dosis: String?,
        catatan: String?,
    ) {
        if (jenisVaksin.isBlank() || tanggal.isBlank()) {
            _uiState.update { it.copy(error = "Vaccine name and date are required") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            repository.addVaccination(
                CreateVaccinationRequest(
                    jenisVaksin = jenisVaksin.trim(),
                    tanggal = tanggal,
                    dosis = dosis?.ifBlank { null },
                    catatan = catatan?.ifBlank { null },
                )
            ).onSuccess { added ->
                _uiState.update { state ->
                    state.copy(
                        vaccinations = listOf(added) + state.vaccinations,
                        isSubmitting = false,
                        successMessage = "Vaccination recorded",
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isSubmitting = false, error = e.message) }
            }
        }
    }

    fun deleteVaccination(id: Int) {
        viewModelScope.launch {
            repository.deleteVaccination(id)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            vaccinations = state.vaccinations.filter { it.id != id },
                            successMessage = "Vaccination removed",
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
