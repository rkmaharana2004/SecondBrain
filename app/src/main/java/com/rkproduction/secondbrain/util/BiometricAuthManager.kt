package com.rkproduction.secondbrain.util

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

/**
 * A wrapper to handle Biometric authentication logic.
 */
class BiometricAuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Check if the device is capable of biometric auth or has a PIN set up.
     */
    fun canAuthenticate(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    /**
     * Launch the system biometric prompt.
     * returns a Flow that emits [AuthResult]
     */
    fun authenticate(activity: FragmentActivity): Flow<AuthResult> = callbackFlow {
        val executor = ContextCompat.getMainExecutor(activity)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Second Brain Secure Lock")
            .setSubtitle("Authenticate to access your notes")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    trySend(AuthResult.Error(errString.toString()))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    trySend(AuthResult.Success)
                }

                override fun onAuthenticationFailed() {
                    trySend(AuthResult.Failed)
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)

        // callbackFlow requires awaitClose to clean up resources
        awaitClose { /* No specific cleanup needed for BiometricPrompt */ }
    }

    sealed class AuthResult {
        object Success : AuthResult()
        object Failed : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
}
