package com.rkproduction.secondbrain.presentation.vault

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkproduction.secondbrain.domain.repository.BrainRepository
import com.rkproduction.secondbrain.util.BiometricAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val repository: BrainRepository,
    private val biometricAuthManager: BiometricAuthManager
) : ViewModel() {

    private val _state = MutableStateFlow(VaultUiState())
    val state: StateFlow<VaultUiState> = _state.asStateFlow()

    init {
        // Automatically fetch private entries, but they won't be shown until isLocked is false
        viewModelScope.launch {
            repository.getPrivateEntries().collect { entries ->
                _state.update { it.copy(entries = entries) }
            }
        }
    }

    fun authenticate(activity: FragmentActivity) {
        viewModelScope.launch {
            biometricAuthManager.authenticate(activity).collect { result ->
                when (result) {
                    is BiometricAuthManager.AuthResult.Success -> {
                        _state.update { it.copy(isLocked = false) }
                    }
                    is BiometricAuthManager.AuthResult.Failed -> {
                        _state.update { it.copy(error = "Authentication Failed") }
                    }
                    is BiometricAuthManager.AuthResult.Error -> {
                        _state.update { it.copy(error = result.message) }
                    }
                }
            }
        }
    }

    fun togglePrivacy(entryId: Int) {
        viewModelScope.launch {
            repository.togglePrivacy(entryId, false) // Move back to public
        }
    }
}
