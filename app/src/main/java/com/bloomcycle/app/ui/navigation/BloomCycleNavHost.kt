package com.bloomcycle.app.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.ui.calendar.CalendarScreen
import com.bloomcycle.app.ui.dedication.DedicationScreen
import com.bloomcycle.app.ui.education.CyclePhaseGuideScreen
import com.bloomcycle.app.ui.education.HealthTipsScreen
import com.bloomcycle.app.ui.education.SymptomGuideScreen
import com.bloomcycle.app.ui.home.HomeScreen
import com.bloomcycle.app.ui.insights.InsightsScreen
import com.bloomcycle.app.ui.onboarding.OnboardingScreen
import com.bloomcycle.app.ui.privacy.PrivacyPolicyScreen
import com.bloomcycle.app.ui.reports.ReportsScreen
import com.bloomcycle.app.ui.settings.SettingsScreen
import com.bloomcycle.app.ui.tracking.DailyTrackingScreen
import java.time.LocalDate

// ── Shared transition specs ────────────────────────────────────
private const val NAV_ANIM_DURATION = 300

// Tabs (Home, Calendar, Insights, Settings) — subtle crossfade
private fun tabEnter(): EnterTransition = fadeIn(tween(NAV_ANIM_DURATION))
private fun tabExit(): ExitTransition = fadeOut(tween(NAV_ANIM_DURATION))

// Detail screens (push in from right, pop out to right)
private fun detailEnter(): EnterTransition =
    slideInHorizontally(tween(NAV_ANIM_DURATION)) { it / 3 } + fadeIn(tween(NAV_ANIM_DURATION))

private fun detailExit(): ExitTransition =
    fadeOut(tween(150))

private fun detailPopEnter(): EnterTransition =
    fadeIn(tween(NAV_ANIM_DURATION))

private fun detailPopExit(): ExitTransition =
    slideOutHorizontally(tween(NAV_ANIM_DURATION)) { it / 3 } + fadeOut(tween(NAV_ANIM_DURATION))

@Composable
fun BloomCycleNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { detailEnter() },
        exitTransition = { detailExit() },
        popEnterTransition = { detailPopEnter() },
        popExitTransition = { detailPopExit() }
    ) {
        // ── Tab destinations (crossfade) ────────────
        composable(
            Screen.Onboarding.route,
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(400)) }
        ) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            Screen.Home.route,
            enterTransition = { tabEnter() },
            exitTransition = { tabExit() }
        ) {
            HomeScreen(
                onNavigateToTracking = { date ->
                    navController.navigate(Screen.DailyTracking.createRoute(date))
                }
            )
        }
        composable(
            Screen.Calendar.route,
            enterTransition = { tabEnter() },
            exitTransition = { tabExit() }
        ) {
            CalendarScreen(
                onNavigateToTracking = { date ->
                    navController.navigate(Screen.DailyTracking.createRoute(date))
                }
            )
        }
        composable(
            route = Screen.DailyTracking.route,
            arguments = listOf(
                navArgument("date") {
                    type = NavType.StringType
                    defaultValue = LocalDate.now().toString()
                }
            )
        ) {
            DailyTrackingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            Screen.Insights.route,
            enterTransition = { tabEnter() },
            exitTransition = { tabExit() }
        ) {
            InsightsScreen(
                onNavigateToCycleGuide = {
                    navController.navigate(Screen.CyclePhaseGuide.route)
                },
                onNavigateToSymptomGuide = {
                    navController.navigate(Screen.SymptomGuide.route)
                },
                onNavigateToHealthTips = { phase ->
                    navController.navigate(Screen.HealthTips.createRoute(phase ?: "none"))
                }
            )
        }
        composable(Screen.CyclePhaseGuide.route) {
            CyclePhaseGuideScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.SymptomGuide.route) {
            SymptomGuideScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.HealthTips.route,
            arguments = listOf(
                navArgument("phase") {
                    type = NavType.StringType
                    defaultValue = "none"
                }
            )
        ) { backStackEntry ->
            val phaseArg = backStackEntry.arguments?.getString("phase") ?: "none"
            val currentPhase = try {
                CyclePhase.valueOf(phaseArg)
            } catch (_: IllegalArgumentException) {
                null
            }
            HealthTipsScreen(
                currentPhase = currentPhase,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Reports.route) {
            ReportsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        // ── Tab: Settings ────────────────────────────
        composable(
            Screen.Settings.route,
            enterTransition = { tabEnter() },
            exitTransition = { tabExit() }
        ) {
            SettingsScreen(
                onNavigateToReports = {
                    navController.navigate(Screen.Reports.route)
                },
                onNavigateToPrivacyPolicy = {
                    navController.navigate(Screen.PrivacyPolicy.route)
                },
                onNavigateToDedication = {
                    navController.navigate(Screen.Dedication.route)
                }
            )
        }
        composable(Screen.Dedication.route) {
            DedicationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
