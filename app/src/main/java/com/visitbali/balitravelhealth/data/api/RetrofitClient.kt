package com.visitbali.balitravelhealth.data.api





import com.visitbali.balitravelhealth.BuildConfig
import com.visitbali.balitravelhealth.data.remote.NurseApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 10.0.2.2 is the special IP that refers to your computer's localhost from the Emulator
    private const val BASE_URL = "https://balihealth.me/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val appAuthInterceptor = Interceptor { chain ->
        val secret = BuildConfig.API_SECRET_KEY
        val request = chain.request().newBuilder()
            .addHeader("X-App-Secret", secret)
            .addHeader("User-Agent", "BaliTravelHealth/1.0 (com.visitbali.balitravelhealth; Android)")
            .build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(appAuthInterceptor)
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val nurseApiService: NurseApiService by lazy {
        retrofit.create(NurseApiService::class.java)
    }
}
