package com.rkproduction.secondbrain.presentation.home

import com.rkproduction.secondbrain.domain.model.BrainEntry

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val entries: List<BrainEntry>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}