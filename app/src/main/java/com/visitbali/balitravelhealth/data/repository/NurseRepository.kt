package com.visitbali.balitravelhealth.data.repository

import com.visitbali.balitravelhealth.data.dto.Appointment
import com.visitbali.balitravelhealth.data.dto.BookAppointmentRequest
import com.visitbali.balitravelhealth.data.dto.CreateNurseRequest
import com.visitbali.balitravelhealth.data.dto.NurseListResponse
import com.visitbali.balitravelhealth.data.dto.NurseSingleResponse
import com.visitbali.balitravelhealth.data.dto.UpdateNurseRequest
import com.visitbali.balitravelhealth.data.model.Nurse
import com.visitbali.balitravelhealth.data.remote.NurseApiService

class NurseRepository(
    private val api: NurseApiService,
) {

    suspend fun getNurses(
        limit: Int = 20,
        offset: Int = 0,
        isActive: Boolean? = true,
    ): Result<List<Nurse>> = runCatching {
        val response = api.listNurses(
            limit = limit,
            offset = offset,
            isActive = isActive,
        )
        if (!response.success) {
            error(response.message ?: "Gagal memuat daftar perawat")
        }
        response.data
    }

    suspend fun getNurseById(id: String): Result<Nurse> = runCatching {
        val response = api.getNurse(id)
        response.data ?: error(response.message ?: "Perawat tidak ditemukan")
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
