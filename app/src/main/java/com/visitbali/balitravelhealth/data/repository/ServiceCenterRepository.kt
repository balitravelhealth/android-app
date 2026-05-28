package com.visitbali.balitravelhealth.data.repository

import com.visitbali.balitravelhealth.data.dto.ExpertSymptom
import com.visitbali.balitravelhealth.data.dto.HealthResponse
import com.visitbali.balitravelhealth.data.dto.LocationClassificationResponse
import com.visitbali.balitravelhealth.data.dto.NearbyFacility
import com.visitbali.balitravelhealth.data.remote.BaliHealthApiService

class ServiceCenterRepository(
    private val api: BaliHealthApiService,
) {
    suspend fun health(): Result<HealthResponse> = runCatching {
        api.health()
    }

    suspend fun classifyLocation(lat: Double, lng: Double): Result<LocationClassificationResponse> = runCatching {
        api.classifyLocation(lat = lat, lng = lng)
    }

    suspend fun nearbyFacilities(
        lat: Double,
        lng: Double,
        radiusKm: Float = 15f,
        limit: Int = 6,
    ): Result<List<NearbyFacility>> = runCatching {
        api.getFacilitiesNearby(
            lat = lat,
            lng = lng,
            radiusKm = radiusKm,
            limit = limit,
        ).data
    }

    suspend fun expertSymptoms(kategori: String? = null): Result<List<ExpertSymptom>> = runCatching {
        api.getExpertSymptoms(kategori = kategori).data
    }
}
