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
    val name: String,
    val country: String,
    val dob: String,
    val gender: String,
    val isLoggedIn: Boolean,
    val isRegistered: Boolean
)

class UserPreferences(private val context: Context) {

    companion object {
        private val EMAIL = stringPreferencesKey("email")
        private val NAME = stringPreferencesKey("name")
        private val COUNTRY = stringPreferencesKey("country")
        private val DOB = stringPreferencesKey("dob")
        private val GENDER = stringPreferencesKey("gender")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val IS_REGISTERED = booleanPreferencesKey("is_registered")
    }

    val userProfile: Flow<UserProfile> = context.dataStore.data.map { preferences ->
        UserProfile(
            email = preferences[EMAIL] ?: "",
            name = preferences[NAME] ?: "",
            country = preferences[COUNTRY] ?: "",
            dob = preferences[DOB] ?: "",
            gender = preferences[GENDER] ?: "",
            isLoggedIn = preferences[IS_LOGGED_IN] ?: false,
            isRegistered = preferences[IS_REGISTERED] ?: false
        )
    }

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
            preferences[IS_LOGGED_IN] = profile.isLoggedIn
            preferences[IS_REGISTERED] = profile.isRegistered
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
