package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.api.SaveProfileRequest
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import com.visitbali.balitravelhealth.data.pref.UserProfile
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "",
    val dob: String = "",
    val country: String = "",
    val gender: String = "",
    val profilePictureUri: Uri? = null,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userPreferences = UserPreferences(application)
    private val apiService = RetrofitClient.apiService

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var originalProfile: UserProfile? = null

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            userPreferences.userProfile.first().let { profile ->
                originalProfile = profile
                _uiState.update { 
                    it.copy(
                        name = profile.name,
                        dob = profile.dob,
                        country = profile.country,
                        gender = profile.gender
                    )
                }
            }
        }
    }

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditMode = !it.isEditMode) }
    }

    fun cancelEdit() {
        originalProfile?.let { profile ->
            _uiState.update { 
                it.copy(
                    name = profile.name,
                    dob = profile.dob,
                    country = profile.country,
                    gender = profile.gender,
                    isEditMode = false,
                    error = null
                )
            }
        }
    }

    fun updateName(name: String) { _uiState.update { it.copy(name = name) } }
    fun updateDob(dob: String) { _uiState.update { it.copy(dob = dob) } }
    fun updateCountry(country: String) { _uiState.update { it.copy(country = country) } }
    fun updateGender(gender: String) { _uiState.update { it.copy(gender = gender) } }
    fun updateProfilePicture(uri: Uri?) { _uiState.update { it.copy(profilePictureUri = uri) } }
    fun clearMessages() { _uiState.update { it.copy(error = null) } }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clear()
            onLoggedOut()
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val currentState = _uiState.value
                val idToken = userPreferences.idToken.first()
                val email = userPreferences.userProfile.first().email

                val response = apiService.saveUserProfile(
                    SaveProfileRequest(
                        idToken = idToken,
                        email = email,
                        name = currentState.name,
                        country = currentState.country,
                        dob = currentState.dob,
                        gender = currentState.gender
                    )
                )

                if (response.success) {
                    val updatedProfile = originalProfile?.copy(
                        name = currentState.name,
                        country = currentState.country,
                        dob = currentState.dob,
                        gender = currentState.gender
                    ) ?: return@launch

                    userPreferences.saveUserProfile(updatedProfile)
                    originalProfile = updatedProfile
                    _uiState.update { it.copy(isLoading = false, isEditMode = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
