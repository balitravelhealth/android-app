package com.visitbali.balitravelhealth.data.dto

import com.google.gson.annotations.SerializedName

data class Vaccination(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("jenis_vaksin") val jenisVaksin: String,
    val tanggal: String,
    val dosis: String?,
    val catatan: String?,
    @SerializedName("created_at") val createdAt: String,
)

data class CreateVaccinationRequest(
    @SerializedName("jenis_vaksin") val jenisVaksin: String,
    val tanggal: String,
    val dosis: String? = null,
    val catatan: String? = null,
)

data class VaccinationsResponse(
    val data: List<Vaccination> = emptyList(),
)

data class VaccinationResponse(
    val data: Vaccination? = null,
    val message: String? = null,
)
