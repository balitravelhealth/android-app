package com.visitbali.balitravelhealth.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "life_support_items")
data class LifeSupportItem(
    @PrimaryKey
    val id: Int,
    val title: String,
    val kategori: String,
    val deskripsi: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
    val sortOrder: Int = 0
)
