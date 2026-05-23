package com.visitbali.balitravelhealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.model.FacilityType
import com.visitbali.balitravelhealth.data.model.HealthcareFacility
import com.visitbali.balitravelhealth.data.repository.HealthcareFacilityRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ── UI State ──────────────────────────────────────────────────────────────────

data class FacilityUiState(
    val facilities: List<FacilityWithDistance> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val activeFilter: FacilityType? = null,
    val searchQuery: String = "",
    val userLocation: UserLocation? = null,
    val sortedByDistance: Boolean = false
)

data class FacilityWithDistance(
    val facility: HealthcareFacility,
    val distanceKm: Double? = null
)

data class UserLocation(val lat: Double, val lng: Double)

// ── ViewModel ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class HealthcareFacilityViewModel(
    private val repository: HealthcareFacilityRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _activeFilter = MutableStateFlow<FacilityType?>(null)
    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    private val _sortByDistance = MutableStateFlow(false)

    val uiState: StateFlow<FacilityUiState> = combine(
        _searchQuery,
        _activeFilter,
        _userLocation,
        _sortByDistance,
        buildFacilityFlow()
    ) { query, filter, location, sortByDist, facilities ->

        var result = facilities

        // Apply type filter
        if (filter != null) {
            result = result.filter { it.facility.type == filter }
        }

        // Apply search
        if (query.isNotBlank()) {
            val q = query.lowercase()
            result = result.filter {
                it.facility.name.lowercase().contains(q) ||
                it.facility.specialty.lowercase().contains(q) ||
                it.facility.address.lowercase().contains(q) ||
                (it.facility.notes?.lowercase()?.contains(q) == true)
            }
        }

        // Apply distance sort if location is available
        if (sortByDist && location != null) {
            result = result.sortedBy { it.distanceKm ?: Double.MAX_VALUE }
        }

        FacilityUiState(
            facilities = result,
            isLoading = false,
            activeFilter = filter,
            searchQuery = query,
            userLocation = location,
            sortedByDistance = sortByDist
        )
    }.catch { e ->
        emit(FacilityUiState(isLoading = false, errorMessage = e.message))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FacilityUiState(isLoading = true)
    )

    // ── Private helpers ──────────────────────────────────────────────────────

    private fun buildFacilityFlow(): Flow<List<FacilityWithDistance>> {
        return combine(
            repository.allFacilities,
            _userLocation
        ) { facilities, location ->
            facilities.map { facility ->
                val dist = if (location != null) {
                    haversineKm(location.lat, location.lng, facility.latitude, facility.longitude)
                } else null
                FacilityWithDistance(facility, dist)
            }
        }
    }

    // ── Public actions ───────────────────────────────────────────────────────

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setTypeFilter(type: FacilityType?) {
        _activeFilter.value = type
    }

    fun updateUserLocation(lat: Double, lng: Double) {
        _userLocation.value = UserLocation(lat, lng)
        _sortByDistance.value = true
    }

    fun clearUserLocation() {
        _userLocation.value = null
        _sortByDistance.value = false
    }

    fun toggleSortByDistance() {
        _sortByDistance.value = !_sortByDistance.value
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun clearFilter() {
        _activeFilter.value = null
    }

    /**
     * Get nearby facilities within [radiusKm] of a given location.
     * Result delivered as a one-shot list (suspend).
     */
    fun getNearby(
        lat: Double,
        lng: Double,
        radiusKm: Double = 10.0,
        type: FacilityType? = null,
        onResult: (List<FacilityWithDistance>) -> Unit
    ) {
        viewModelScope.launch {
            val results = repository.getNearestFacilities(lat, lng, radiusKm, type)
            onResult(results.map { (f, d) -> FacilityWithDistance(f, d) })
        }
    }

    // ── Haversine ─────────────────────────────────────────────────────────────

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2).let { it * it } +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2).let { it * it }
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    class Factory(private val repository: HealthcareFacilityRepository) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HealthcareFacilityViewModel::class.java)) {
                return HealthcareFacilityViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
