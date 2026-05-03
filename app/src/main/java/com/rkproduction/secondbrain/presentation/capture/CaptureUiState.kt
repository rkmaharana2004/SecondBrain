package com.rkproduction.secondbrain.presentation.capture

import com.rkproduction.secondbrain.domain.model.EntryType

data class CaptureUiState(
    val selectedTab: EntryType = EntryType.NOTE,
    val inputText: String = "",
    val tagInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val editingEntry: com.rkproduction.secondbrain.domain.model.BrainEntry? = null
)