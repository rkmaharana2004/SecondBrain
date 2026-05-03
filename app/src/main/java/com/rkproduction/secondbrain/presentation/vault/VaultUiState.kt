package com.rkproduction.secondbrain.presentation.vault

import com.rkproduction.secondbrain.domain.model.BrainEntry

data class VaultUiState(
    val entries: List<BrainEntry> = emptyList(),
    val isLocked: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
)
