package com.visitbali.balitravelhealth.data.dto

import com.google.gson.annotations.SerializedName

data class GoogleAuthRequest(
    @SerializedName("id_token") val idToken: String,
    @SerializedName("device_info") val deviceInfo: String = "Android",
)

data class AuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_in") val expiresIn: Int,
    val user: AuthUser,
)

data class AuthUser(
    val id: Int,
    val email: String,
)

data class RefreshTokenRequest(
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("device_info") val deviceInfo: String = "Android",
)

data class TokenRefreshResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_in") val expiresIn: Int,
)

data class LogoutRequest(
    @SerializedName("refresh_token") val refreshToken: String,
)

data class MessageResponse(
    val message: String,
)
