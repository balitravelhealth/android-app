package com.visitbali.balitravelhealth.data.dto

import com.google.gson.annotations.SerializedName

data class LocationClassificationResponse(
    val zone: String, // "urban", "rural", "remote"
    val label: String,
    @SerializedName("nearest_facility_km") val nearestFacilityKm: Float,
)

data class NearbyFacility(
    val id: Int,
    val nama: String,
    val jenis: String,
    val lat: Double,
    val lng: Double,
    @SerializedName("jarak_km") val jarakKm: Float,
    val telepon: String?,
)

data class NearbyFacilitiesResponse(
    val data: List<NearbyFacility> = emptyList(),
)

data class HealthcareFacilitiesResponse(
    val data: List<com.visitbali.balitravelhealth.data.model.HealthcareFacility> = emptyList(),
)
