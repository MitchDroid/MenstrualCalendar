package com.bloomcycle.app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Brand Colors ──────────────────────────────────────────────
val SoftPink = Color(0xFFF8BBD0)
val Lavender = Color(0xFFCE93D8)
val Coral = Color(0xFFFF8A80)
val Cream = Color(0xFFFFF8E1)

// ── Light Theme Colors ────────────────────────────────────────
val PinkPrimary = Color(0xFFD81B60)
val PinkPrimaryContainer = Color(0xFFFCE4EC)
val OnPinkPrimaryContainer = Color(0xFF3E0021)

val LavenderSecondary = Color(0xFF8E24AA)
val LavenderSecondaryContainer = Color(0xFFF3E5F5)
val OnLavenderSecondaryContainer = Color(0xFF36003C)

val CoralTertiary = Color(0xFFE53935)
val CoralTertiaryContainer = Color(0xFFFFEBEE)
val OnCoralTertiaryContainer = Color(0xFF410002)

val LightBackground = Color(0xFFFFFBFE)
val LightSurface = Color(0xFFFFFBFE)
val LightSurfaceVariant = Color(0xFFFCE4EC)
val LightOnSurface = Color(0xFF1C1B1F)
val LightOnSurfaceVariant = Color(0xFF534349)
val LightOutline = Color(0xFF857379)

// ── Dark Theme Colors ─────────────────────────────────────────
val DarkPinkPrimary = Color(0xFFF8BBD0)
val DarkPinkPrimaryContainer = Color(0xFF8C0D48)
val DarkOnPinkPrimaryContainer = Color(0xFFFCE4EC)

val DarkLavenderSecondary = Color(0xFFCE93D8)
val DarkLavenderSecondaryContainer = Color(0xFF5C1070)
val DarkOnLavenderSecondaryContainer = Color(0xFFF3E5F5)

val DarkCoralTertiary = Color(0xFFFF8A80)
val DarkCoralTertiaryContainer = Color(0xFF930012)
val DarkOnCoralTertiaryContainer = Color(0xFFFFEBEE)

val DarkBackground = Color(0xFF1C1B1F)
val DarkSurface = Color(0xFF1C1B1F)
val DarkSurfaceVariant = Color(0xFF3E2D33)
val DarkOnSurface = Color(0xFFE6E1E5)
val DarkOnSurfaceVariant = Color(0xFFD6C2C8)
val DarkOutline = Color(0xFF9F8C93)

// ── Calendar Marker Colors ────────────────────────────────────
val PeriodRed = Color(0xFFEF5350)
val PredictedPurple = Color(0xFFAB47BC)
val FertileGreen = Color(0xFF66BB6A)
val OvulationYellow = Color(0xFFFFCA28)
val RegularDay = Color(0xFFE0E0E0)

// ── Status Colors ─────────────────────────────────────────────
val SuccessGreen = Color(0xFF4CAF50)
val WarningAmber = Color(0xFFFFC107)
val ErrorRed = Color(0xFFF44336)
val InfoBlue = Color(0xFF2196F3)

// ══════════════════════════════════════════════════════════════
// ── Phase-Aware Dynamic Theme Colors ─────────────────────────
// ══════════════════════════════════════════════════════════════
// Each cycle phase tints the UI with a subtle, warm palette shift.
// These are used as overrides inside BloomCycleTheme when a phase
// is known, creating a living, phase-responsive visual identity.

// ── Menstrual Phase (warm rose / coral tones) ────────────────
val MenstrualPrimary = Color(0xFFD32F2F)
val MenstrualPrimaryContainer = Color(0xFFFFCDD2)
val MenstrualSecondary = Color(0xFFC2185B)
val MenstrualSecondaryContainer = Color(0xFFFCE4EC)
val MenstrualSurfaceVariant = Color(0xFFFFF0F0)

val DarkMenstrualPrimary = Color(0xFFEF9A9A)
val DarkMenstrualPrimaryContainer = Color(0xFF8E0000)
val DarkMenstrualSecondary = Color(0xFFF48FB1)
val DarkMenstrualSecondaryContainer = Color(0xFF6A0036)
val DarkMenstrualSurfaceVariant = Color(0xFF3D2527)

// ── Follicular Phase (fresh mint / green tones) ──────────────
val FollicularPrimary = Color(0xFF2E7D32)
val FollicularPrimaryContainer = Color(0xFFC8E6C9)
val FollicularSecondary = Color(0xFF00796B)
val FollicularSecondaryContainer = Color(0xFFE0F2F1)
val FollicularSurfaceVariant = Color(0xFFF0FFF0)

val DarkFollicularPrimary = Color(0xFFA5D6A7)
val DarkFollicularPrimaryContainer = Color(0xFF1B5E20)
val DarkFollicularSecondary = Color(0xFF80CBC4)
val DarkFollicularSecondaryContainer = Color(0xFF004D40)
val DarkFollicularSurfaceVariant = Color(0xFF253D2A)

// ── Ovulation Phase (soft pink / lilac tones) ────────────────
val OvulationPrimary = Color(0xFFAD1457)
val OvulationPrimaryContainer = Color(0xFFF8BBD0)
val OvulationSecondary = Color(0xFF7B1FA2)
val OvulationSecondaryContainer = Color(0xFFE1BEE7)
val OvulationSurfaceVariant = Color(0xFFFFF0F7)

val DarkOvulationPrimary = Color(0xFFF48FB1)
val DarkOvulationPrimaryContainer = Color(0xFF880E4F)
val DarkOvulationSecondary = Color(0xFFCE93D8)
val DarkOvulationSecondaryContainer = Color(0xFF4A148C)
val DarkOvulationSurfaceVariant = Color(0xFF3D2535)

// ── Luteal Phase (warm amber / terracotta tones) ─────────────
val LutealPrimary = Color(0xFFE65100)
val LutealPrimaryContainer = Color(0xFFFFE0B2)
val LutealSecondary = Color(0xFF6D4C41)
val LutealSecondaryContainer = Color(0xFFD7CCC8)
val LutealSurfaceVariant = Color(0xFFFFF8F0)

val DarkLutealPrimary = Color(0xFFFFCC80)
val DarkLutealPrimaryContainer = Color(0xFFBF360C)
val DarkLutealSecondary = Color(0xFFBCAAA4)
val DarkLutealSecondaryContainer = Color(0xFF3E2723)
val DarkLutealSurfaceVariant = Color(0xFF3D3025)
