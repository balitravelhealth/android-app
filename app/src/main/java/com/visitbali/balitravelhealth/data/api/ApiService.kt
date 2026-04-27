package com.visitbali.balitravelhealth.data.api

import com.visitbali.balitravelhealth.data.pref.UserProfile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

data class UserResponse(
    val success: Boolean,
    val data: UserProfile?,
    val message: String?
)

data class SaveProfileRequest(
    val idToken: String,
    val email: String,
    val name: String,
    val country: String,
    val dob: String,
    val gender: String
)
interface ApiService {
    @GET("credentials.php")
    suspend fun getUserProfile(
        @Header("Authorization") authorization: String,
        @Query("email") email: String
    ): UserResponse

    @POST("credentials.php")
    suspend fun saveUserProfile(
        @Header("Authorization") authorization: String,
        @Body request: SaveProfileRequest
    ): UserResponse

    @POST("credentials.php")
    suspend fun updateTravelDates(
        @Query("arrival") arrival: String,
        @Query("departure") departure: String
    ): UserResponse
}
