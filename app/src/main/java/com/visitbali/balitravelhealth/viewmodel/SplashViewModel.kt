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
            val idToken = userPreferences.idToken.first()
            Log.d("SplashSync", "Local Email: ${user.email}, Local isRegistered: ${user.isRegistered}")
            
            if (user.email.isNotEmpty()) {
                try {
                    Log.d("SplashSync", "Checking server for: ${user.email}")
                    val response = RetrofitClient.apiService.getUserProfile(
                        authorization = "Bearer $idToken",
                        email = user.email)
                    Log.d("SplashSync", "Server Response: success=${response.success}, dataFound=${response.data != null}")
                    
                    if (response.success && response.data != null) {
                        Log.d("SplashSync", "User found on server. Syncing and going HOME.")
                        userPreferences.saveUserProfile(response.data.copy(isLoggedIn = true, isRegistered = true))
                        _startDestination.value = "home"
                    } else {
                        Log.d("SplashSync", "User NOT found on server (success=false). Clearing local data.")
                        userPreferences.clear()
                        _startDestination.value = "login"
                    }
                } catch (e: retrofit2.HttpException) {
                    if (e.code() == 401) {
                        Log.w("SplashSync", "Server returned 401 Unauthorized. User not in DB. Clearing data.")
                        userPreferences.clear()
                        _startDestination.value = "login"
                    } else {
                        Log.e("SplashSync", "HTTP Error ${e.code()}. Redirecting to login.")
                        _startDestination.value = "login"
                    }
                } catch (e: Exception) {
                    Log.e("SplashSync", "Server check failed: ${e.message}")
                    // Fallback to local state ONLY if user was previously fully registered
                    if (user.isLoggedIn && user.isRegistered) {
                        Log.d("SplashSync", "Network error. Falling back to local HOME state.")
                        _startDestination.value = "home"
                    } else {
                        Log.d("SplashSync", "Network error/Unregistered. Going LOGIN.")
                        _startDestination.value = "login"
                    }
                }
            } else {
                Log.d("SplashSync", "No local email found. Going LOGIN.")
                _startDestination.value = "login"
            }
            
            delay(1500)
            _isLoading.value = false
        }
    }
}
