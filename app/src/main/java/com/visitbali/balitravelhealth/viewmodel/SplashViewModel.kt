package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.BuildConfig
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.database.AppDatabase
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import com.visitbali.balitravelhealth.data.repository.AppContentSyncRepository
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
    private val contentSyncRepository: AppContentSyncRepository

    init {
        val db = AppDatabase.getDatabase(application)
        contentSyncRepository = AppContentSyncRepository(
            context = application,
            contentApi = RetrofitClient.contentApiService,
            nurseApi = RetrofitClient.nurseApiService,
            healthcareFacilityDao = db.healthcareFacilityDao(),
            guideItemDao = db.guideItemDao(),
            lifeSupportItemDao = db.lifeSupportItemDao(),
            nurseDao = db.nurseDao()
        )

        viewModelScope.launch {
            contentSyncRepository.refreshIfConnected()

            val user = userPreferences.userProfile.first()
            val sessionToken = userPreferences.sessionToken.first()

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
                    if (BuildConfig.DEBUG) Log.w("SplashSync", "Token expired (${e.code()}). Falling back to local state.")
                    _startDestination.value = if (user.isLoggedIn && user.isRegistered) "home" else "login"
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) Log.e("SplashSync", "Network error: ${e.message}")
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
            val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
            if (webClientId.isBlank()) {
                if (BuildConfig.DEBUG) Log.e("SplashSync", "GOOGLE_WEBCLIENT is missing from local.properties")
                return ""
            }

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
                if (BuildConfig.DEBUG) Log.d("SplashSync", "Token refreshed successfully")
            }
            newToken
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) Log.e("SplashSync", "Silent token refresh failed: ${e.message}")
            ""
        }
    }
}
