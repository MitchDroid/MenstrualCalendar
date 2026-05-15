package com.bloomcycle.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.bloomcycle.app.domain.model.CyclePhase

/**
 * BloomCycle's root theme.
 *
 * @param cyclePhase The user's current cycle phase, or `null` during
 *   onboarding / loading. When non-null the primary and secondary color
 *   slots animate toward the phase-specific palette, making the entire
 *   app feel responsive to the user's cycle.
 * @param darkTheme Whether to use the dark color scheme.
 * @param content The composable content to theme.
 */
@Composable
fun BloomCycleTheme(
    cyclePhase: CyclePhase? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Build the animated, phase-aware color scheme
    val colorScheme = phaseAwareColorScheme(phase = cyclePhase, darkTheme = darkTheme)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Let the system draw behind the status bar (edge-to-edge)
            // and just control the icon contrast — no deprecated statusBarColor.
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Provide the current phase through CompositionLocal so any
    // composable in the tree can read it with LocalCyclePhase.current
    CompositionLocalProvider(LocalCyclePhase provides cyclePhase) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = BloomCycleTypography,
            shapes = BloomCycleShapes,
            content = content
        )
    }
}
