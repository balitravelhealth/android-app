package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.api.SaveProfileRequest
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import com.visitbali.balitravelhealth.data.pref.UserProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SetupViewModel(application: Application) : AndroidViewModel(application) {
    private val userPreferences = UserPreferences(application)
    private val apiService = RetrofitClient.apiService

    fun saveUserProfile(name: String, country: String, dob: String, gender: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val currentEmail = userPreferences.userProfile.first().email
            val idToken      = userPreferences.idToken.first()

            val dobForServer = try {
                val input  = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                val output = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                output.format(input.parse(dob)!!)
            } catch (e: Exception) { dob }

            val profile = UserProfile(
                email = currentEmail, name = name,
                country = country, dob = dob,
                gender = gender, isLoggedIn = true, isRegistered = true
            )
            userPreferences.saveUserProfile(profile)

            try {
                val response = apiService.saveUserProfile(
                    request = SaveProfileRequest(
                        idToken  = idToken,
                        email    = currentEmail,
                        name     = name,
                        country  = country,
                        dob      = dobForServer,
                        gender   = gender
                    )
                )
                // Simpan session token dari response
                if (response.success && response.session_token != null) {
                    userPreferences.saveSessionToken(response.session_token)
                    Log.d("SetupViewModel", "Session token saved")
                }
            } catch (e: Exception) {
                Log.e("SetupViewModel", "Failed to sync: ${e.message}")
            }

            onComplete()
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clear()
            onComplete()
        }
    }
}
