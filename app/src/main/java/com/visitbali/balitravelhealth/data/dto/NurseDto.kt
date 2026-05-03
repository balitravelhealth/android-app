package com.visitbali.balitravelhealth.data.dto

import com.visitbali.balitravelhealth.data.model.Nurse
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/* ---------- Request bodies ---------- */

data class CreateNurseRequest(
    @SerializedName("full_name")            val fullName: String,
    @SerializedName("years_of_experience")  val yearsOfExperience: Int,
    @SerializedName("rate_per_appointment") val ratePerAppointment: BigDecimal,
    @SerializedName("specialization")       val specialization: String? = null,
    @SerializedName("bio")                  val bio: String? = null,
    @SerializedName("profile_photo_url")    val profilePhotoUrl: String? = null,
)

data class UpdateNurseRequest(
    @SerializedName("full_name")            val fullName: String? = null,
    @SerializedName("years_of_experience")  val yearsOfExperience: Int? = null,
    @SerializedName("rate_per_appointment") val ratePerAppointment: BigDecimal? = null,
    @SerializedName("specialization")       val specialization: String? = null,
    @SerializedName("bio")                  val bio: String? = null,
    @SerializedName("profile_photo_url")    val profilePhotoUrl: String? = null,
    @SerializedName("is_active")            val isActive: Boolean? = null,
)

data class BookAppointmentRequest(
    @SerializedName("nurse_id")       val nurseId: String,
    @SerializedName("meeting_address") val meetingAddress: String,
    @SerializedName("appointment_date") val appointmentDate: String,
    @SerializedName("description")     val description: String
)

/* ---------- Response wrappers ---------- */

data class Pagination(
    val limit: Int,
    val offset: Int,
    val total: Int,
)

data class NurseListResponse(
    val success: Boolean,
    val data: List<Nurse> = emptyList(),
    val pagination: Pagination? = null,
    val message: String? = null,
)

data class NurseSingleResponse(
    val success: Boolean,
    val data: Nurse? = null,
    val message: String? = null,
)

data class BookAppointmentResponse(
    val success: Boolean,
    val message: String? = null
)

data class Appointment(
    @SerializedName("id")               val id: String,
    @SerializedName("nurse_id")        val nurseId: String,
    @SerializedName("nurse_name")      val nurseName: String,
    @SerializedName("meeting_address") val meetingAddress: String,
    @SerializedName("appointment_date") val appointmentDate: String,
    @SerializedName("description")      val description: String,
    @SerializedName("nurse_phone")     val nursePhone: String? = null,
    @SerializedName("nurse_photo_url") val nursePhotoUrl: String? = null
)

data class AppointmentResponse(
    val success: Boolean,
    val data: Appointment? = null,
    val message: String? = null
)
