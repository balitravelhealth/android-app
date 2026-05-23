package com.visitbali.balitravelhealth.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a BMTA-listed healthcare facility in Bali.
 *
 * Data sources: official hospital websites, Australian Embassy Bali Medical List,
 * US Embassy Medical Assistance Indonesia, SIRS Kemkes, verified third-party directories.
 *
 * NOTE: Coordinates are approximate (~100–200 m accuracy).
 * Verify with Google Maps API before production use.
 *
 * Schema version: 2 — added operating hours fields
 */
@Entity(tableName = "healthcare_facilities")
data class HealthcareFacility(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val officialName: String,
    val specialty: String,
    val type: FacilityType,
    val address: String,
    val phone: String,
    val phoneAlt: String? = null,
    val website: String? = null,
    val email: String? = null,
    val latitude: Double,
    val longitude: Double,

    // ── Operating Hours ───────────────────────────────────────────────────────

    /**
     * True jika unit IGD/Emergency beroperasi 24 jam 7 hari.
     * Polikinik bisa tetap memiliki jam terbatas meski isOpen24Hours = true.
     */
    val isOpen24Hours: Boolean = false,

    /**
     * Jam rawat jalan / poliklinik dalam format human-readable.
     * Gunakan \n untuk memisahkan beberapa baris jadwal.
     *
     * Contoh:
     *   "Senin–Kamis: 07.30–16.00 WITA\nJumat: 07.30–13.00 WITA\nSabtu: 07.30–12.00 WITA (perjanjian)"
     *
     * Prefix "⚠️ " jika jam diestimasi / tidak dikonfirmasi resmi.
     */
    val outpatientHours: String? = null,

    /**
     * Jam IGD / Emergency.
     * Biasanya "24 jam / 7 hari" untuk RS; null untuk klinik tanpa IGD.
     */
    val emergencyHours: String? = null,

    /**
     * Ringkasan satu baris untuk ditampilkan di list card.
     * Contoh: "IGD 24 jam  |  Poli: Sen–Jum 07.30–16.00"
     */
    val hoursSummary: String? = null,

    val notes: String? = null
)

enum class FacilityType {
    GOVERNMENT,
    PRIVATE,
    CLINIC
}
