package com.bloomcycle.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = PinkPrimaryContainer,
    onPrimaryContainer = OnPinkPrimaryContainer,
    secondary = LavenderSecondary,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = LavenderSecondaryContainer,
    onSecondaryContainer = OnLavenderSecondaryContainer,
    tertiary = CoralTertiary,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = CoralTertiaryContainer,
    onTertiaryContainer = OnCoralTertiaryContainer,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPinkPrimary,
    onPrimary = OnPinkPrimaryContainer,
    primaryContainer = DarkPinkPrimaryContainer,
    onPrimaryContainer = DarkOnPinkPrimaryContainer,
    secondary = DarkLavenderSecondary,
    onSecondary = OnLavenderSecondaryContainer,
    secondaryContainer = DarkLavenderSecondaryContainer,
    onSecondaryContainer = DarkOnLavenderSecondaryContainer,
    tertiary = DarkCoralTertiary,
    onTertiary = OnCoralTertiaryContainer,
    tertiaryContainer = DarkCoralTertiaryContainer,
    onTertiaryContainer = DarkOnCoralTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

@Composable
fun BloomCycleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BloomCycleTypography,
        shapes = BloomCycleShapes,
        content = content
    )
}
