package com.visitbali.balitravelhealth.data.dao

import androidx.room.*
import com.visitbali.balitravelhealth.data.model.FacilityType
import com.visitbali.balitravelhealth.data.model.HealthcareFacility
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthcareFacilityDao {

    // ── Basic queries ────────────────────────────────────────────────────────

    @Query("SELECT * FROM healthcare_facilities ORDER BY type, name")
    fun getAllFacilities(): Flow<List<HealthcareFacility>>

    @Query("SELECT * FROM healthcare_facilities WHERE id = :id")
    suspend fun getFacilityById(id: Int): HealthcareFacility?

    @Query("SELECT * FROM healthcare_facilities WHERE type = :type ORDER BY name")
    fun getFacilitiesByType(type: FacilityType): Flow<List<HealthcareFacility>>

    // ── Search ───────────────────────────────────────────────────────────────

    @Query("""
        SELECT * FROM healthcare_facilities 
        WHERE name LIKE '%' || :query || '%'
           OR officialName LIKE '%' || :query || '%'
           OR specialty LIKE '%' || :query || '%'
           OR address LIKE '%' || :query || '%'
        ORDER BY name
    """)
    fun searchFacilities(query: String): Flow<List<HealthcareFacility>>

    // ── Geo-based: returns all + calculate distance in app layer ─────────────
    // (SQLite has no native Haversine; calculate distance in ViewModel/Repository)
    @Query("SELECT * FROM healthcare_facilities")
    suspend fun getAllFacilitiesSnapshot(): List<HealthcareFacility>

    // ── Specialty filter ─────────────────────────────────────────────────────

    @Query("""
        SELECT * FROM healthcare_facilities
        WHERE specialty LIKE '%' || :keyword || '%'
        ORDER BY name
    """)
    fun getFacilitiesBySpecialty(keyword: String): Flow<List<HealthcareFacility>>

    // ── Insert ───────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(facilities: List<HealthcareFacility>)

    @Query("DELETE FROM healthcare_facilities")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(facilities: List<HealthcareFacility>) {
        clear()
        insertAll(facilities)
    }

    // ── Count ────────────────────────────────────────────────────────────────

    @Query("SELECT COUNT(*) FROM healthcare_facilities")
    suspend fun count(): Int
}
