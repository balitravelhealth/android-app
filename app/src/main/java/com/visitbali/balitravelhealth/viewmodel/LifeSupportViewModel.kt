package com.visitbali.balitravelhealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.model.LifeSupportItem
import com.visitbali.balitravelhealth.data.repository.LifeSupportRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class LifeSupportViewModel(
    repository: LifeSupportRepository
) : ViewModel() {
    val items: StateFlow<List<LifeSupportItem>> = repository.items.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    class Factory(private val repository: LifeSupportRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LifeSupportViewModel::class.java)) {
                return LifeSupportViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
