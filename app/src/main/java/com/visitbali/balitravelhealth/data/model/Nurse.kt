package com.visitbali.balitravelhealth.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "nurses")
data class Nurse(
    @PrimaryKey
    val id: Int,
    val nama: String,
    val spesialisasi: String? = null,
    @SerializedName("is_active")
    val isActive: Boolean = true,
)
