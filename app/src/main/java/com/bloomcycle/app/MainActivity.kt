package com.bloomcycle.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.ui.components.BloomCycleBottomBar
import com.bloomcycle.app.ui.navigation.BloomCycleNavHost
import com.bloomcycle.app.ui.navigation.Screen
import com.bloomcycle.app.ui.theme.BloomCycleTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Minimal ViewModel that exposes the onboarding-completed flag.
 * Keeps MainActivity thin while giving Compose a lifecycle-aware observable.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: UserPreferencesManager
) : ViewModel() {

    sealed interface StartState {
        data object Loading : StartState
        data object Onboarding : StartState
        data object Main : StartState
    }

    val startState: Flow<StartState> = preferencesManager.onboardingCompleted.map { completed ->
        if (completed) StartState.Main else StartState.Onboarding
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BloomCycleTheme {
                BloomCycleApp()
            }
        }
    }
}

@Composable
private fun BloomCycleApp(
    viewModel: MainViewModel = hiltViewModel()
) {
    val startState by viewModel.startState.collectAsStateWithLifecycle(
        initialValue = MainViewModel.StartState.Loading
    )

    when (startState) {
        MainViewModel.StartState.Loading -> {
            // Brief splash while DataStore loads
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        MainViewModel.StartState.Onboarding,
        MainViewModel.StartState.Main -> {
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
                    currentRoute != Screen.Reports.route

            val startDestination = when (startState) {
                MainViewModel.StartState.Onboarding -> Screen.Onboarding.route
                else -> Screen.Home.route
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (showBottomBar) {
                        BloomCycleBottomBar(navController = navController)
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
    }
}
