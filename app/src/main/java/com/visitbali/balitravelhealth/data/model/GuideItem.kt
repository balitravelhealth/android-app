package com.visitbali.balitravelhealth.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "guide_items")
data class GuideItem(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("sort_order")
    val sortOrder: Int = 0
)
