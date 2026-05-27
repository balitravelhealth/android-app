package com.visitbali.balitravelhealth.data.repository

import android.content.Context
import com.visitbali.balitravelhealth.R
import com.visitbali.balitravelhealth.data.pref.UserPreferences
import com.visitbali.balitravelhealth.data.remote.SeasonalForecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Month

class TravelRepository(context: Context) {

    private val userPreferences = UserPreferences(context)

    val arrivalDate: Flow<String?> = userPreferences.userProfile.map { it.arrivalDate }
    val departureDate: Flow<String?> = userPreferences.userProfile.map { it.departureDate }

    suspend fun saveTravelDates(arrival: String, departure: String) {
        val current = userPreferences.userProfile.first()
        userPreferences.saveUserProfile(current.copy(arrivalDate = arrival, departureDate = departure))
    }

    suspend fun getSeasonalForecast(arrival: LocalDate, departure: LocalDate): Result<SeasonalForecast> =
        withContext(Dispatchers.Default) {
            runCatching {
                if (isDrySeason(arrival.month)) {
                    SeasonalForecast(
                        seasonName = "Dry Season",
                        description = "Clear skies and low humidity. Perfect for outdoor activities.",
                        iconResId = R.drawable.sunny,
                        averageTemp = 27,
                        averageHumidity = 65,
                    )
                } else {
                    SeasonalForecast(
                        seasonName = "Rainy Season",
                        description = "Occasional tropical showers and lush green landscapes.",
                        iconResId = R.drawable.rainy,
                        averageTemp = 28,
                        averageHumidity = 85,
                    )
                }
            }
        }

    private fun isDrySeason(month: Month): Boolean = month.value !in listOf(11, 12, 1, 2, 3)
}
