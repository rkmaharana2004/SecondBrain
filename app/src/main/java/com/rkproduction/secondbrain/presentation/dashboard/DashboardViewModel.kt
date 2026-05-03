package com.rkproduction.secondbrain.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkproduction.secondbrain.data.local.db.BrainEntryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val typeDistribution: Map<String, Int> = emptyMap(),
    val totalEntries: Int = 0,
    val wordCount: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dao: BrainEntryDao
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val counts = dao.getEntryCountByType()
            val total = counts.sumOf { it.count }
            
            _state.update { it.copy(
                typeDistribution = counts.associate { it.type to it.count },
                totalEntries = total
            ) }
        }
    }
}
