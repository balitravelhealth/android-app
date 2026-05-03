package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow("login")
    val startDestination = _startDestination.asStateFlow()

    private val userPreferences = UserPreferences(application)

    init {
        viewModelScope.launch {
            val user = userPreferences.userProfile.first()
            val sessionToken = userPreferences.sessionToken.first()  // pakai stored token langsung

            if (user.email.isNotEmpty() && sessionToken.isNotEmpty()) {
                try {
                    val response = RetrofitClient.apiService.getUserProfile(
                        authorization = "Bearer $sessionToken",
                        email = user.email
                    )

                    if (response.success && response.data != null) {
                        userPreferences.saveUserProfile(response.data.copy(isLoggedIn = true, isRegistered = true))
                        _startDestination.value = "home"
                    } else {
                        userPreferences.clear()
                        _startDestination.value = "login"
                    }
                } catch (e: retrofit2.HttpException) {
                    if (e.code() == 401) {
                        // Token expired → minta login ulang, tapi jangan hapus data lokal
                        Log.w("SplashSync", "Token expired. Need re-login.")
                        _startDestination.value = if (user.isLoggedIn && user.isRegistered) "home" else "login"
                    } else {
                        // Error teknis → fallback local
                        _startDestination.value = if (user.isLoggedIn && user.isRegistered) "home" else "login"
                    }
                } catch (e: Exception) {
                    // Network error → fallback local
                    Log.e("SplashSync", "Network error: ${e.message}")
                    _startDestination.value = if (user.isLoggedIn && user.isRegistered) "home" else "login"
                }
            } else {
                _startDestination.value = "login"
            }

            delay(1500)
            _isLoading.value = false
        }
    }
    private suspend fun refreshIdToken(): String {
        return try {
            val credentialManager = androidx.credentials.CredentialManager.create(getApplication())
            val webClientId = "779721266536-an0g84psqn5b7fc8name2qbgajtpgih3.apps.googleusercontent.com"

            val googleIdOption = com.google.android.libraries.identity.googleid.GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(true)
                .build()

            val request = androidx.credentials.GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = getApplication(),
                request = request
            )
            val credential = result.credential

            val newToken = when {
                credential is com.google.android.libraries.identity.googleid.GoogleIdTokenCredential ->
                    credential.idToken
                credential.type == com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL ->
                    com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
                        .createFrom(credential.data).idToken
                else -> ""
            }

            if (newToken.isNotEmpty()) {
                userPreferences.saveIdToken(newToken)
                Log.d("SplashSync", "Token refreshed successfully")
            }
            newToken
        } catch (e: Exception) {
            Log.e("SplashSync", "Silent token refresh failed: ${e.message}")
            ""
        }
    }
}
