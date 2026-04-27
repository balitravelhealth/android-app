package com.visitbali.balitravelhealth.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.visitbali.balitravelhealth.R
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.pref.dataStore
import com.visitbali.balitravelhealth.data.remote.SeasonalForecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Month

class TravelRepository(private val context: Context) {

    private val arrivalKey = stringPreferencesKey("arrival_date")
    private val departureKey = stringPreferencesKey("departure_key")

    // Local Storage
    suspend fun saveTravelDates(arrival: String, departure: String) {
        context.dataStore.edit { prefs ->
            prefs[arrivalKey] = arrival
            prefs[departureKey] = departure
        }
    }

    val arrivalDate: Flow<String?> = context.dataStore.data.map { it[arrivalKey] }
    val departureDate: Flow<String?> = context.dataStore.data.map { it[departureKey] }

    // API calls
    suspend fun syncDatesWithServer(arrival: String, departure: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                RetrofitClient.apiService.updateTravelDates(arrival, departure)
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
