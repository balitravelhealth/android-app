package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.visitbali.balitravelhealth.BuildConfig
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import com.visitbali.balitravelhealth.data.repository.AuthRepository
import com.visitbali.balitravelhealth.data.repository.TravelerProfileRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val displayName: String, val isNewUser: Boolean) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val preferences = UserPreferences(application)
    private val authRepository = AuthRepository(RetrofitClient.apiService, preferences)
    private val travelerProfileRepository = TravelerProfileRepository(RetrofitClient.apiService, preferences)

    fun getGoogleSignInIntent(context: Context): Intent {
        _uiState.value = LoginUiState.Loading
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("779721266536-an0g84psqn5b7fc8name2qbgajtpgih3.apps.googleusercontent.com")
            .build()
        return GoogleSignIn.getClient(context, options).signInIntent
    }

    fun onGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            try {
                _uiState.value = LoginUiState.Loading
                val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                    .getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken.isNullOrBlank()) {
                    _uiState.value = LoginUiState.Error("Google sign-in did not return an ID token.")
                    return@launch
                }

                val displayName = account.displayName ?: "User"
                val photoUrl = account.photoUrl?.toString()

                // Save Google ID token and photo locally before server call
                preferences.saveIdToken(idToken)
                photoUrl?.let {
                    preferences.saveUserProfile(
                        preferences.userProfile.first().copy(
                            name = displayName,
                            photoUrl = it,
                        )
                    )
                }

                // Authenticate with our backend
                authRepository.loginWithGoogle(idToken)
                    .onSuccess {
                        preferences.saveName(displayName)
                        // Check if traveler profile exists
                        val profileResult = travelerProfileRepository.getProfile()
                        val localProfile = preferences.userProfile.first()
                        val profileError = profileResult.exceptionOrNull()
                        val isNewUser = when {
                            profileResult.isSuccess -> false
                            (profileError as? HttpException)?.code() == 404 -> true
                            else -> !localProfile.isProfileComplete
                        }
                        _uiState.value = LoginUiState.Success(displayName, isNewUser = isNewUser)
                    }
                    .onFailure { e ->
                        if (BuildConfig.DEBUG) Log.e("LoginViewModel", "Backend auth failed", e)
                        _uiState.value = LoginUiState.Error("Authentication failed: ${e.localizedMessage ?: "Unknown error"}")
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (e: ApiException) {
                if (e.statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    _uiState.value = LoginUiState.Idle
                } else {
                    if (BuildConfig.DEBUG) Log.e("LoginViewModel", "Google sign-in failed", e)
                    _uiState.value = LoginUiState.Error("Google sign-in failed. Please try again.")
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) Log.e("LoginViewModel", "Error parsing credential", e)
                _uiState.value = LoginUiState.Error("Failed to parse login information")
            }
        }
    }
}
