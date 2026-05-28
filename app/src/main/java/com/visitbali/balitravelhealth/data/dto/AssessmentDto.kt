package com.visitbali.balitravelhealth.data.dto

import com.google.gson.annotations.SerializedName

data class AssessmentRequest(
    val symptoms: List<Int>,
    val kategori: String, // "pre_travel" or "post_travel"
)

data class AssessmentResult(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val diagnosis: String? = null,
    @SerializedName("confidence_score") val confidenceScore: Float = 0f,
    @SerializedName("risk_level") val riskLevel: String? = null, // "low", "medium", "high"
    val recommendation: String? = null,
    val kategori: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
)

data class AssessmentListResponse(
    val data: List<AssessmentResult> = emptyList(),
)
