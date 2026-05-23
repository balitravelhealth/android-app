package com.visitbali.balitravelhealth.data.remote

import com.visitbali.balitravelhealth.data.model.GuideItem
import com.visitbali.balitravelhealth.data.model.HealthcareFacility
import com.visitbali.balitravelhealth.data.model.LifeSupportItem
import retrofit2.http.GET

data class ContentListResponse<T>(
    val success: Boolean,
    val data: List<T> = emptyList(),
    val message: String? = null
)

interface ContentApiService {
    @GET("api/emergency-guides")
    suspend fun getEmergencyGuides(): ContentListResponse<GuideItem>

    @GET("api/basic-life-support")
    suspend fun getBasicLifeSupport(): ContentListResponse<LifeSupportItem>

    @GET("api/healthcare-facilities")
    suspend fun getHealthcareFacilities(): ContentListResponse<HealthcareFacility>
}
