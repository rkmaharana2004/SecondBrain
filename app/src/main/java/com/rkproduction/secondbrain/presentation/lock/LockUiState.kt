package com.rkproduction.secondbrain.presentation.lock

data class LockUiState(
    val isAuthenticated: Boolean = false,
    val isBiometricAvailable: Boolean = false,
    val errorMessage: String? = null,
    val pinInput: String = ""
)
