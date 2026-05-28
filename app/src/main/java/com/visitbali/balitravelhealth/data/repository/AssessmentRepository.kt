package com.visitbali.balitravelhealth.data.repository

import com.visitbali.balitravelhealth.data.dto.AssessmentListResponse
import com.visitbali.balitravelhealth.data.dto.AssessmentRequest
import com.visitbali.balitravelhealth.data.dto.AssessmentResult
import com.visitbali.balitravelhealth.data.remote.BaliHealthApiService

class AssessmentRepository(private val api: BaliHealthApiService) {

    suspend fun submitAssessment(
        symptoms: List<Int>,
        kategori: String,
    ): Result<AssessmentResult> = runCatching {
        api.submitAssessment(AssessmentRequest(symptoms = symptoms, kategori = kategori))
    }

    suspend fun getHistory(page: Int = 1, limit: Int = 10): Result<AssessmentListResponse> = runCatching {
        api.getAssessments(page = page, limit = limit)
    }
}
