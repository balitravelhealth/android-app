package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.BuildConfig
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.dto.CreateHealthProfileRequest
import com.visitbali.balitravelhealth.data.dto.CreateTravelerProfileRequest
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import com.visitbali.balitravelhealth.data.repository.HealthProfileRepository
import com.visitbali.balitravelhealth.data.repository.TravelerProfileRepository
import com.visitbali.balitravelhealth.data.util.ProfileFormatters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SetupViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = UserPreferences(application)
    private val travelerProfileRepository = TravelerProfileRepository(
        RetrofitClient.apiService,
        preferences,
    )
    private val healthProfileRepository = HealthProfileRepository(RetrofitClient.apiService)

    fun saveUserProfile(
        name: String,
        country: String,
        dob: String,
        gender: String,
        onComplete: () -> Unit,
    ) {
        viewModelScope.launch {
            val dobIso = ProfileFormatters.toApiDate(dob)
            val apiGender = ProfileFormatters.toApiGender(gender)

            // Save all fields locally first
            preferences.saveUserProfile(
                preferences.userProfile.first().copy(
                        name = name,
                        country = country,
                        dob = dob,
                        gender = ProfileFormatters.toDisplayGender(apiGender),
                        isLoggedIn = true,
                        isProfileComplete = true,
                )
            )

            // POST traveler profile
            travelerProfileRepository.createProfile(
                CreateTravelerProfileRequest(
                    namaLengkap = name,
                    tanggalLahir = dobIso.takeIf { it.isNotEmpty() },
                )
            ).onFailure { e ->
                if (BuildConfig.DEBUG) Log.e("SetupViewModel", "Failed to create traveler profile: ${e.message}")
            }

            // POST health profile (gender + dob)
            if (gender.isNotEmpty() || dobIso.isNotEmpty()) {
                healthProfileRepository.createProfile(
                    CreateHealthProfileRequest(
                        tanggalLahir = dobIso.takeIf { it.isNotEmpty() },
                        jenisKelamin = apiGender.takeIf { it.isNotEmpty() },
                    )
                ).onFailure { e ->
                    if (BuildConfig.DEBUG) Log.e("SetupViewModel", "Failed to create health profile: ${e.message}")
                }
            }

            onComplete()
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            preferences.clear()
            onComplete()
        }
    }

}
