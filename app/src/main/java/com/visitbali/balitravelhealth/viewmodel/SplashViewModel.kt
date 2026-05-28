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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow("login")
    val startDestination = _startDestination.asStateFlow()

    private val preferences = UserPreferences(application)
    private val contentSyncRepository: AppContentSyncRepository

    init {
        val db = AppDatabase.getDatabase(application)
        contentSyncRepository = AppContentSyncRepository(
            context = application,
            api = RetrofitClient.apiService,
            guideItemDao = db.guideItemDao(),
            nurseDao = db.nurseDao(),
            lifeSupportItemDao = db.lifeSupportItemDao(),
            healthcareFacilityDao = db.healthcareFacilityDao(),
        )

        viewModelScope.launch {
            // Trigger sync immediately for public data
            val syncJob = launch { contentSyncRepository.refreshIfConnected() }

            val accessToken = preferences.accessToken.first()
            val localProfile = preferences.userProfile.first()
            val isLoggedIn = localProfile.isLoggedIn

            if (accessToken.isNotEmpty() && isLoggedIn) {
                try {
                    // Validate session by fetching traveler profile
                    val remoteProfile = RetrofitClient.apiService.getTravelerProfile()
                    preferences.saveUserProfile(
                        localProfile.copy(
                            name = remoteProfile.namaLengkap,
                            dob = com.visitbali.balitravelhealth.data.util.ProfileFormatters
                                .toDisplayDate(remoteProfile.tanggalLahir)
                                .ifBlank { localProfile.dob },
                            isProfileComplete = true,
                        )
                    )
                    preferences.saveProfileComplete(true)
                    _startDestination.value = "home"
                } catch (e: CancellationException) {
                    throw e
                } catch (e: retrofit2.HttpException) {
                    if (e.code() == 401 || e.code() == 403) {
                        if (BuildConfig.DEBUG) Log.w("SplashVM", "Token invalid, redirecting to login")
                        preferences.clear()
                        _startDestination.value = "login"
                    } else if (e.code() == 404) {
                        _startDestination.value = if (localProfile.isProfileComplete) "home" else "setup"
                    } else {
                        if (BuildConfig.DEBUG) Log.w("SplashVM", "Server error (${e.code()}), falling back to local state")
                        _startDestination.value = if (localProfile.isProfileComplete) "home" else "setup"
                    }
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) Log.e("SplashVM", "Network error: ${e.message}")
                    _startDestination.value = if (localProfile.isProfileComplete) "home" else "setup"
                }
            } else {
                _startDestination.value = "login"
            }

            // Ensure sync completes before hiding splash if it's the first install or data is missing
            // But don't block too long for better UX
            try {
                withTimeout(3000) { syncJob.join() }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) Log.w("SplashVM", "Sync timed out or failed")
            }

            delay(1000)
            _isLoading.value = false
        }
    }
}
