package com.visitbali.balitravelhealth.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class UserProfile(
    val email: String = "",
    val name: String,
    val country: String,
    val dob: String,
    val gender: String,
    val photoUrl: String? = null,
    val isLoggedIn: Boolean,
    val isRegistered: Boolean,
    @SerializedName("arrival_date")
    val arrivalDate: String? = null,
    @SerializedName("departure_date")
    val departureDate: String? = null,
)

class UserPreferences(private val context: Context) {

    companion object {
        private val ID_TOKEN_KEY = stringPreferencesKey("id_token")
        private val EMAIL = stringPreferencesKey("email")
        private val NAME = stringPreferencesKey("name")
        private val COUNTRY = stringPreferencesKey("country")
        private val DOB = stringPreferencesKey("dob")
        private val GENDER = stringPreferencesKey("gender")
        private val PHOTO_URL = stringPreferencesKey("photo_url")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val IS_REGISTERED = booleanPreferencesKey("is_registered")
        private val SESSION_TOKEN_KEY = stringPreferencesKey("session_token")
        private val ARRIVAL_DATE = stringPreferencesKey("arrival_date")
        private val DEPARTURE_DATE = stringPreferencesKey("departure_date")
    }

    val userProfile: Flow<UserProfile> = context.dataStore.data.map { preferences ->
        UserProfile(
            email = preferences[EMAIL] ?: "",
            name = preferences[NAME] ?: "",
            country = preferences[COUNTRY] ?: "",
            dob = preferences[DOB] ?: "",
            gender = preferences[GENDER] ?: "",
            photoUrl = preferences[PHOTO_URL],
            isLoggedIn = preferences[IS_LOGGED_IN] ?: false,
            isRegistered = preferences[IS_REGISTERED] ?: false,
            arrivalDate = preferences[ARRIVAL_DATE],
            departureDate = preferences[DEPARTURE_DATE]
        )
    }

    suspend fun saveIdToken(token: String) {
        context.dataStore.edit { it[ID_TOKEN_KEY] = token }
    }
    val idToken: Flow<String> = context.dataStore.data.map { it[ID_TOKEN_KEY] ?: ""}

    suspend fun saveSessionToken(token: String) {
        context.dataStore.edit { it[SESSION_TOKEN_KEY] = token }
    }
    val sessionToken: Flow<String> = context.dataStore.data.map { it[SESSION_TOKEN_KEY] ?: ""}

    suspend fun saveEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[EMAIL] = email
        }
    }

    suspend fun saveLoginStatus(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        context.dataStore.edit { preferences ->
            preferences[EMAIL] = profile.email
            preferences[NAME] = profile.name
            preferences[COUNTRY] = profile.country
            preferences[DOB] = profile.dob
            preferences[GENDER] = profile.gender
            profile.photoUrl?.let { preferences[PHOTO_URL] = it }
            preferences[IS_LOGGED_IN] = profile.isLoggedIn
            preferences[IS_REGISTERED] = profile.isRegistered
            profile.arrivalDate?.let { preferences[ARRIVAL_DATE] = it }
            profile.departureDate?.let { preferences[DEPARTURE_DATE] = it }
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
