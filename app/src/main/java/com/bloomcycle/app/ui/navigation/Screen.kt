package com.bloomcycle.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class representing all navigable screens in BloomCycle.
 */
sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    data object Onboarding : Screen(
        route = "onboarding",
        title = "Onboarding"
    )

    data object Home : Screen(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Calendar : Screen(
        route = "calendar",
        title = "Calendar",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    )

    data object Insights : Screen(
        route = "insights",
        title = "Insights",
        selectedIcon = Icons.Filled.Insights,
        unselectedIcon = Icons.Outlined.Insights
    )

    data object DailyTracking : Screen(
        route = "daily_tracking/{date}",
        title = "Daily Log"
    ) {
        fun createRoute(date: String) = "daily_tracking/$date"
    }

    data object CyclePhaseGuide : Screen(
        route = "cycle_phase_guide",
        title = "Cycle Phases"
    )

    data object SymptomGuide : Screen(
        route = "symptom_guide",
        title = "Symptom Guide"
    )

    data object HealthTips : Screen(
        route = "health_tips/{phase}",
        title = "Health Tips"
    ) {
        fun createRoute(phase: String = "none") = "health_tips/$phase"
    }

    data object Reports : Screen(
        route = "reports",
        title = "Reports & Export"
    )

    data object PrivacyPolicy : Screen(
        route = "privacy_policy",
        title = "Privacy & Data"
    )

    data object Settings : Screen(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    data object Dedication : Screen(
        route = "dedication",
        title = "Dedication"
    )

    companion object {
        /** Screens shown in the bottom navigation bar. */
        val bottomNavItems = listOf(Home, Calendar, Insights, Settings)
    }
}
