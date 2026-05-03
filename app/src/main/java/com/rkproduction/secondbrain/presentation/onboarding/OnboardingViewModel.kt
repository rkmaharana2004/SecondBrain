package com.rkproduction.secondbrain.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkproduction.secondbrain.util.AppLockManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val pinInput: String = "",
    val confirmPinInput: String = "",
    val step: Int = 1, // 1: Initial PIN, 2: Confirm PIN
    val isComplete: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val appLockManager: AppLockManager
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun onPinInput(digit: String) {
        if (digit == "DEL") {
            if (_state.value.step == 1) {
                _state.update { it.copy(pinInput = it.pinInput.dropLast(1), error = null) }
            } else {
                _state.update { it.copy(confirmPinInput = it.confirmPinInput.dropLast(1), error = null) }
            }
            return
        }

        if (_state.value.step == 1) {
            if (_state.value.pinInput.length < 4) {
                val newInput = _state.value.pinInput + digit
                _state.update { it.copy(pinInput = newInput) }
                if (newInput.length == 4) {
                    _state.update { it.copy(step = 2) }
                }
            }
        } else {
            if (_state.value.confirmPinInput.length < 4) {
                val newInput = _state.value.confirmPinInput + digit
                _state.update { it.copy(confirmPinInput = newInput) }
                if (newInput.length == 4) {
                    validateAndSave()
                }
            }
        }
    }

    private fun validateAndSave() {
        if (_state.value.pinInput == _state.value.confirmPinInput) {
            viewModelScope.launch {
                appLockManager.setPin(_state.value.pinInput)
                appLockManager.setLockEnabled(true)
                _state.update { it.copy(isComplete = true) }
            }
        } else {
            _state.update { it.copy(confirmPinInput = "", error = "PINs do not match. Try again.", step = 1, pinInput = "") }
        }
    }
}
