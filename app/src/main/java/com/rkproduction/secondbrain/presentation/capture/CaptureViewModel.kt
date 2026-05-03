package com.rkproduction.secondbrain.presentation.capture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkproduction.secondbrain.data.remote.util.LinkMetadataFetcher
import com.rkproduction.secondbrain.domain.model.BrainEntry
import com.rkproduction.secondbrain.domain.model.EntryType
import com.rkproduction.secondbrain.domain.repository.BrainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val repository: BrainRepository,
    private val linkFetcher: LinkMetadataFetcher
) : ViewModel() {

    private val _state = MutableStateFlow(CaptureUiState())
    val state: StateFlow<CaptureUiState> = _state.asStateFlow()

    fun onTabSelected(type: EntryType) {
        _state.update { it.copy(selectedTab = type, inputText = "") }
    }

    fun onTextChanged(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    fun onTagInputChanged(text: String) {
        _state.update { it.copy(tagInput = text) }
    }

    fun startEditing(entry: BrainEntry) {
        _state.update { it.copy(
            selectedTab = entry.type,
            inputText = entry.content,
            tagInput = entry.tags.joinToString(", "),
            editingEntry = entry
        ) }
    }

    fun saveEntry() {
        val currentState = _state.value
        if (currentState.inputText.isBlank()) return

        _state.update { it.copy(isLoading = true) }

        val tags = currentState.tagInput.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        viewModelScope.launch {
            val (title, description, imageUrl) = if (currentState.selectedTab == EntryType.LINK) {
                // If the link changed, re-fetch metadata
                if (currentState.editingEntry?.url != currentState.inputText) {
                    val meta = linkFetcher.fetchMetadata(currentState.inputText)
                    Triple(meta.title ?: "Saved Link", meta.description, meta.imageUrl)
                } else {
                    Triple(
                        currentState.editingEntry.title,
                        currentState.editingEntry.description,
                        currentState.editingEntry.imageUrl
                    )
                }
            } else {
                Triple(
                    extractTitle(currentState.inputText, currentState.selectedTab),
                    null,
                    null
                )
            }

            val entry = currentState.editingEntry?.copy(
                title = title,
                content = currentState.inputText,
                type = currentState.selectedTab,
                tags = tags,
                timestamp = System.currentTimeMillis(),
                url = if (currentState.selectedTab == EntryType.LINK) currentState.inputText else null,
                description = description,
                imageUrl = imageUrl
            ) ?: BrainEntry(
                title = title,
                content = currentState.inputText,
                type = currentState.selectedTab,
                tags = tags,
                timestamp = System.currentTimeMillis(),
                url = if (currentState.selectedTab == EntryType.LINK) currentState.inputText else null,
                description = description,
                imageUrl = imageUrl
            )

            if (currentState.editingEntry != null) {
                repository.updateEntry(entry)
            } else {
                repository.insertEntry(entry)
            }
            
            _state.update { it.copy(inputText = "", tagInput = "", isLoading = false, editingEntry = null) }
        }
    }

    private fun extractTitle(text: String, type: EntryType): String {
        return when (type) {
            EntryType.NOTE -> text.take(30) + if (text.length > 30) "..." else ""
            EntryType.VOICE -> "Voice Memo"
            EntryType.IMAGE -> "Saved Image"
            EntryType.LINK -> "Saved Link"
        }
    }
}
