package com.visitbali.balitravelhealth.data.repository

import com.visitbali.balitravelhealth.data.dto.CreateHealthProfileRequest
import com.visitbali.balitravelhealth.data.dto.HealthProfile
import com.visitbali.balitravelhealth.data.dto.UpdateHealthProfileRequest
import com.visitbali.balitravelhealth.data.remote.BaliHealthApiService

class HealthProfileRepository(private val api: BaliHealthApiService) {

    suspend fun getProfile(): Result<HealthProfile> = runCatching {
        api.getHealthProfile()
    }

    suspend fun createProfile(request: CreateHealthProfileRequest): Result<HealthProfile> = runCatching {
        api.createHealthProfile(request)
    }

    suspend fun updateProfile(request: UpdateHealthProfileRequest): Result<HealthProfile> = runCatching {
        api.updateHealthProfile(request)
    }
}
