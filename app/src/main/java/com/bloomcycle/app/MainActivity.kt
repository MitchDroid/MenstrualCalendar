package com.bloomcycle.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.data.security.BiometricAuthManager
import com.bloomcycle.app.data.security.BiometricStatus
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.ui.components.BloomCycleBottomBar
import com.bloomcycle.app.ui.navigation.BloomCycleNavHost
import com.bloomcycle.app.ui.navigation.Screen
import com.bloomcycle.app.ui.privacy.AppLockScreen
import com.bloomcycle.app.ui.splash.SplashScreen
import com.bloomcycle.app.ui.theme.BloomCycleTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * ViewModel that exposes onboarding state, security preferences,
 * and the current cycle phase for the phase-aware theme.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: UserPreferencesManager,
    val biometricAuthManager: BiometricAuthManager
) : ViewModel() {

    sealed interface StartState {
        data object Loading : StartState
        data object Onboarding : StartState
        data object Main : StartState
    }

    val startState: Flow<StartState> = preferencesManager.onboardingCompleted.map { completed ->
        if (completed) StartState.Main else StartState.Onboarding
    }

    val biometricEnabled: Flow<Boolean> = preferencesManager.biometricEnabled

    val screenSecurityEnabled: Flow<Boolean> = preferencesManager.screenSecurityEnabled

    /**
     * Current cycle phase derived from preferences.
     * Used by [BloomCycleTheme] to tint the entire UI.
     * Null during onboarding or when last-period data is unavailable.
     */
    val currentCyclePhase: Flow<CyclePhase?> = combine(
        preferencesManager.lastPeriodDate,
        preferencesManager.averageCycleLength,
        preferencesManager.averagePeriodDuration
    ) { lastPeriod, cycleLen, periodDur ->
        if (lastPeriod == null) return@combine null

        val daysSince = ChronoUnit.DAYS.between(lastPeriod, LocalDate.now()).toInt()
        val cycleDay = (daysSince % cycleLen) + 1
        val ovulationDay = cycleLen - 14

        when {
            cycleDay in 1..periodDur -> CyclePhase.MENSTRUAL
            cycleDay in (periodDur + 1) until ovulationDay -> CyclePhase.FOLLICULAR
            cycleDay in ovulationDay..(ovulationDay + 1) -> CyclePhase.OVULATION
            else -> CyclePhase.LUTEAL
        }
    }
}

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    // Track authentication state across configuration changes
    private var isAuthenticated by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Restore auth state on config change
        if (savedInstanceState != null) {
            isAuthenticated = savedInstanceState.getBoolean("is_authenticated", false)
        }

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val cyclePhase by viewModel.currentCyclePhase.collectAsStateWithLifecycle(
                initialValue = null
            )

            BloomCycleTheme(cyclePhase = cyclePhase) {
                BloomCycleApp(
                    activity = this,
                    isAuthenticated = isAuthenticated,
                    onAuthenticated = { isAuthenticated = true },
                    onLockRequired = { isAuthenticated = false },
                    viewModel = viewModel
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("is_authenticated", isAuthenticated)
    }
}

@Composable
private fun BloomCycleApp(
    activity: FragmentActivity,
    isAuthenticated: Boolean,
    onAuthenticated: () -> Unit,
    onLockRequired: () -> Unit,
    viewModel: MainViewModel
) {
    val startState by viewModel.startState.collectAsStateWithLifecycle(
        initialValue = MainViewModel.StartState.Loading
    )
    val biometricEnabled by viewModel.biometricEnabled.collectAsStateWithLifecycle(
        initialValue = false
    )
    val screenSecurityEnabled by viewModel.screenSecurityEnabled.collectAsStateWithLifecycle(
        initialValue = false
    )

    // ── FLAG_SECURE management ──────────────────────────
    DisposableEffect(screenSecurityEnabled) {
        if (screenSecurityEnabled) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
        onDispose {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    // ── Biometric gate on app resume ────────────────────
    // rememberUpdatedState ensures the observer lambda always reads the
    // latest values, avoiding stale captures across recompositions.
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentIsAuthenticated by rememberUpdatedState(isAuthenticated)
    val currentOnAuthenticated by rememberUpdatedState(onAuthenticated)
    val currentOnLockRequired by rememberUpdatedState(onLockRequired)

    DisposableEffect(lifecycleOwner, biometricEnabled) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    // Reset auth when app goes to background so the user
                    // must re-authenticate on every return.
                    if (biometricEnabled && currentIsAuthenticated) {
                        currentOnLockRequired()
                    }
                }
                Lifecycle.Event.ON_START -> {
                    if (biometricEnabled && !currentIsAuthenticated) {
                        val status = viewModel.biometricAuthManager.getStatus()
                        if (status == BiometricStatus.AVAILABLE) {
                            viewModel.biometricAuthManager.authenticate(
                                activity = activity,
                                onSuccess = { currentOnAuthenticated() },
                                onError = { /* Stay locked — user can tap Unlock button */ }
                            )
                        } else {
                            // Biometric not available — unlock automatically
                            currentOnAuthenticated()
                        }
                    }
                }
                else -> { /* Other lifecycle events — no action needed */ }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // ── Lock Screen Gate ────────────────────────────────
    if (biometricEnabled && !isAuthenticated) {
        AppLockScreen(
            onUnlockClick = {
                val status = viewModel.biometricAuthManager.getStatus()
                if (status == BiometricStatus.AVAILABLE) {
                    viewModel.biometricAuthManager.authenticate(
                        activity = activity,
                        onSuccess = { onAuthenticated() },
                        onError = { /* Stay locked */ }
                    )
                } else {
                    onAuthenticated()
                }
            }
        )
        return
    }

    // ── Minimum splash duration ────────────────────────
    // DataStore resolves almost instantly, but the bloom animation
    // takes ~1.2s. Hold the splash for at least 1.5s so it fully plays.
    var splashFinished by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(1500L)
        splashFinished = true
    }

    val showSplash = !splashFinished || startState is MainViewModel.StartState.Loading

    // ── Main App Content ────────────────────────────────
    if (showSplash) {
        SplashScreen(modifier = Modifier.fillMaxSize())
        return
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide the bottom bar during onboarding and full-screen flows
    val showBottomBar = currentRoute != null &&
            currentRoute != Screen.Onboarding.route &&
            !currentRoute.startsWith("daily_tracking") &&
            currentRoute != Screen.CyclePhaseGuide.route &&
            currentRoute != Screen.SymptomGuide.route &&
            !currentRoute.startsWith("health_tips") &&
            currentRoute != Screen.Reports.route &&
            currentRoute != Screen.PrivacyPolicy.route

    val startDestination = when (startState) {
        MainViewModel.StartState.Onboarding -> Screen.Onboarding.route
        else -> Screen.Home.route
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BloomCycleBottomBar(
                    navController = navController,
                    onQuickLogClick = {
                        navController.navigate(
                            Screen.DailyTracking.createRoute(LocalDate.now().toString())
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        BloomCycleNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
