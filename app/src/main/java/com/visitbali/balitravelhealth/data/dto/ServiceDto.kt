package com.visitbali.balitravelhealth.data.dto

import com.google.gson.annotations.SerializedName

data class HealthResponse(
    val status: String? = null,
    val message: String? = null,
    val database: String? = null,
)

data class ExpertSymptomsResponse(
    val data: List<ExpertSymptom> = emptyList(),
)

data class ExpertSymptom(
    val id: Int,
    val kode: String? = null,
    @SerializedName("label_id") val labelId: String? = null,
    @SerializedName("label_en") val labelEn: String? = null,
    val kategori: String? = null,
)

data class CareRecordRequest(
    @SerializedName("nursing_assessment") val nursingAssessment: String? = null,
    @SerializedName("nursing_diagnosis") val nursingDiagnosis: String? = null,
    @SerializedName("nursing_planning") val nursingPlanning: String? = null,
    @SerializedName("nursing_implementation") val nursingImplementation: String? = null,
    @SerializedName("nursing_evaluation") val nursingEvaluation: String? = null,
)
