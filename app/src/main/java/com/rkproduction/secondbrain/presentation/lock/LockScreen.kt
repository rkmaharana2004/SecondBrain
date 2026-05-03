package com.rkproduction.secondbrain.presentation.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LockScreen(
    onAuthenticated: () -> Unit,
    viewModel: LockViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current as FragmentActivity

    // Auto-trigger biometric on start if available
    LaunchedEffect(Unit) {
        if (state.isBiometricAvailable) {
            viewModel.authenticateWithBiometrics(context)
        }
    }

    // Navigate when authenticated
    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onAuthenticated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Enter your PIN to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // PIN Circles
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(4) { index ->
                val isFilled = index < state.pinInput.length
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            if (isFilled) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Numeric Keypad
        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "BIO", "0", "DEL")
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.width(280.dp)
        ) {
            items(keys) { key ->
                KeypadButton(
                    key = key,
                    isBiometricAvailable = state.isBiometricAvailable,
                    onClick = {
                        when (key) {
                            "DEL" -> viewModel.onPinChanged(state.pinInput.dropLast(1))
                            "BIO" -> viewModel.authenticateWithBiometrics(context)
                            else -> if (key.all { it.isDigit() }) viewModel.onPinChanged(state.pinInput + key)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun KeypadButton(
    key: String,
    isBiometricAvailable: Boolean,
    onClick: () -> Unit
) {
    if (key == "BIO" && !isBiometricAvailable) {
        Spacer(modifier = Modifier.size(64.dp))
        return
    }

    FilledTonalIconButton(
        onClick = onClick,
        modifier = Modifier.size(72.dp),
        shape = CircleShape
    ) {
        when (key) {
            "DEL" -> Icon(Icons.Default.Backspace, contentDescription = "Delete")
            "BIO" -> Icon(Icons.Default.Fingerprint, contentDescription = "Biometric")
            else -> Text(text = key, fontSize = 24.sp, fontWeight = FontWeight.Medium)
        }
    }
}
