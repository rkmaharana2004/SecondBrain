package com.rkproduction.secondbrain.presentation.tagcloud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkproduction.secondbrain.data.local.db.BrainEntryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TagCloudUiState(
    val tags: List<TagCount> = emptyList()
)

data class TagCount(val tag: String, val count: Int)

@HiltViewModel
class TagCloudViewModel @Inject constructor(
    private val dao: BrainEntryDao
) : ViewModel() {

    private val _state = MutableStateFlow(TagCloudUiState())
    val state: StateFlow<TagCloudUiState> = _state.asStateFlow()

    init {
        loadTags()
    }

    private fun loadTags() {
        viewModelScope.launch {
            val rawTags = dao.getAllTagsRaw()
            val counts = rawTags.flatMap { it.split(",") }
                .filter { it.isNotBlank() }
                .groupingBy { it }
                .eachCount()
                .map { TagCount(it.key, it.value) }
                .sortedByDescending { it.count }
            
            _state.update { it.copy(tags = counts) }
        }
    }
}
