package com.visitbali.balitravelhealth.data.dto

import com.google.gson.annotations.SerializedName
import com.visitbali.balitravelhealth.data.model.Nurse

data class NursingAppointmentRequest(
    @SerializedName("nurse_id") val nurseId: Int,
    @SerializedName("tanggal_kunjungan") val tanggalKunjungan: String,
)

data class NursingRecord(
    val id: Int,
    @SerializedName("nurse_id") val nurseId: Int,
    @SerializedName("tanggal_kunjungan") val tanggalKunjungan: String,
    @SerializedName("nursing_assessment") val nursingAssessment: String? = null,
    @SerializedName("nursing_diagnosis") val nursingDiagnosis: String? = null,
    @SerializedName("nursing_planning") val nursingPlanning: String? = null,
    @SerializedName("nursing_implementation") val nursingImplementation: String? = null,
    @SerializedName("nursing_evaluation") val nursingEvaluation: String? = null,
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("updated_at") val updatedAt: String = "",
)

data class NursesResponse(
    val data: List<Nurse> = emptyList(),
)

data class NursingAppointmentResponse(
    val message: String? = null,
)

data class NursingRecordsResponse(
    val data: List<NursingRecord> = emptyList(),
)
