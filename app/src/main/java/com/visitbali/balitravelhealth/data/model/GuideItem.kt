package com.visitbali.balitravelhealth.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guide_items")
data class GuideItem(
    @PrimaryKey
    val id: Int,
    val kategori: String,
    val langkah: Int,
    val isiMediaTeks: String? = null,
    val isiMediaGambarUrl: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
)
