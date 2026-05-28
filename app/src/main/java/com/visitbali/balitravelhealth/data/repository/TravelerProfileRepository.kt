package com.visitbali.balitravelhealth.data.repository

import com.visitbali.balitravelhealth.data.dto.CreateTravelerProfileRequest
import com.visitbali.balitravelhealth.data.dto.TravelerProfile
import com.visitbali.balitravelhealth.data.dto.UpdateTravelerProfileRequest
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import com.visitbali.balitravelhealth.data.remote.BaliHealthApiService
import com.visitbali.balitravelhealth.data.util.ProfileFormatters
import kotlinx.coroutines.flow.first

class TravelerProfileRepository(
    private val api: BaliHealthApiService,
    private val preferences: UserPreferences,
) {
    suspend fun getProfile(): Result<TravelerProfile> = runCatching {
        api.getTravelerProfile().also { profile ->
            val current = preferences.userProfile.first()
            preferences.saveUserProfile(
                current.copy(
                    name = profile.namaLengkap,
                    dob = ProfileFormatters.toDisplayDate(profile.tanggalLahir).ifBlank { current.dob },
                    isLoggedIn = true,
                    isProfileComplete = true,
                )
            )
        }
    }

    suspend fun createProfile(request: CreateTravelerProfileRequest): Result<TravelerProfile> = runCatching {
        val profile = api.createTravelerProfile(request)
        preferences.saveName(profile.namaLengkap)
        preferences.saveProfileComplete(true)
        profile
    }

    suspend fun updateProfile(request: UpdateTravelerProfileRequest): Result<TravelerProfile> = runCatching {
        val profile = api.updateTravelerProfile(request)
        request.namaLengkap?.let { preferences.saveName(it) }
        profile
    }
}
