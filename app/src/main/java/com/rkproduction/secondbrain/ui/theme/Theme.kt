package com.rkproduction.secondbrain.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * AMOLED Support: Pure Black for OLED screens
 */
private val DarkAmoledColorScheme = darkColorScheme(
    primary = Color(0xFFE2E2E6),
    onPrimary = Color(0xFF1A1C1E),
    primaryContainer = Color(0xFF32353A),
    onPrimaryContainer = Color(0xFFE2E2E6),
    background = Color.Black,
    surface = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

// (Rest of the standard editorial theme remains similar, just adding AMOLED mode)
@Composable
fun SecondBrainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isAmoled: Boolean = false, // Fetched from DataStore in real app
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        isAmoled && darkTheme -> DarkAmoledColorScheme
        darkTheme -> darkColorScheme(primary = Color(0xFFE2E2E6), background = Color(0xFF1A1C1E))
        else -> lightColorScheme(primary = Charcoal, background = CreamBackground)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
