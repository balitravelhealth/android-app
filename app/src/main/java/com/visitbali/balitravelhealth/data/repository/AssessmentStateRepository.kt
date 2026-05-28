package com.visitbali.balitravelhealth.data.repository

import com.visitbali.balitravelhealth.data.pref.UserPreferences
import kotlinx.coroutines.flow.Flow

class AssessmentStateRepository(
    private val preferences: UserPreferences,
) {
    val hasCompletedHealthRiskAssessment: Flow<Boolean> =
        preferences.hasCompletedHealthRiskAssessment

    suspend fun setHealthRiskAssessmentCompleted(completed: Boolean) {
        preferences.setHealthRiskAssessmentCompleted(completed)
    }
}
