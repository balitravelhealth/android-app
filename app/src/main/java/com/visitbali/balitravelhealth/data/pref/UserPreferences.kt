package com.visitbali.balitravelhealth.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class UserProfile(
    val email: String = "",
    val name: String = "",
    val country: String = "",
    val dob: String = "",
    val gender: String = "",
    val photoUrl: String? = null,
    val isLoggedIn: Boolean = false,
    val isProfileComplete: Boolean = false,
    val arrivalDate: String? = null,
    val departureDate: String? = null,
)

class UserPreferences(private val context: Context) {

    companion object {
        private val ID_TOKEN = stringPreferencesKey("id_token")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_ID = intPreferencesKey("user_id")
        private val EMAIL = stringPreferencesKey("email")
        private val NAME = stringPreferencesKey("name")
        private val COUNTRY = stringPreferencesKey("country")
        private val DOB = stringPreferencesKey("dob")
        private val GENDER = stringPreferencesKey("gender")
        private val PHOTO_URL = stringPreferencesKey("photo_url")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val IS_PROFILE_COMPLETE = booleanPreferencesKey("is_profile_complete")
        private val ARRIVAL_DATE = stringPreferencesKey("arrival_date")
        private val DEPARTURE_DATE = stringPreferencesKey("departure_date")
        private val HEALTH_RISK_ASSESSMENT_COMPLETE =
            booleanPreferencesKey("com.balitravelhealth.healthRiskAssessmentComplete")
    }

    val userProfile: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        UserProfile(
            email = prefs[EMAIL] ?: "",
            name = prefs[NAME] ?: "",
            country = prefs[COUNTRY] ?: "",
            dob = prefs[DOB] ?: "",
            gender = prefs[GENDER] ?: "",
            photoUrl = prefs[PHOTO_URL],
            isLoggedIn = prefs[IS_LOGGED_IN] ?: false,
            isProfileComplete = prefs[IS_PROFILE_COMPLETE] ?: false,
            arrivalDate = prefs[ARRIVAL_DATE],
            departureDate = prefs[DEPARTURE_DATE],
        )
    }

    val idToken: Flow<String> = context.dataStore.data.map { it[ID_TOKEN] ?: "" }
    val accessToken: Flow<String> = context.dataStore.data.map { it[ACCESS_TOKEN] ?: "" }
    val refreshToken: Flow<String> = context.dataStore.data.map { it[REFRESH_TOKEN] ?: "" }
    val userId: Flow<Int> = context.dataStore.data.map { it[USER_ID] ?: 0 }
    val hasCompletedHealthRiskAssessment: Flow<Boolean> =
        context.dataStore.data.map { it[HEALTH_RISK_ASSESSMENT_COMPLETE] ?: false }

    suspend fun saveIdToken(token: String) {
        context.dataStore.edit { it[ID_TOKEN] = token }
    }

    suspend fun saveAuthTokens(accessToken: String, refreshToken: String, userId: Int = 0) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            prefs[REFRESH_TOKEN] = refreshToken
            if (userId > 0) prefs[USER_ID] = userId
            prefs[IS_LOGGED_IN] = true
        }
    }

    suspend fun clearAuthTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN)
            prefs.remove(REFRESH_TOKEN)
            prefs[IS_LOGGED_IN] = false
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[EMAIL] = profile.email
            prefs[NAME] = profile.name
            prefs[COUNTRY] = profile.country
            prefs[DOB] = profile.dob
            prefs[GENDER] = profile.gender
            profile.photoUrl?.let { prefs[PHOTO_URL] = it }
            prefs[IS_LOGGED_IN] = profile.isLoggedIn
            prefs[IS_PROFILE_COMPLETE] = profile.isProfileComplete
            profile.arrivalDate?.let { prefs[ARRIVAL_DATE] = it }
            profile.departureDate?.let { prefs[DEPARTURE_DATE] = it }
        }
    }

    suspend fun saveLoginStatus(isLoggedIn: Boolean) {
        context.dataStore.edit { it[IS_LOGGED_IN] = isLoggedIn }
    }

    suspend fun saveEmail(email: String) {
        context.dataStore.edit { it[EMAIL] = email }
    }

    suspend fun saveName(name: String) {
        context.dataStore.edit { it[NAME] = name }
    }

    suspend fun saveProfileComplete(complete: Boolean) {
        context.dataStore.edit { it[IS_PROFILE_COMPLETE] = complete }
    }

    suspend fun saveTravelDates(arrivalDate: String?, departureDate: String?) {
        context.dataStore.edit { prefs ->
            if (arrivalDate != null) prefs[ARRIVAL_DATE] = arrivalDate
            if (departureDate != null) prefs[DEPARTURE_DATE] = departureDate
        }
    }

    suspend fun setHealthRiskAssessmentCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[HEALTH_RISK_ASSESSMENT_COMPLETE] = completed
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
