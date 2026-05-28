package com.visitbali.balitravelhealth.data.dto

import com.google.gson.annotations.SerializedName

data class Destination(
    val id: Int,
    @SerializedName("nama_daerah") val namaDaerah: String,
    @SerializedName("created_at") val createdAt: String,
)

data class DestinationsResponse(
    val data: List<Destination> = emptyList(),
)

data class HealthRisk(
    val id: Int,
    @SerializedName("destination_id") val destinationId: Int,
    @SerializedName("nama_risiko") val namaRisiko: String,
    @SerializedName("saran_pencegahan") val saranPencegahan: String,
    @SerializedName("rekomendasi_vaksinasi") val rekomendasiVaksinasi: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
)

data class HealthRisksResponse(
    val data: List<HealthRisk> = emptyList(),
)
