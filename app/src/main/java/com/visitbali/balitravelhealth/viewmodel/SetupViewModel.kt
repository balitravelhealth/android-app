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
            val idToken = userPreferences.idToken.first()
            Log.d("SetupViewModel", "idToken length: ${idToken.length}")
            val profile = UserProfile(
                email = currentEmail,
                name = name,
                country = country,
                dob = dob,
                gender = gender,
                isLoggedIn = true,
                isRegistered = true
            )
            
            // Save locally
            userPreferences.saveUserProfile(profile)
            
            // Sync to server
            try {
                apiService.saveUserProfile(
                    authorization = "Bearer $idToken",
                    request = SaveProfileRequest(
                    idToken = idToken,
                    email = currentEmail,
                    name = name,
                    country = country,
                    dob = dob,
                    gender = gender
                    )
                )
            } catch (e: Exception) {
                Log.e("SetupViewModel", "Failed to sync profile to server: ${e.message}")
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
