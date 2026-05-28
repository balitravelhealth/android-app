package com.visitbali.balitravelhealth.data.network

import com.google.gson.Gson
import com.visitbali.balitravelhealth.data.dto.RefreshTokenRequest
import com.visitbali.balitravelhealth.data.dto.TokenRefreshResponse
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val preferences: UserPreferences,
    private val gson: Gson,
    private val baseUrl: String,
) : okhttp3.Authenticator {

    private val lock = Any()
    private val refreshClient = OkHttpClient()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.responseCount >= 2) return null

        synchronized(lock) {
            val refreshToken = runBlocking { preferences.refreshToken.first() }
            if (refreshToken.isEmpty()) return null

            // If another thread already refreshed, retry with the new token
            val currentToken = runBlocking { preferences.accessToken.first() }
            val tokenInRequest = response.request.header("Authorization")?.removePrefix("Bearer ")
            if (currentToken != tokenInRequest && currentToken.isNotEmpty()) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val body = gson.toJson(RefreshTokenRequest(refreshToken))
                .toRequestBody("application/json".toMediaType())
            val refreshRequest = Request.Builder()
                .url("${baseUrl}auth/refresh")
                .header("Accept", "application/json")
                .header("User-Agent", "BTH-ANDROID-7c3e9f BaliTravelHealth/${com.visitbali.balitravelhealth.BuildConfig.VERSION_NAME} (${com.visitbali.balitravelhealth.BuildConfig.VERSION_CODE}; Android)")
                .post(body)
                .build()

            return try {
                val refreshResponse = refreshClient.newCall(refreshRequest).execute()
                if (!refreshResponse.isSuccessful) {
                    runBlocking { preferences.clearAuthTokens() }
                    return null
                }
                val newTokens = gson.fromJson(
                    refreshResponse.body?.string(),
                    TokenRefreshResponse::class.java,
                )
                runBlocking {
                    preferences.saveAuthTokens(
                        accessToken = newTokens.accessToken,
                        refreshToken = newTokens.refreshToken,
                    )
                }
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                    .build()
            } catch (e: Exception) {
                null
            }
        }
    }

    private val Response.responseCount: Int
        get() = generateSequence(this) { it.priorResponse }.count()
}
