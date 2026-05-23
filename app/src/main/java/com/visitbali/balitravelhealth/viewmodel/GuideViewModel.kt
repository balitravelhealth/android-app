package com.visitbali.balitravelhealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.model.GuideItem
import com.visitbali.balitravelhealth.data.repository.GuideRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class GuideViewModel(
    repository: GuideRepository
) : ViewModel() {
    val guides: StateFlow<List<GuideItem>> = repository.guides.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    class Factory(private val repository: GuideRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GuideViewModel::class.java)) {
                return GuideViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
