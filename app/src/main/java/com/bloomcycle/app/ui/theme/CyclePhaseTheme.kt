package com.bloomcycle.app.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.bloomcycle.app.domain.model.CyclePhase

/**
 * CompositionLocal that carries the current [CyclePhase] (or null when
 * the phase is unknown — e.g. during onboarding or before data loads).
 *
 * Every composable in the tree can read `LocalCyclePhase.current` to
 * react to the user's cycle phase.
 */
val LocalCyclePhase = compositionLocalOf<CyclePhase?> { null }

// ── Phase-aware color overrides ──────────────────────────────────

/**
 * Returns a [ColorScheme] whose primary/secondary/surfaceVariant slots are
 * **animated** toward the active cycle-phase palette. When [phase] is null
 * the base BloomCycle colors are returned unchanged.
 *
 * The animation uses a 600 ms tween so the palette shift feels organic,
 * not jarring — like the app is breathing with the user's cycle.
 */
@Composable
fun phaseAwareColorScheme(
    phase: CyclePhase?,
    darkTheme: Boolean
): ColorScheme {
    // ── Pick target colors per phase ─────────────────────────
    val targets = resolvePhaseColors(phase, darkTheme)

    // ── Animate each slot for a smooth transition ────────────
    val animSpec = tween<Color>(durationMillis = 600)

    val primary by animateColorAsState(targets.primary, animSpec, label = "primary")
    val primaryContainer by animateColorAsState(targets.primaryContainer, animSpec, label = "primaryContainer")
    val secondary by animateColorAsState(targets.secondary, animSpec, label = "secondary")
    val secondaryContainer by animateColorAsState(targets.secondaryContainer, animSpec, label = "secondaryContainer")
    val surfaceVariant by animateColorAsState(targets.surfaceVariant, animSpec, label = "surfaceVariant")

    // ── Build the full scheme, overriding only the phase slots ──
    return if (darkTheme) {
        darkColorScheme(
            primary = primary,
            onPrimary = OnPinkPrimaryContainer,
            primaryContainer = primaryContainer,
            onPrimaryContainer = DarkOnPinkPrimaryContainer,
            secondary = secondary,
            onSecondary = OnLavenderSecondaryContainer,
            secondaryContainer = secondaryContainer,
            onSecondaryContainer = DarkOnLavenderSecondaryContainer,
            tertiary = DarkCoralTertiary,
            onTertiary = OnCoralTertiaryContainer,
            tertiaryContainer = DarkCoralTertiaryContainer,
            onTertiaryContainer = DarkOnCoralTertiaryContainer,
            background = DarkBackground,
            onBackground = DarkOnSurface,
            surface = DarkSurface,
            onSurface = DarkOnSurface,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = DarkOnSurfaceVariant,
            outline = DarkOutline
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primaryContainer,
            onPrimaryContainer = OnPinkPrimaryContainer,
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = secondaryContainer,
            onSecondaryContainer = OnLavenderSecondaryContainer,
            tertiary = CoralTertiary,
            onTertiary = Color.White,
            tertiaryContainer = CoralTertiaryContainer,
            onTertiaryContainer = OnCoralTertiaryContainer,
            background = LightBackground,
            onBackground = LightOnSurface,
            surface = LightSurface,
            onSurface = LightOnSurface,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = LightOnSurfaceVariant,
            outline = LightOutline
        )
    }
}

// ── Internal: resolve target colors for a given phase ────────────

private data class PhaseColors(
    val primary: Color,
    val primaryContainer: Color,
    val secondary: Color,
    val secondaryContainer: Color,
    val surfaceVariant: Color
)

private fun resolvePhaseColors(phase: CyclePhase?, darkTheme: Boolean): PhaseColors {
    return if (darkTheme) {
        when (phase) {
            CyclePhase.MENSTRUAL -> PhaseColors(
                DarkMenstrualPrimary, DarkMenstrualPrimaryContainer,
                DarkMenstrualSecondary, DarkMenstrualSecondaryContainer,
                DarkMenstrualSurfaceVariant
            )
            CyclePhase.FOLLICULAR -> PhaseColors(
                DarkFollicularPrimary, DarkFollicularPrimaryContainer,
                DarkFollicularSecondary, DarkFollicularSecondaryContainer,
                DarkFollicularSurfaceVariant
            )
            CyclePhase.OVULATION -> PhaseColors(
                DarkOvulationPrimary, DarkOvulationPrimaryContainer,
                DarkOvulationSecondary, DarkOvulationSecondaryContainer,
                DarkOvulationSurfaceVariant
            )
            CyclePhase.LUTEAL -> PhaseColors(
                DarkLutealPrimary, DarkLutealPrimaryContainer,
                DarkLutealSecondary, DarkLutealSecondaryContainer,
                DarkLutealSurfaceVariant
            )
            null -> PhaseColors(
                DarkPinkPrimary, DarkPinkPrimaryContainer,
                DarkLavenderSecondary, DarkLavenderSecondaryContainer,
                DarkSurfaceVariant
            )
        }
    } else {
        when (phase) {
            CyclePhase.MENSTRUAL -> PhaseColors(
                MenstrualPrimary, MenstrualPrimaryContainer,
                MenstrualSecondary, MenstrualSecondaryContainer,
                MenstrualSurfaceVariant
            )
            CyclePhase.FOLLICULAR -> PhaseColors(
                FollicularPrimary, FollicularPrimaryContainer,
                FollicularSecondary, FollicularSecondaryContainer,
                FollicularSurfaceVariant
            )
            CyclePhase.OVULATION -> PhaseColors(
                OvulationPrimary, OvulationPrimaryContainer,
                OvulationSecondary, OvulationSecondaryContainer,
                OvulationSurfaceVariant
            )
            CyclePhase.LUTEAL -> PhaseColors(
                LutealPrimary, LutealPrimaryContainer,
                LutealSecondary, LutealSecondaryContainer,
                LutealSurfaceVariant
            )
            null -> PhaseColors(
                PinkPrimary, PinkPrimaryContainer,
                LavenderSecondary, LavenderSecondaryContainer,
                LightSurfaceVariant
            )
        }
    }
}
