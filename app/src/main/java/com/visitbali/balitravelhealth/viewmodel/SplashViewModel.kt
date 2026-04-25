package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
            if (user.isLoggedIn) {
                if (user.isRegistered) {
                    _startDestination.value = "home"
                } else {
                    _startDestination.value = "setup"
                }
            } else {
                _startDestination.value = "login"
            }
            
            // Ensure splash stays for at least 2 seconds
            delay(2000)
            _isLoading.value = false
        }
    }
}
