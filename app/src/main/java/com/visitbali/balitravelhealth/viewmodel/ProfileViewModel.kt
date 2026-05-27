package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.BuildConfig
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.dto.UpdateHealthProfileRequest
import com.visitbali.balitravelhealth.data.dto.UpdateTravelerProfileRequest
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import com.visitbali.balitravelhealth.data.repository.AssessmentStateRepository
import com.visitbali.balitravelhealth.data.repository.AuthRepository
import com.visitbali.balitravelhealth.data.repository.HealthProfileRepository
import com.visitbali.balitravelhealth.data.repository.TravelerProfileRepository
import com.visitbali.balitravelhealth.data.util.ProfileFormatters
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val dob: String = "",
    val country: String = "",
    val gender: String = "",
    val arrivalDate: String? = null,
    val departureDate: String? = null,
    val hasCompletedHealthRiskAssessment: Boolean = false,
    val profilePictureUri: Uri? = null,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = UserPreferences(application)
    private val travelerProfileRepository = TravelerProfileRepository(
        RetrofitClient.apiService,
        preferences,
    )
    private val healthProfileRepository = HealthProfileRepository(RetrofitClient.apiService)
    private val authRepository = AuthRepository(RetrofitClient.apiService, preferences)
    private val assessmentStateRepository = AssessmentStateRepository(preferences)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var originalState = ProfileUiState()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            combine(
                preferences.userProfile,
                assessmentStateRepository.hasCompletedHealthRiskAssessment,
            ) { profile, assessmentComplete ->
                val state = ProfileUiState(
                    name = profile.name,
                    email = profile.email,
                    dob = profile.dob,
                    country = profile.country,
                    gender = profile.gender,
                    arrivalDate = profile.arrivalDate,
                    departureDate = profile.departureDate,
                    hasCompletedHealthRiskAssessment = assessmentComplete,
                )
                state
            }.collect { state ->
                if (!_uiState.value.isEditMode) {
                    originalState = state
                    _uiState.value = state
                } else {
                    _uiState.update {
                        it.copy(
                            hasCompletedHealthRiskAssessment = state.hasCompletedHealthRiskAssessment,
                            arrivalDate = state.arrivalDate,
                            departureDate = state.departureDate,
                        )
                    }
                }
            }
        }
    }

    fun toggleEditMode() = _uiState.update { it.copy(isEditMode = !it.isEditMode) }

    fun cancelEdit() = _uiState.update { originalState.copy(isEditMode = false, error = null) }

    fun updateName(name: String) = _uiState.update { it.copy(name = name) }
    fun updateDob(dob: String) = _uiState.update { it.copy(dob = dob) }
    fun updateCountry(country: String) = _uiState.update { it.copy(country = country) }
    fun updateGender(gender: String) = _uiState.update { it.copy(gender = gender) }
    fun updateProfilePicture(uri: Uri?) = _uiState.update { it.copy(profilePictureUri = uri) }
    fun clearMessages() = _uiState.update { it.copy(error = null) }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val state = _uiState.value

            travelerProfileRepository.updateProfile(
                UpdateTravelerProfileRequest(namaLengkap = state.name.takeIf { it.isNotEmpty() })
            ).onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
                return@launch
            }

            if (state.gender.isNotEmpty()) {
                healthProfileRepository.updateProfile(
                    UpdateHealthProfileRequest(
                        tanggalLahir = ProfileFormatters.toApiDate(state.dob).takeIf { it.isNotEmpty() },
                        jenisKelamin = ProfileFormatters.toApiGender(state.gender).takeIf { it.isNotEmpty() },
                    )
                ).onFailure { e ->
                    if (BuildConfig.DEBUG) Log.w("ProfileViewModel", "Health profile update: ${e.message}")
                }
            }

            preferences.saveUserProfile(
                preferences.userProfile.first().copy(
                    name = state.name,
                    dob = state.dob,
                    country = state.country,
                    gender = ProfileFormatters.toDisplayGender(state.gender),
                )
            )

            originalState = state.copy(
                gender = ProfileFormatters.toDisplayGender(state.gender),
                isEditMode = false,
            )
            _uiState.update {
                it.copy(
                    gender = ProfileFormatters.toDisplayGender(state.gender),
                    isLoading = false,
                    isEditMode = false,
                )
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLoggedOut()
        }
    }
}
