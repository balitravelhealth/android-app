package com.visitbali.balitravelhealth.data.api





import com.visitbali.balitravelhealth.BuildConfig
import com.visitbali.balitravelhealth.data.remote.ContentApiService
import com.visitbali.balitravelhealth.data.remote.NurseApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val AUTH_BASE_URL = "https://auth.balihealth.me/"

    private val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
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
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun normalizedBaseUrl(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }

    private val authRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(AUTH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private val otherRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(normalizedBaseUrl(BuildConfig.OTHER_API_BASE_URL))
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val apiService: ApiService by lazy {
        authRetrofit.create(ApiService::class.java)
    }

    val nurseApiService: NurseApiService by lazy {
        otherRetrofit.create(NurseApiService::class.java)
    }

    val contentApiService: ContentApiService by lazy {
        otherRetrofit.create(ContentApiService::class.java)
    }
}
