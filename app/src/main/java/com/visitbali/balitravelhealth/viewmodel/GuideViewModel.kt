package com.visitbali.balitravelhealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.model.GuideCategory
import com.visitbali.balitravelhealth.data.model.GuideMenuContent
import com.visitbali.balitravelhealth.data.repository.GuideRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GuideUiState(
    val content: GuideMenuContent = GuideMenuContent(),
    val isLoading: Boolean = true,
    val error: String? = null,
) {
    val basicLifeSupportFlows = content.flows.filter {
        it.kategori.equals("BLS", ignoreCase = true) ||
            it.title.contains("Basic Life Support", ignoreCase = true)
    }

    val emergencyFlows = content.flows.filterNot { flow ->
        basicLifeSupportFlows.any { it.id == flow.id }
    }

    fun category(categoryId: String): GuideCategory? =
        content.categories.firstOrNull { it.id.equals(categoryId, ignoreCase = true) }
}

class GuideViewModel(
    private val repository: GuideRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GuideUiState())
    val uiState: StateFlow<GuideUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.loadMenuContent()
                .onSuccess { content ->
                    _uiState.update {
                        it.copy(
                            content = content,
                            isLoading = false,
                            error = null,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Guide content is unavailable.",
                        )
                    }
                }
        }
    }

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
