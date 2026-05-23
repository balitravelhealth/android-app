package com.visitbali.balitravelhealth.data.api

import com.visitbali.balitravelhealth.data.pref.UserProfile
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

data class UserResponse(
    val success: Boolean,
    val data: UserProfile?,
    val message: String?,
    val session_token: String?
)

data class SaveProfileRequest(
    val idToken: String,
    val email: String,
    val name: String,
    val country: String,
    val dob: String,
    val gender: String
)

data class TravelDatesRequest(
    val arrival_date: String,
    val departure_date: String
)


data class BasicApiResponse(
    val success: Boolean,
    val message: String?
)

interface ApiService {
    @GET("credentials.php")
    suspend fun getUserProfile(
        @Header("Authorization") authorization: String,
        @Query("email") email: String
    ): UserResponse

    @POST("credentials.php")
    suspend fun saveUserProfile(
        @Body request: SaveProfileRequest
    ): UserResponse

    @PUT("credentials.php")
    suspend fun updateTravelDates(
        @Header("Authorization") authorization: String,
        @Body dates: TravelDatesRequest
    ): UserResponse

}
