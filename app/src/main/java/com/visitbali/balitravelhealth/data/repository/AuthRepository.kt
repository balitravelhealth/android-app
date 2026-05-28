package com.visitbali.balitravelhealth.data.repository

import com.visitbali.balitravelhealth.data.dto.GoogleAuthRequest
import com.visitbali.balitravelhealth.data.dto.LogoutRequest
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import com.visitbali.balitravelhealth.data.remote.BaliHealthApiService
import kotlinx.coroutines.flow.first

class AuthRepository(
    private val api: BaliHealthApiService,
    private val preferences: UserPreferences,
) {
    suspend fun loginWithGoogle(idToken: String): Result<Unit> = runCatching {
        val response = api.loginWithGoogle(GoogleAuthRequest(idToken = idToken))
        preferences.saveAuthTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            userId = response.user.id,
        )
        preferences.saveEmail(response.user.email)
        preferences.saveLoginStatus(true)
    }

    suspend fun logout(): Result<Unit> = runCatching {
        val refreshToken = preferences.refreshToken.first()
        if (refreshToken.isNotEmpty()) {
            runCatching { api.logout(LogoutRequest(refreshToken)) }
        }
        preferences.clear()
    }
}
