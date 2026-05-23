package com.visitbali.balitravelhealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.dto.Appointment
import com.visitbali.balitravelhealth.data.dto.BookAppointmentRequest
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
    val appointment: Appointment? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val bookingSuccess: Boolean = false,
    val bookingError: String? = null
)

class NursingCareViewModel(
    private val repository: NurseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NursingCareUiState())
    val uiState: StateFlow<NursingCareUiState> = _uiState.asStateFlow()

    init {
        observeCachedNurses()
        checkUserAppointment()
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

    fun checkUserAppointment() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getUserAppointment()
            result.onSuccess { appointment ->
                val activeAppointment = appointment?.takeIf { 
                    isAppointmentStillActive(it.appointmentDate)
                }
                _uiState.update { it.copy(appointment = activeAppointment, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun isAppointmentStillActive(dateStr: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            // Split by space to get just the date part if time is included (e.g. "15/05/2026 10:00 AM")
            val datePart = dateStr.split(" ")[0]
            val appDate = LocalDate.parse(datePart, formatter)
            val today = LocalDate.now()
            
            // Show the card until 1 day AFTER the appointment date
            // If appDate is 01/01, appDate.plusDays(1) is 02/01. 
            // We show it if today is <= 02/01.
            !today.isAfter(appDate.plusDays(1))
        } catch (e: Exception) {
            true // Fallback to active if parsing fails
        }
    }

    fun loadNurses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getNurses()
            result.onSuccess { list ->
                _uiState.update { it.copy(nurses = list, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = if (state.nurses.isEmpty()) e.message else null
                    )
                }
            }
        }
    }

    fun bookAppointment(
        nurseId: String,
        meetingAddress: String,
        appointmentDate: String,
        description: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, bookingError = null, bookingSuccess = false) }
            val request = BookAppointmentRequest(nurseId, meetingAddress, appointmentDate, description)
            val result = repository.bookAppointment(request)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, bookingSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, bookingError = e.message) }
            }
        }
    }

    fun resetBookingState() {
        _uiState.update { it.copy(bookingSuccess = false, bookingError = null) }
    }

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
