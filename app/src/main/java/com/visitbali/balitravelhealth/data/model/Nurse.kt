package com.visitbali.balitravelhealth.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

@Entity(tableName = "nurses")
data class Nurse(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("years_of_experience")
    val yearsOfExperience: Int,

    @SerializedName("rate_per_appointment")
    val ratePerAppointment: BigDecimal,

    @SerializedName("specialization")
    val specialization: String? = null,

    @SerializedName("bio")
    val bio: String? = null,

    @SerializedName("profile_photo_url")
    val profilePhotoUrl: String? = null,

    @SerializedName("is_active")
    val isActive: Boolean = true,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,
)
