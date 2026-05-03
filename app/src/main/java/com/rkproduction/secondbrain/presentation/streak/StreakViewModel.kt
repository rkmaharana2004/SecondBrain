package com.rkproduction.secondbrain.presentation.streak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkproduction.secondbrain.data.local.db.BrainEntryDao
import com.rkproduction.secondbrain.util.StreakCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class StreakUiState(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val activityMap: Map<LocalDate, Int> = emptyMap()
)

@HiltViewModel
class StreakViewModel @Inject constructor(
    private val dao: BrainEntryDao
) : ViewModel() {

    private val _state = MutableStateFlow(StreakUiState())
    val state: StateFlow<StreakUiState> = _state.asStateFlow()
    
    private val calculator = StreakCalculator()

    init {
        loadStreak()
    }

    private fun loadStreak() {
        viewModelScope.launch {
            val timestamps = dao.getAllTimestamps()
            
            val activityMap = timestamps.map {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            }.groupingBy { it }.eachCount()

            _state.update { it.copy(
                currentStreak = calculator.calculateCurrentStreak(timestamps),
                longestStreak = calculator.calculateLongestStreak(timestamps),
                activityMap = activityMap
            ) }
        }
    }
}
