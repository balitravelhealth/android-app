package com.visitbali.balitravelhealth.data.dto

import com.google.gson.annotations.SerializedName

data class HealthProfile(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("tanggal_lahir") val tanggalLahir: String?,
    @SerializedName("jenis_kelamin") val jenisKelamin: String?,
    @SerializedName("tinggi_cm") val tinggiCm: Float?,
    @SerializedName("berat_kg") val beratKg: Float?,
    @SerializedName("golongan_darah") val golonganDarah: String?,
    @SerializedName("riwayat_alergi") val riwayatAlergi: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
)

data class CreateHealthProfileRequest(
    @SerializedName("tanggal_lahir") val tanggalLahir: String? = null,
    @SerializedName("jenis_kelamin") val jenisKelamin: String? = null,
    @SerializedName("tinggi_cm") val tinggiCm: Float? = null,
    @SerializedName("berat_kg") val beratKg: Float? = null,
    @SerializedName("golongan_darah") val golonganDarah: String? = null,
    @SerializedName("riwayat_alergi") val riwayatAlergi: String? = null,
)

typealias UpdateHealthProfileRequest = CreateHealthProfileRequest
