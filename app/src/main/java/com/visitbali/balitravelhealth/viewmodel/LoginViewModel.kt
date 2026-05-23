package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.visitbali.balitravelhealth.BuildConfig
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import com.visitbali.balitravelhealth.data.pref.UserProfile
import com.visitbali.balitravelhealth.data.api.SaveProfileRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val userPreferences = UserPreferences(application)
    private val apiService = RetrofitClient.apiService

    fun onGoogleSignInClick(context: Context) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            val credentialManager = CredentialManager.create(context)
            val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                handleSignInResult(result)
            } catch (e: GetCredentialException) {
                if (BuildConfig.DEBUG) Log.e("LoginViewModel", "Credential Manager error: ${e.message}", e)
                _uiState.value = LoginUiState.Error(formatGoogleSignInError(e))
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) Log.e("LoginViewModel", "Unexpected error during sign-in", e)
                _uiState.value = LoginUiState.Error("An unexpected error occurred")
            }
        }
    }

    private fun handleSignInResult(result: GetCredentialResponse) {
        val credential = result.credential

        viewModelScope.launch {
            try {
                var userEmail = ""
                var displayName = "User"
                var idToken = ""
                var photoUrl: String? = null

                if (credential is GoogleIdTokenCredential) {
                    userEmail = credential.id
                    displayName = credential.displayName ?: "User"
                    idToken = credential.idToken
                    photoUrl = credential.profilePictureUri?.toString()
                } else if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    userEmail = googleIdTokenCredential.id
                    displayName = googleIdTokenCredential.displayName ?: "User"
                    idToken = googleIdTokenCredential.idToken
                    photoUrl = googleIdTokenCredential.profilePictureUri?.toString()
                }

                if (userEmail.isNotEmpty()) {
                    userPreferences.saveEmail(userEmail)
                    userPreferences.saveIdToken(idToken)
                    userPreferences.saveLoginStatus(true)
                    photoUrl?.let {
                        userPreferences.saveUserProfile(UserProfile(
                            email = userEmail, name = displayName, country = "", dob = "",
                            gender = "", photoUrl = it, isLoggedIn = true, isRegistered = false
                        ))
                    }

                    try {
                        val response = apiService.saveUserProfile(
                            request = SaveProfileRequest(
                                idToken  = idToken,
                                email    = userEmail,
                                name     = displayName,
                                country  = "",
                                dob      = "",
                                gender   = ""
                            )
                        )

                        if (response.success && response.session_token != null) {
                            userPreferences.saveSessionToken(response.session_token)
                        }

                        if (response.success && response.data != null &&
                            response.data.country.isNotEmpty() &&
                            response.data.gender.isNotEmpty()) {
                            userPreferences.saveUserProfile(
                                response.data.copy(isLoggedIn = true, isRegistered = true, photoUrl = photoUrl)
                            )
                            _uiState.value = LoginUiState.Success(displayName, isNewUser = false)
                        } else {
                            userPreferences.saveUserProfile(UserProfile(
                                email = userEmail, name = displayName,
                                country = "", dob = "", gender = "", photoUrl = photoUrl,
                                isLoggedIn = true, isRegistered = false
                            ))
                            _uiState.value = LoginUiState.Success(displayName, isNewUser = true)
                        }
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) Log.e("LoginViewModel", "Server sync error", e)
                        val currentUser = userPreferences.userProfile.first()
                        _uiState.value = LoginUiState.Success(displayName, isNewUser = !currentUser.isRegistered)
                    }
                } else {
                    _uiState.value = LoginUiState.Error("Failed to get user information")
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) Log.e("LoginViewModel", "Error parsing Google ID Token credential", e)
                _uiState.value = LoginUiState.Error("Failed to parse login information")
            }
        }
    }

    private fun formatGoogleSignInError(e: GetCredentialException): String {
        return when (e.type) {
            "androidx.credentials.TYPE_GET_CREDENTIAL_CANCELED_EXCEPTION" -> "Sign-in was cancelled"
            else -> e.message ?: "Sign-in failed"
        }
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val displayName: String, val isNewUser: Boolean) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
