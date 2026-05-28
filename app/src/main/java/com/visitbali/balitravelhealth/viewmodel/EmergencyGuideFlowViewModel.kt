package com.visitbali.balitravelhealth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visitbali.balitravelhealth.data.api.RetrofitClient
import com.visitbali.balitravelhealth.data.dto.EmergencyGuideFlowDetail
import com.visitbali.balitravelhealth.data.dto.EmergencyGuideFlowSummary
import com.visitbali.balitravelhealth.data.dto.FlowNode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GuideFlowUiState(
    val flows: List<EmergencyGuideFlowSummary> = emptyList(),
    val activeFlow: EmergencyGuideFlowDetail? = null,
    val currentNode: FlowNode? = null,
    val nodeHistory: List<FlowNode> = emptyList(),
    val isLoadingList: Boolean = false,
    val isLoadingFlow: Boolean = false,
    val error: String? = null,
)

class EmergencyGuideFlowViewModel(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitClient.apiService

    private val _uiState = MutableStateFlow(GuideFlowUiState())
    val uiState: StateFlow<GuideFlowUiState> = _uiState.asStateFlow()

    init {
        loadFlows()
    }

    fun loadFlows() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingList = true, error = null) }
            runCatching { api.getEmergencyGuideFlows() }
                .onSuccess { response ->
                    _uiState.update { it.copy(flows = response.data, isLoadingList = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoadingList = false, error = e.message) }
                }
        }
    }

    fun startFlow(flowId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingFlow = true, error = null) }
            runCatching { api.getEmergencyGuideFlow(flowId) }
                .onSuccess { flow ->
                    val entryNode = flow.nodes.firstOrNull { it.isEntry } ?: flow.nodes.firstOrNull()
                    _uiState.update {
                        it.copy(
                            activeFlow = flow,
                            currentNode = entryNode,
                            nodeHistory = emptyList(),
                            isLoadingFlow = false,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoadingFlow = false, error = e.message) }
                }
        }
    }

    /** Navigate to the next node identified by [nextId]. */
    fun navigate(nextId: String) {
        val state = _uiState.value
        val flow = state.activeFlow ?: return
        val current = state.currentNode ?: return
        val nextNode = flow.nodes.firstOrNull { it.id == nextId } ?: return

        _uiState.update {
            it.copy(
                currentNode = nextNode,
                nodeHistory = it.nodeHistory + current,
            )
        }
    }

    /** Go back one step in the flow. */
    fun navigateBack(): Boolean {
        val state = _uiState.value
        if (state.nodeHistory.isEmpty()) return false

        val previous = state.nodeHistory.last()
        _uiState.update {
            it.copy(
                currentNode = previous,
                nodeHistory = it.nodeHistory.dropLast(1),
            )
        }
        return true
    }

    /** Close the active flow and return to the flow list. */
    fun closeFlow() {
        _uiState.update {
            it.copy(
                activeFlow = null,
                currentNode = null,
                nodeHistory = emptyList(),
            )
        }
    }
}
