package com.visitbali.balitravelhealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.dto.NursingRecord
import com.visitbali.balitravelhealth.data.model.Nurse
import com.visitbali.balitravelhealth.data.repository.NurseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class NursingCareUiState(
    val nurses: List<Nurse> = emptyList(),
    val latestRecord: NursingRecord? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val bookingSuccess: Boolean = false,
    val bookingError: String? = null,
)

class NursingCareViewModel(
    private val repository: NurseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NursingCareUiState())
    val uiState: StateFlow<NursingCareUiState> = _uiState.asStateFlow()

    init {
        observeCachedNurses()
        checkLatestRecord()
    }

    private fun observeCachedNurses() {
        viewModelScope.launch {
            repository.cachedNurses.collect { cached ->
                if (cached.isNotEmpty()) {
                    _uiState.update { it.copy(nurses = cached) }
                }
            }
        }
    }

    fun checkLatestRecord() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getLatestRecord()
                .onSuccess { record ->
                    val activeRecord = record?.takeIf { isRecordActive(it.tanggalKunjungan) }
                    _uiState.update { it.copy(latestRecord = activeRecord, isLoading = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    private fun isRecordActive(dateStr: String): Boolean =
        try {
            val date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
            !LocalDate.now().isAfter(date.plusDays(1))
        } catch (e: Exception) {
            true
        }

    fun loadNurses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getNurses()
                .onSuccess { list ->
                    _uiState.update { it.copy(nurses = list, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = if (state.nurses.isEmpty()) e.message else null,
                        )
                    }
                }
        }
    }

    fun bookAppointment(nurseId: Int, tanggalKunjungan: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, bookingError = null, bookingSuccess = false) }
            repository.bookAppointment(nurseId, tanggalKunjungan)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, bookingSuccess = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, bookingError = e.message) }
                }
        }
    }

    fun resetBookingState() = _uiState.update { it.copy(bookingSuccess = false, bookingError = null) }

    class Factory(private val repository: NurseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NursingCareViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NursingCareViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
