package com.bloomcycle.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.ui.calendar.CalendarScreen
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

@Composable
fun BloomCycleNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTracking = { date ->
                    navController.navigate(Screen.DailyTracking.createRoute(date))
                }
            )
        }
        composable(Screen.Calendar.route) {
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
        composable(Screen.Insights.route) {
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
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToReports = {
                    navController.navigate(Screen.Reports.route)
                },
                onNavigateToPrivacyPolicy = {
                    navController.navigate(Screen.PrivacyPolicy.route)
                }
            )
        }
    }
}
