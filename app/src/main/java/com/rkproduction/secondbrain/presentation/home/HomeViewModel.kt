package com.rkproduction.secondbrain.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkproduction.secondbrain.domain.repository.BrainRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BrainRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = repository.getAllEntries()
        .map { entries -> HomeUiState.Success(entries) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading
        )

    fun onSearchQueryChange(query: String) {
        // We will implement search in the next phase
    }

    fun deleteEntry(entry: com.rkproduction.secondbrain.domain.model.BrainEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    fun togglePrivacy(entry: com.rkproduction.secondbrain.domain.model.BrainEntry) {
        viewModelScope.launch {
            repository.togglePrivacy(entry.id, !entry.isPrivate)
        }
    }
}