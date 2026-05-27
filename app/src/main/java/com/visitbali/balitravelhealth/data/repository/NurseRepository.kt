package com.visitbali.balitravelhealth.data.repository

import com.visitbali.balitravelhealth.data.dao.NurseDao
import com.visitbali.balitravelhealth.data.dto.CareRecordRequest
import com.visitbali.balitravelhealth.data.dto.NursingAppointmentRequest
import com.visitbali.balitravelhealth.data.dto.NursingRecord
import com.visitbali.balitravelhealth.data.model.Nurse
import com.visitbali.balitravelhealth.data.remote.BaliHealthApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class NurseRepository(
    private val api: BaliHealthApiService,
    private val nurseDao: NurseDao? = null,
) {
    val cachedNurses: Flow<List<Nurse>> = nurseDao?.getActiveNurses() ?: flowOf(emptyList())

    suspend fun getNurses(): Result<List<Nurse>> = runCatching {
        try {
            val response = api.getNurses()
            nurseDao?.replaceAll(response.data)
            response.data
        } catch (e: Exception) {
            val cached = nurseDao?.getActiveNursesSnapshot().orEmpty()
            if (cached.isNotEmpty()) cached else throw e
        }
    }

    suspend fun getNurseById(id: Int): Result<Nurse> = runCatching {
        nurseDao?.getById(id) ?: error("Nurse not found")
    }

    suspend fun bookAppointment(nurseId: Int, tanggalKunjungan: String): Result<Unit> = runCatching {
        api.bookNursingAppointment(NursingAppointmentRequest(nurseId, tanggalKunjungan))
        Unit
    }

    suspend fun getMyRecords(): Result<List<NursingRecord>> = runCatching {
        api.getMyNursingRecords().data
    }

    suspend fun getLatestRecord(): Result<NursingRecord?> = runCatching {
        api.getMyNursingRecords().data.firstOrNull()
    }

    suspend fun getNurseRecords(): Result<List<NursingRecord>> = runCatching {
        api.getNurseRecords().data
    }

    suspend fun updateCareRecord(id: Int, request: CareRecordRequest): Result<NursingRecord> = runCatching {
        api.updateCareRecord(id, request)
    }
}
