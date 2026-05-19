package com.bloomcycle.app.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bloomcycle.app.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay

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

    // ── Entrance animation ──────────────────────────────────
    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(150)
        showContent = true
    }

    Box(modifier = modifier.fillMaxSize()) {
        // ── Animated background (behind everything) ─────────
        OnboardingBackground(step = uiState.currentStep)

        // ── Foreground content ──────────────────────────────
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top Bar: Back button + Dot indicator ────────
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -it }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.canGoBack) {
                        IconButton(
                            onClick = { viewModel.previousStep() },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    // ── Dot step indicator (centered) ───────────
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        StepDotIndicator(
                            totalSteps = uiState.totalSteps,
                            currentStep = uiState.stepIndex
                        )
                    }

                    // Balance spacer
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }

            // ── Step Content (animated transitions) ─────────
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = uiState.currentStep,
                    transitionSpec = {
                        val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1
                        (slideInHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) { width -> direction * (width / 3) } + fadeIn(tween(300)))
                            .togetherWith(
                                slideOutHorizontally(
                                    animationSpec = tween(250)
                                ) { width -> -direction * (width / 3) } + fadeOut(tween(200))
                            )
                    },
                    label = "onboarding_step"
                ) { step ->
                    when (step) {
                        OnboardingStep.WELCOME -> WelcomeStep()
                        OnboardingStep.YOUR_NAME -> NameStep(
                            name = uiState.userName,
                            onNameChanged = viewModel::setUserName
                        )
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

            // ── Bottom Button (animated entrance) ───────────
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(500, delayMillis = 200)) +
                        slideInVertically(tween(500, delayMillis = 200)) { it / 2 }
            ) {
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
                                Text(
                                    text = when {
                                        uiState.currentStep == OnboardingStep.WELCOME ->
                                            stringResource(R.string.onboarding_get_started)
                                        isLastStep ->
                                            stringResource(R.string.onboarding_complete_setup)
                                        else ->
                                            stringResource(R.string.onboarding_continue)
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = if (isLastStep) Icons.Filled.Check
                                    else Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
