package com.visitbali.balitravelhealth.data.repository

import com.visitbali.balitravelhealth.data.dto.Destination
import com.visitbali.balitravelhealth.data.dto.HealthRisk
import com.visitbali.balitravelhealth.data.remote.BaliHealthApiService

class DestinationRepository(private val api: BaliHealthApiService) {

    suspend fun getDestinations(): Result<List<Destination>> = runCatching {
        api.getDestinations().data
    }

    suspend fun getHealthRisks(destinationId: Int): Result<List<HealthRisk>> = runCatching {
        api.getDestinationHealthRisks(destinationId).data
    }
}
