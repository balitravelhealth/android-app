package com.visitbali.balitravelhealth.data.remote

import com.visitbali.balitravelhealth.data.dto.*
import retrofit2.http.*

interface BaliHealthApiService {

    @GET("health")
    suspend fun health(): HealthResponse

    // ── Auth ────────────────────────────────────────────────────────────────

    @POST("auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleAuthRequest): AuthResponse

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): TokenRefreshResponse

    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest): MessageResponse

    // ── Location ────────────────────────────────────────────────────────────

    @GET("location/classify")
    suspend fun classifyLocation(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
    ): LocationClassificationResponse

    @GET("facilities/nearby")
    suspend fun getFacilitiesNearby(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius_km") radiusKm: Float? = null,
        @Query("limit") limit: Int? = null,
    ): NearbyFacilitiesResponse

    @GET("facilities/all")
    suspend fun getFacilities(): HealthcareFacilitiesResponse

    // ── Destinations ────────────────────────────────────────────────────────

    @GET("destinations")
    suspend fun getDestinations(): DestinationsResponse

    @GET("destinations/{id}/health-risks")
    suspend fun getDestinationHealthRisks(@Path("id") id: Int): HealthRisksResponse

    // ── Emergency Guides ────────────────────────────────────────────────────

    @GET("emergency-guides")
    suspend fun getEmergencyGuides(
        @Query("kategori") kategori: String? = null,
    ): EmergencyGuidesResponse

    @GET("emergency-guide-flows")
    suspend fun getEmergencyGuideFlows(): EmergencyGuideFlowsResponse

    @GET("emergency-guide-flows/{id}")
    suspend fun getEmergencyGuideFlow(@Path("id") id: Int): EmergencyGuideFlowDetail

    @GET("expert/symptoms")
    suspend fun getExpertSymptoms(
        @Query("kategori") kategori: String? = null,
    ): ExpertSymptomsResponse

    // ── Traveler Profile (Auth required) ────────────────────────────────────

    @GET("traveler-profile")
    suspend fun getTravelerProfile(): TravelerProfile

    @POST("traveler-profile")
    suspend fun createTravelerProfile(@Body request: CreateTravelerProfileRequest): TravelerProfile

    @PUT("traveler-profile")
    suspend fun updateTravelerProfile(@Body request: UpdateTravelerProfileRequest): TravelerProfile

    // ── Health Profile (Auth required) ──────────────────────────────────────

    @GET("health-profile")
    suspend fun getHealthProfile(): HealthProfile

    @POST("health-profile")
    suspend fun createHealthProfile(@Body request: CreateHealthProfileRequest): HealthProfile

    @PUT("health-profile")
    suspend fun updateHealthProfile(@Body request: UpdateHealthProfileRequest): HealthProfile

    // ── Assessment (Auth required) ───────────────────────────────────────────

    @POST("assessment")
    suspend fun submitAssessment(@Body request: AssessmentRequest): AssessmentResult

    @GET("assessments")
    suspend fun getAssessments(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
    ): AssessmentListResponse

    // ── Vaccinations (Auth required) ─────────────────────────────────────────

    @GET("vaccinations")
    suspend fun getVaccinations(): VaccinationsResponse

    @POST("vaccinations")
    suspend fun addVaccination(@Body request: CreateVaccinationRequest): VaccinationResponse

    @DELETE("vaccinations/{id}")
    suspend fun deleteVaccination(@Path("id") id: Int): MessageResponse

    // ── Nursing (Auth required) ──────────────────────────────────────────────

    @GET("nurses")
    suspend fun getNurses(): NursesResponse

    @POST("nursing/appointments")
    suspend fun bookNursingAppointment(@Body request: NursingAppointmentRequest): NursingAppointmentResponse

    @GET("nursing/my-records")
    suspend fun getMyNursingRecords(): NursingRecordsResponse

    @GET("nursing/nurse-records")
    suspend fun getNurseRecords(): NursingRecordsResponse

    @PUT("nursing/records/{id}")
    suspend fun updateCareRecord(
        @Path("id") id: Int,
        @Body request: CareRecordRequest,
    ): NursingRecord
}
