package com.visitbali.balitravelhealth.data.repository

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.visitbali.balitravelhealth.R
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.pref.dataStore
import com.visitbali.balitravelhealth.data.remote.SeasonalForecast
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import com.visitbali.balitravelhealth.data.api.TravelDatesRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Month

class TravelRepository(context: Context) {

    private val userPreferences = UserPreferences(context)

    // Local Storage
    suspend fun saveTravelDates(arrival: String, departure: String) {
        val currentProfile = userPreferences.userProfile.first()
        userPreferences.saveUserProfile(currentProfile.copy(
            arrivalDate = arrival,
            departureDate = departure
        ))
    }

    val arrivalDate: Flow<String?> = userPreferences.userProfile.map { it.arrivalDate }
    val departureDate: Flow<String?> = userPreferences.userProfile.map { it.departureDate }

    // API calls
    suspend fun fetchDatesFromServer(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val sessionToken = userPreferences.sessionToken.first()
                val email = userPreferences.userProfile.first().email
                if (sessionToken.isNotEmpty() && email.isNotEmpty()) {
                    val response = RetrofitClient.apiService.getUserProfile(
                        authorization = "Bearer $sessionToken",
                        email = email
                    )
                    if (response.success && response.data != null) {
                        userPreferences.saveUserProfile(response.data.copy(isLoggedIn = true, isRegistered = true))
                    }
                }
                Unit
            }
        }
    }

    suspend fun syncDatesWithServer(arrival: String, departure: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val sessionToken = userPreferences.sessionToken.first()
                RetrofitClient.apiService.updateTravelDates(
                    authorization = "Bearer $sessionToken",
                    dates = TravelDatesRequest(
                        arrival_date = arrival,
                        departure_date = departure
                    )
                )
                Unit
            }
        }
    }

    /**
     * Determines the seasonal forecast based on the date range.
     * In Bali, Dry Season is Apr-Sep, Rainy Season is Oct-Mar.
     */
    suspend fun getSeasonalForecast(arrival: LocalDate, departure: LocalDate): Result<SeasonalForecast> {
        return withContext(Dispatchers.Default) {
            runCatching {
                val arrivalMonth = arrival.month
                
                if (isDrySeason(arrivalMonth)) {
                    SeasonalForecast(
                        seasonName = "Dry Season",
                        description = "Clear skies and low humidity. Perfect for outdoor activities.",
                        iconResId = R.drawable.sunny, // Placeholder for sunny/dry gif
                        averageTemp = 27,
                        averageHumidity = 65
                    )
                } else {
                    SeasonalForecast(
                        seasonName = "Rainy Season",
                        description = "Occasional tropical showers and lush green landscapes.",
                        iconResId = R.drawable.rainy, // Placeholder for rainy gif
                        averageTemp = 28,
                        averageHumidity = 85
                    )
                }
            }
        }
    }

    private fun isDrySeason(month: Month): Boolean {
        return month.value in 4..9 // April to September
    }
}
