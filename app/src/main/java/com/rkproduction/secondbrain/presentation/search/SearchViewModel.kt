package com.rkproduction.secondbrain.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkproduction.secondbrain.domain.model.BrainEntry
import com.rkproduction.secondbrain.domain.model.EntryType
import com.rkproduction.secondbrain.domain.repository.BrainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SearchUiState(
    val searchQuery: String = "",
    val selectedType: EntryType? = null,
    val results: List<BrainEntry> = emptyList(),
    val isSearching: Boolean = false
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: BrainRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedType = MutableStateFlow<EntryType?>(null)

    val uiState: StateFlow<SearchUiState> = combine(
        _searchQuery.debounce(300),
        _selectedType
    ) { query, type ->
        query to type
    }.flatMapLatest { (query, type) ->
        if (query.isBlank() && type == null) {
            flowOf(emptyList())
        } else {
            // If query is blank but type is selected, we should probably show all of that type
            // For now, let's just search. Room FTS requires at least some query or we use a different Dao method.
            repository.searchEntries(query).map { entries ->
                if (type != null) {
                    entries.filter { it.type == type }
                } else {
                    entries
                }
            }
        }
    }.combine(_searchQuery) { results, query ->
        SearchUiState(
            searchQuery = query,
            selectedType = _selectedType.value,
            results = results,
            isSearching = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState()
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onTypeFilterChange(type: EntryType?) {
        _selectedType.value = if (_selectedType.value == type) null else type
    }
}
