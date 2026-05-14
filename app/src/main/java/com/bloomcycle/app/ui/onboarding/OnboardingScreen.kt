package com.bloomcycle.app.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bloomcycle.app.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigate away when onboarding is complete
    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onOnboardingComplete()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // ── Top Bar: Back button + Progress ──────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.canGoBack) {
                IconButton(onClick = { viewModel.previousStep() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.onboarding_step_indicator, uiState.stepIndex + 1, uiState.totalSteps),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { uiState.progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // Skip button (only on welcome)
            if (uiState.currentStep == OnboardingStep.WELCOME) {
                TextButton(onClick = { /* Could skip later */ }) {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        // ── Step Content ─────────────────────────────────────
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                    (slideInHorizontally { width -> direction * width } + fadeIn())
                        .togetherWith(slideOutHorizontally { width -> -direction * width } + fadeOut())
                },
                label = "onboarding_step"
            ) { step ->
                when (step) {
                    OnboardingStep.WELCOME -> WelcomeStep()
                    OnboardingStep.LAST_PERIOD -> LastPeriodStep(
                        selectedDate = uiState.lastPeriodDate,
                        showDatePicker = uiState.showDatePicker,
                        onShowDatePicker = viewModel::showDatePicker,
                        onDateSelected = viewModel::setLastPeriodDate
                    )
                    OnboardingStep.CYCLE_LENGTH -> CycleLengthStep(
                        cycleLength = uiState.cycleLength,
                        onCycleLengthChanged = viewModel::setCycleLength
                    )
                    OnboardingStep.PERIOD_DURATION -> PeriodDurationStep(
                        periodDuration = uiState.periodDuration,
                        onPeriodDurationChanged = viewModel::setPeriodDuration
                    )
                    OnboardingStep.USER_GOAL -> UserGoalStep(
                        selectedGoal = uiState.userGoal,
                        onGoalSelected = viewModel::setUserGoal
                    )
                }
            }
        }

        // ── Bottom Button ────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            val isLastStep = uiState.currentStep == OnboardingStep.USER_GOAL

            Button(
                onClick = {
                    if (isLastStep) {
                        viewModel.completeOnboarding()
                    } else {
                        viewModel.nextStep()
                    }
                },
                enabled = uiState.canProceed && !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isLastStep) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = when {
                                uiState.currentStep == OnboardingStep.WELCOME -> stringResource(R.string.onboarding_get_started)
                                isLastStep -> stringResource(R.string.onboarding_complete_setup)
                                else -> stringResource(R.string.onboarding_continue)
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
