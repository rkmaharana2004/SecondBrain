package com.rkproduction.secondbrain.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkproduction.secondbrain.util.AppLockManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLockEnabled: Boolean = false,
    val currentPin: String? = null,
    val newPinInput: String = "",
    val isChangingPin: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appLockManager: AppLockManager
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                appLockManager.isLockEnabled(),
                appLockManager.getPin()
            ) { enabled, pin ->
                _state.update { it.copy(isLockEnabled = enabled, currentPin = pin) }
            }.collect()
        }
    }

    fun toggleLock(enabled: Boolean) {
        if (enabled && _state.value.currentPin == null) {
            _state.update { it.copy(message = "Please set a PIN first") }
            return
        }
        viewModelScope.launch {
            appLockManager.setLockEnabled(enabled)
        }
    }

    fun onNewPinChange(pin: String) {
        if (pin.length <= 4) {
            _state.update { it.copy(newPinInput = pin) }
        }
    }

    fun updatePin() {
        val newPin = _state.value.newPinInput
        if (newPin.length == 4) {
            viewModelScope.launch {
                appLockManager.setPin(newPin)
                _state.update { it.copy(newPinInput = "", message = "PIN Updated successfully!", isChangingPin = false) }
            }
        } else {
            _state.update { it.copy(message = "PIN must be 4 digits") }
        }
    }
    
    fun setChangingPin(changing: Boolean) {
        _state.update { it.copy(isChangingPin = changing, message = null) }
    }
    
    fun clearMessage() {
        _state.update { it.copy(message = null) }
    }
}
