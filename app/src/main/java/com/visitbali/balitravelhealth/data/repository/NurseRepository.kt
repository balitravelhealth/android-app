package com.visitbali.balitravelhealth.data.repository

import com.visitbali.balitravelhealth.data.dao.NurseDao
import com.visitbali.balitravelhealth.data.dto.Appointment
import com.visitbali.balitravelhealth.data.dto.BookAppointmentRequest
import com.visitbali.balitravelhealth.data.dto.CreateNurseRequest
import com.visitbali.balitravelhealth.data.dto.NurseListResponse
import com.visitbali.balitravelhealth.data.dto.NurseSingleResponse
import com.visitbali.balitravelhealth.data.dto.UpdateNurseRequest
import com.visitbali.balitravelhealth.data.model.Nurse
import com.visitbali.balitravelhealth.data.remote.NurseApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class NurseRepository(
    private val api: NurseApiService,
    private val nurseDao: NurseDao? = null
) {
    val cachedNurses: Flow<List<Nurse>> = nurseDao?.getActiveNurses() ?: flowOf(emptyList())

    suspend fun getNurses(
        limit: Int = 20,
        offset: Int = 0,
        isActive: Boolean? = true,
    ): Result<List<Nurse>> = runCatching {
        try {
            val response = api.listNurses(
                limit = limit,
                offset = offset,
                isActive = isActive,
            )
            if (!response.success) {
                error(response.message ?: "Gagal memuat daftar perawat")
            }
            nurseDao?.replaceAll(response.data)
            response.data
        } catch (e: Exception) {
            val cached = nurseDao?.getActiveNursesSnapshot().orEmpty()
            if (cached.isNotEmpty()) cached else throw e
        }
    }

    suspend fun getNurseById(id: String): Result<Nurse> = runCatching {
        try {
            val response = api.getNurse(id)
            response.data ?: error(response.message ?: "Perawat tidak ditemukan")
        } catch (e: Exception) {
            nurseDao?.getById(id) ?: throw e
        }
    }

    suspend fun createNurse(request: CreateNurseRequest): Result<Nurse> = runCatching {
        val response = api.createNurse(request)
        response.data ?: error(response.message ?: "Gagal membuat data perawat")
    }

    suspend fun updateNurse(
        id: String,
        request: UpdateNurseRequest,
    ): Result<Nurse> = runCatching {
        val response = api.updateNurse(id, request)
        response.data ?: error(response.message ?: "Gagal memperbarui data perawat")
    }

    suspend fun deleteNurse(id: String): Result<Unit> = runCatching {
        val response = api.deleteNurse(id)
        if (!response.success) {
            error(response.message ?: "Gagal menonaktifkan perawat")
        }
    }

    suspend fun bookAppointment(request: BookAppointmentRequest): Result<Unit> = runCatching {
        val response = api.bookAppointment(request)
        if (!response.success) {
            error(response.message ?: "Appointment Failed")
        }
    }

    suspend fun getUserAppointment(): Result<Appointment?> = runCatching {
        val response = api.getUserAppointment()
        if (!response.success) {
            null // Or throw error based on your preference, but usually null means no appointment
        } else {
            response.data
        }
    }
}
