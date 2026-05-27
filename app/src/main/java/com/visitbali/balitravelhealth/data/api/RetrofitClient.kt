package com.visitbali.balitravelhealth.data.api

import android.content.Context
import com.google.gson.Gson
import com.visitbali.balitravelhealth.BuildConfig
import com.visitbali.balitravelhealth.data.network.AuthInterceptor
import com.visitbali.balitravelhealth.data.network.TokenAuthenticator
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import com.visitbali.balitravelhealth.data.remote.BaliHealthApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://backend.balihealth.me/"

    @Volatile private var preferences: UserPreferences? = null
    private val gson = Gson()

    fun init(context: Context) {
        if (preferences == null) {
            synchronized(this) {
                if (preferences == null) {
                    preferences = UserPreferences(context.applicationContext)
                }
            }
        }
    }

    private val prefs: UserPreferences
        get() = preferences ?: error("RetrofitClient.init(context) must be called from Application.onCreate()")

    private val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    private val mainClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(prefs))
            .authenticator(TokenAuthenticator(prefs, gson, BASE_URL))
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    val apiService: BaliHealthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(mainClient)
            .build()
            .create(BaliHealthApiService::class.java)
    }
}
