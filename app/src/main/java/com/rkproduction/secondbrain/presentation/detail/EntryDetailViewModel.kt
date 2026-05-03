package com.rkproduction.secondbrain.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkproduction.secondbrain.domain.model.BrainEntry
import com.rkproduction.secondbrain.domain.repository.BrainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryDetailViewModel @Inject constructor(
    private val repository: BrainRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val entryId: Int = checkNotNull(savedStateHandle["entryId"])

    val entry = repository.getEntryById(entryId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun deleteEntry(entry: BrainEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    fun togglePrivacy(entry: BrainEntry) {
        viewModelScope.launch {
            repository.togglePrivacy(entry.id, !entry.isPrivate)
        }
    }
}
