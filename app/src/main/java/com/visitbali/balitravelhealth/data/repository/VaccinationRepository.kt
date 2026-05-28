package com.visitbali.balitravelhealth.data.repository

import com.visitbali.balitravelhealth.data.dto.CreateVaccinationRequest
import com.visitbali.balitravelhealth.data.dto.Vaccination
import com.visitbali.balitravelhealth.data.remote.BaliHealthApiService

class VaccinationRepository(private val api: BaliHealthApiService) {

    suspend fun getVaccinations(): Result<List<Vaccination>> = runCatching {
        api.getVaccinations().data
    }

    suspend fun addVaccination(request: CreateVaccinationRequest): Result<Vaccination> = runCatching {
        api.addVaccination(request).data ?: error("No data in vaccination response")
    }

    suspend fun deleteVaccination(id: Int): Result<Unit> = runCatching {
        api.deleteVaccination(id)
        Unit
    }
}
