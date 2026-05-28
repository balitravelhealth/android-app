package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.dto.CareRecordRequest
import com.visitbali.balitravelhealth.data.dto.NursingRecord
import com.visitbali.balitravelhealth.data.repository.NurseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NursingRecordsUiState(
    val myRecords: List<NursingRecord> = emptyList(),
    val nurseRecords: List<NursingRecord> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val message: String? = null,
)

class NursingRecordsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NurseRepository(api = RetrofitClient.apiService)

    private val _uiState = MutableStateFlow(NursingRecordsUiState(isLoading = true))
    val uiState: StateFlow<NursingRecordsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, message = null) }
            val my = async { repository.getMyRecords() }
            val nurse = async { repository.getNurseRecords() }

            val myResult = my.await()
            val nurseResult = nurse.await()

            _uiState.update {
                it.copy(
                    myRecords = myResult.getOrNull().orEmpty(),
                    nurseRecords = nurseResult.getOrNull().orEmpty(),
                    isLoading = false,
                    error = myResult.exceptionOrNull()?.message ?: nurseResult.exceptionOrNull()?.message,
                )
            }
        }
    }

    fun updateRecord(id: Int, request: CareRecordRequest) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, message = null) }
            repository.updateCareRecord(id, request)
                .onSuccess { updated ->
                    _uiState.update { state ->
                        state.copy(
                            nurseRecords = state.nurseRecords.map { if (it.id == updated.id) updated else it },
                            isSaving = false,
                            message = "Care record updated",
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, error = e.message) }
                }
        }
    }
}
