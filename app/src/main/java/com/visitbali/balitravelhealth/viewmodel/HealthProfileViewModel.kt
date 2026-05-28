package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.dto.CreateHealthProfileRequest
import com.visitbali.balitravelhealth.data.dto.HealthProfile
import com.visitbali.balitravelhealth.data.repository.HealthProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class HealthProfileUiState(
    val profile: HealthProfile? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    // Editable fields
    val tinggiCm: String = "",
    val beratKg: String = "",
    val golonganDarah: String = "",
    val riwayatAlergi: String = "",
    val jenisKelamin: String = "",
)

class HealthProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HealthProfileRepository(RetrofitClient.apiService)

    private val _uiState = MutableStateFlow(HealthProfileUiState())
    val uiState: StateFlow<HealthProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getProfile()
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            profile = profile,
                            isLoading = false,
                            tinggiCm = profile.tinggiCm?.toString() ?: "",
                            beratKg = profile.beratKg?.toString() ?: "",
                            golonganDarah = profile.golonganDarah ?: "",
                            riwayatAlergi = profile.riwayatAlergi ?: "",
                            jenisKelamin = profile.jenisKelamin ?: "",
                        )
                    }
                }
                .onFailure { e ->
                    // 404 = profile not yet created (expected for new users) — show empty state
                    val isNotFound = (e as? HttpException)?.code() == 404
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = if (isNotFound) null else e.message,
                        )
                    }
                }
        }
    }

    fun toggleEditMode() {
        val profile = _uiState.value.profile
        _uiState.update {
            it.copy(
                isEditMode = true,
                // Reset draft fields to current saved values
                tinggiCm = profile?.tinggiCm?.toString() ?: "",
                beratKg = profile?.beratKg?.toString() ?: "",
                golonganDarah = profile?.golonganDarah ?: "",
                riwayatAlergi = profile?.riwayatAlergi ?: "",
                jenisKelamin = profile?.jenisKelamin ?: "",
            )
        }
    }

    fun cancelEdit() {
        _uiState.update { it.copy(isEditMode = false) }
    }

    fun updateTinggi(v: String) = _uiState.update { it.copy(tinggiCm = v) }
    fun updateBerat(v: String) = _uiState.update { it.copy(beratKg = v) }
    fun updateGolonganDarah(v: String) = _uiState.update { it.copy(golonganDarah = v) }
    fun updateRiwayatAlergi(v: String) = _uiState.update { it.copy(riwayatAlergi = v) }
    fun updateJenisKelamin(v: String) = _uiState.update { it.copy(jenisKelamin = v) }

    fun saveProfile() {
        val state = _uiState.value
        val request = CreateHealthProfileRequest(
            tinggiCm = state.tinggiCm.toFloatOrNull(),
            beratKg = state.beratKg.toFloatOrNull(),
            golonganDarah = state.golonganDarah.ifBlank { null },
            riwayatAlergi = state.riwayatAlergi.ifBlank { null },
            jenisKelamin = state.jenisKelamin.ifBlank { null },
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val result = if (state.profile != null) {
                repository.updateProfile(request)
            } else {
                repository.createProfile(request)
            }
            result
                .onSuccess { updated ->
                    _uiState.update {
                        it.copy(
                            profile = updated,
                            isSaving = false,
                            isEditMode = false,
                            successMessage = "Health profile saved",
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, error = e.message) }
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
