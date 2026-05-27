package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.remote.SeasonalForecast
import com.visitbali.balitravelhealth.data.repository.TravelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class TravelUiState(
    val arrivalDate: LocalDate? = null,
    val departureDate: LocalDate? = null,
    val seasonalForecast: SeasonalForecast? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class TravelViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TravelRepository(application)

    private val _uiState = MutableStateFlow(TravelUiState())
    val uiState: StateFlow<TravelUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.arrivalDate.collect { arrival ->
                arrival?.let {
                    runCatching { LocalDate.parse(it) }.getOrNull()?.let { date ->
                        _uiState.update { state -> state.copy(arrivalDate = date) }
                        checkForecastIfNeeded()
                    }
                }
            }
        }
        viewModelScope.launch {
            repository.departureDate.collect { departure ->
                departure?.let {
                    runCatching { LocalDate.parse(it) }.getOrNull()?.let { date ->
                        _uiState.update { state -> state.copy(departureDate = date) }
                        checkForecastIfNeeded()
                    }
                }
            }
        }
    }

    private fun checkForecastIfNeeded() {
        val state = _uiState.value
        if (state.arrivalDate != null && state.departureDate != null && state.seasonalForecast == null) {
            fetchSeasonalForecast(state.arrivalDate, state.departureDate)
        }
    }

    fun updateArrival(date: LocalDate) {
        val currentDeparture = _uiState.value.departureDate
        _uiState.update {
            it.copy(
                arrivalDate = date,
                departureDate = if (currentDeparture?.isBefore(date) == true) null else currentDeparture,
                error = null,
            )
        }
        checkAndSaveDates()
    }

    fun updateDeparture(date: LocalDate) {
        val arrival = _uiState.value.arrivalDate
        if (arrival != null && date.isBefore(arrival)) {
            _uiState.update { it.copy(error = "Departure date cannot be before arrival date") }
            return
        }
        _uiState.update { it.copy(departureDate = date, error = null) }
        checkAndSaveDates()
    }

    private fun checkAndSaveDates() {
        val state = _uiState.value
        if (state.arrivalDate != null && state.departureDate != null) {
            fetchSeasonalForecast(state.arrivalDate, state.departureDate)
            saveAndSyncDates(state.arrivalDate, state.departureDate)
        }
    }

    fun fetchSeasonalForecast(arrival: LocalDate, departure: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getSeasonalForecast(arrival, departure)
                .onSuccess { result -> _uiState.update { it.copy(seasonalForecast = result, isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun saveAndSyncDates(arrival: LocalDate, departure: LocalDate) {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        viewModelScope.launch {
            repository.saveTravelDates(arrival.format(formatter), departure.format(formatter))
        }
    }
}
