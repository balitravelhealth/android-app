package com.visitbali.balitravelhealth.data.remote

import com.visitbali.balitravelhealth.data.dto.AppointmentResponse
import com.visitbali.balitravelhealth.data.dto.BookAppointmentRequest
import com.visitbali.balitravelhealth.data.dto.BookAppointmentResponse
import com.visitbali.balitravelhealth.data.dto.CreateNurseRequest
import com.visitbali.balitravelhealth.data.dto.NurseListResponse
import com.visitbali.balitravelhealth.data.dto.NurseSingleResponse
import com.visitbali.balitravelhealth.data.dto.UpdateNurseRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NurseApiService {

    @GET("api/nurses")
    suspend fun listNurses(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("isActive") isActive: Boolean? = true,
    ): NurseListResponse

    @GET("api/nurses/{id}")
    suspend fun getNurse(
        @Path("id") id: String
    ): NurseSingleResponse

    @POST("api/nurses")
    suspend fun createNurse(
        @Body body: CreateNurseRequest
    ): NurseSingleResponse

    @PUT("api/nurses/{id}")
    suspend fun updateNurse(
        @Path("id") id: String,
        @Body body: UpdateNurseRequest,
    ): NurseSingleResponse

    @DELETE("api/nurses/{id}")
    suspend fun deleteNurse(
        @Path("id") id: String
    ): NurseSingleResponse

    @POST("api/appointments")
    suspend fun bookAppointment(
        @Body body: BookAppointmentRequest
    ): BookAppointmentResponse

    @GET("api/appointments/me")
    suspend fun getUserAppointment(): AppointmentResponse
}
