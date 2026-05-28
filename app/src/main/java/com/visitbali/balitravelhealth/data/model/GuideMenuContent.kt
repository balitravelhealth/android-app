package com.visitbali.balitravelhealth.data.model

import com.visitbali.balitravelhealth.data.dto.EmergencyGuideFlowSummary

data class GuideStep(
    val id: Int,
    val number: Int,
    val title: String,
    val body: String,
    val imageUrl: String? = null,
    val iconName: String? = null,
)

data class GuideCategory(
    val id: String,
    val title: String,
    val summary: String,
    val steps: List<GuideStep>,
    val imageUrl: String? = null,
    val iconName: String? = null,
)

data class GuideMenuContent(
    val flows: List<EmergencyGuideFlowSummary> = emptyList(),
    val categories: List<GuideCategory> = emptyList(),
)
