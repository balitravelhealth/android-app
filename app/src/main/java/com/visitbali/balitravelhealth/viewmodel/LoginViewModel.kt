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
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import com.visitbali.balitravelhealth.data.pref.UserProfile
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
            Log.d("LoginViewModel", "Starting Google Sign-In process")
            _uiState.value = LoginUiState.Loading
            
            val credentialManager = CredentialManager.create(context)
            
            // TODO: Replace with your actual Web Client ID from Google Cloud Console
            val webClientId = "YOUR_GOOGLE_OAUTH_CLIENT_ID"
            
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                Log.d("LoginViewModel", "Requesting credentials...")
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                Log.d("LoginViewModel", "Credentials received successfully")
                handleSignInResult(result)
            } catch (e: GetCredentialException) {
                Log.e("LoginViewModel", "Credential Manager error: ${e.message}", e)
                _uiState.value = LoginUiState.Error(e.message ?: "Sign-in failed")
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Unexpected error during sign-in", e)
                _uiState.value = LoginUiState.Error("An unexpected error occurred")
            }
        }
    }

    private fun handleSignInResult(result: GetCredentialResponse) {
        val credential = result.credential
        Log.d("LoginViewModel", "Received credential type: ${credential::class.java.name}")
        
        viewModelScope.launch {
            try {
                var userEmail = ""
                var displayName = "User"
                var idToken = ""

                if (credential is GoogleIdTokenCredential) {
                    userEmail = credential.id // id is usually the email in GoogleIdTokenCredential
                    displayName = credential.displayName ?: "User"
                    idToken = credential.idToken
                } else if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    userEmail = googleIdTokenCredential.id
                    displayName = googleIdTokenCredential.displayName ?: "User"
                    idToken = googleIdTokenCredential.idToken
                }

                if (userEmail.isNotEmpty()) {
                    userPreferences.saveEmail(userEmail)
                    userPreferences.saveIdToken(idToken)
                    userPreferences.saveLoginStatus(true)

                    // Check with server if user exists
                    try {
                        val response = apiService.getUserProfile(
                            authorization = "Bearer $idToken",
                            email = userEmail)
                        if (response.success && response.data != null) {
                            // User exists on server, sync to local
                            userPreferences.saveUserProfile(response.data.copy(isLoggedIn = true, isRegistered = true))
                            _uiState.value = LoginUiState.Success(displayName, isNewUser = false)
                        } else {
                            // User doesn't exist on server, ensure local state matches
                            userPreferences.saveUserProfile(UserProfile(
                                email = userEmail,
                                name = "",
                                country = "",
                                dob = "",
                                gender = "",
                                isLoggedIn = true,
                                isRegistered = false
                            ))
                            _uiState.value = LoginUiState.Success(displayName, isNewUser = true)
                        }
                    } catch (e: retrofit2.HttpException) {
                        if (e.code() == 401) {
                            Log.d("LoginViewModel", "Server returned 401: User not found or unauthorized. Treating as new user.")
                            userPreferences.saveUserProfile(UserProfile(
                                email = userEmail,
                                name = "",
                                country = "",
                                dob = "",
                                gender = "",
                                isLoggedIn = true,
                                isRegistered = false
                            ))
                            _uiState.value = LoginUiState.Success(displayName, isNewUser = true)
                        } else {
                            Log.e("LoginViewModel", "HTTP Error ${e.code()}", e)
                            val currentUser = userPreferences.userProfile.first()
                            _uiState.value = LoginUiState.Success(displayName, isNewUser = !currentUser.isRegistered)
                        }
                    } catch (e: Exception) {
                        Log.e("LoginViewModel", "Server sync error", e)
                        // Fallback to local check if server is down
                        val currentUser = userPreferences.userProfile.first()
                        _uiState.value = LoginUiState.Success(displayName, isNewUser = !currentUser.isRegistered)
                    }
                } else {
                    _uiState.value = LoginUiState.Error("Failed to get user information")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error parsing Google ID Token credential", e)
                _uiState.value = LoginUiState.Error("Failed to parse login information")
            }
        }
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val displayName: String, val isNewUser: Boolean) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
