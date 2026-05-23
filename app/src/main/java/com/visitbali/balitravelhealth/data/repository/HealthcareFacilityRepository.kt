package com.visitbali.balitravelhealth.data.repository

import com.visitbali.balitravelhealth.data.dao.HealthcareFacilityDao
import com.visitbali.balitravelhealth.data.model.FacilityType
import com.visitbali.balitravelhealth.data.model.HealthcareFacility
import kotlinx.coroutines.flow.Flow
import kotlin.math.*

class HealthcareFacilityRepository(
    private val dao: HealthcareFacilityDao
) {

    val allFacilities: Flow<List<HealthcareFacility>> = dao.getAllFacilities()

    fun getByType(type: FacilityType): Flow<List<HealthcareFacility>> =
        dao.getFacilitiesByType(type)

    fun search(query: String): Flow<List<HealthcareFacility>> =
        dao.searchFacilities(query.trim())

    fun getBySpecialty(keyword: String): Flow<List<HealthcareFacility>> =
        dao.getFacilitiesBySpecialty(keyword)

    suspend fun getFacilityById(id: Int): HealthcareFacility? =
        dao.getFacilityById(id)

    /**
     * Returns all facilities sorted by distance from [userLat]/[userLng].
     * Distance is calculated using the Haversine formula (in km).
     */
    suspend fun getNearestFacilities(
        userLat: Double,
        userLng: Double,
        maxDistanceKm: Double = Double.MAX_VALUE,
        type: FacilityType? = null
    ): List<Pair<HealthcareFacility, Double>> {
        val all = dao.getAllFacilitiesSnapshot()
        return all
            .filter { type == null || it.type == type }
            .map { facility ->
                val dist = haversineKm(userLat, userLng, facility.latitude, facility.longitude)
                facility to dist
            }
            .filter { (_, dist) -> dist <= maxDistanceKm }
            .sortedBy { (_, dist) -> dist }
    }

    suspend fun count(): Int = dao.count()

    // ── Haversine formula ──────────────────────────────────────────────────

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}
