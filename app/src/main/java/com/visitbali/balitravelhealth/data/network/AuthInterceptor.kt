package com.visitbali.balitravelhealth.data.network

import com.visitbali.balitravelhealth.BuildConfig
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val preferences: UserPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { preferences.accessToken.first() }
        val request = chain.request().newBuilder()
            .header("Accept", "application/json")
            .header("User-Agent", appUserAgent())
            .apply {
                if (token.isNotEmpty()) {
                    header("Authorization", "Bearer $token")
                }
            }
            .build()

        return chain.proceed(request)
    }

    private fun appUserAgent(): String {
        val versionName = BuildConfig.VERSION_NAME.ifBlank { "1.0" }
        val versionCode = BuildConfig.VERSION_CODE
        return "BTH-ANDROID-7c3e9f BaliTravelHealth/$versionName ($versionCode; Android)"
    }
}
