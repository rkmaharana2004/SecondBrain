package com.rkproduction.secondbrain.presentation.lock

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkproduction.secondbrain.util.AppLockManager
import com.rkproduction.secondbrain.util.BiometricAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockViewModel @Inject constructor(
    private val biometricAuthManager: BiometricAuthManager,
    private val appLockManager: AppLockManager
) : ViewModel() {

    private val _state = MutableStateFlow(LockUiState())
    val state: StateFlow<LockUiState> = _state.asStateFlow()

    init {
        _state.update { it.copy(isBiometricAvailable = biometricAuthManager.canAuthenticate()) }
    }

    fun onPinChanged(newPin: String) {
        if (newPin.length <= 4) {
            _state.update { it.copy(pinInput = newPin, errorMessage = null) }
            if (newPin.length == 4) {
                verifyPin(newPin)
            }
        }
    }

    private fun verifyPin(pin: String) {
        viewModelScope.launch {
            val savedPin = appLockManager.getPin().first()
            if (pin == savedPin) {
                _state.update { it.copy(isAuthenticated = true) }
            } else {
                _state.update { it.copy(pinInput = "", errorMessage = "Invalid PIN. Try again.") }
            }
        }
    }

    fun authenticateWithBiometrics(activity: FragmentActivity) {
        viewModelScope.launch {
            biometricAuthManager.authenticate(activity).collect { result ->
                when (result) {
                    is BiometricAuthManager.AuthResult.Success -> {
                        _state.update { it.copy(isAuthenticated = true) }
                    }
                    is BiometricAuthManager.AuthResult.Failed -> {
                        _state.update { it.copy(errorMessage = "Authentication failed") }
                    }
                    is BiometricAuthManager.AuthResult.Error -> {
                        _state.update { it.copy(errorMessage = result.message) }
                    }
                }
            }
        }
    }
}
