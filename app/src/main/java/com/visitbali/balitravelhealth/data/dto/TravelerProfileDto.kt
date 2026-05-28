package com.visitbali.balitravelhealth.data.dto

import com.google.gson.annotations.SerializedName

data class TravelerProfile(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("nama_lengkap") val namaLengkap: String,
    @SerializedName("tanggal_lahir") val tanggalLahir: String?,
    @SerializedName("kontak_darurat") val kontakDarurat: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
)

data class CreateTravelerProfileRequest(
    @SerializedName("nama_lengkap") val namaLengkap: String,
    @SerializedName("tanggal_lahir") val tanggalLahir: String? = null,
    @SerializedName("kontak_darurat") val kontakDarurat: String? = null,
)

data class UpdateTravelerProfileRequest(
    @SerializedName("nama_lengkap") val namaLengkap: String? = null,
    @SerializedName("tanggal_lahir") val tanggalLahir: String? = null,
    @SerializedName("kontak_darurat") val kontakDarurat: String? = null,
)
