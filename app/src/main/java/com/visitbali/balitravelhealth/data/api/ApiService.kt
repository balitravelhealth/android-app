package com.visitbali.balitravelhealth.data.api

import com.visitbali.balitravelhealth.data.pref.UserProfile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class UserResponse(
    val success: Boolean,
    val data: UserProfile?,
    val message: String?
)

interface ApiService {
    @GET("user/profile")
    suspend fun getUserProfile(@Query("email") email: String): UserResponse

    @POST("user/profile")
    suspend fun saveUserProfile(@Body profile: UserProfile): UserResponse
}
