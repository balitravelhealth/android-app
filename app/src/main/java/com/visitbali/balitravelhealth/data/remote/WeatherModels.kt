package com.visitbali.balitravelhealth.data.remote

/**
 * Domain model for UI consumption
 * Representing seasonal trends in Bali
 */
data class SeasonalForecast(
    val seasonName: String,
    val description: String,
    val iconResId: Int,
    val averageTemp: Int,
    val averageHumidity: Int
)
