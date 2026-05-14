package com.bloomcycle.app.ui.onboarding

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.bloomcycle.app.R
import com.bloomcycle.app.ui.theme.BloomCycleTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import com.bloomcycle.app.domain.model.UserGoal
import java.time.LocalDate

class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun getString(id: Int, vararg args: Any) =
        composeTestRule.activity.getString(id, *args)

    private fun setOnboardingScreen(
        uiState: OnboardingUiState = OnboardingUiState(),
        onComplete: () -> Unit = {}
    ) {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true) {
            every { this@mockk.uiState } returns MutableStateFlow(uiState)
        }
        composeTestRule.setContent {
            BloomCycleTheme {
                OnboardingScreen(
                    onOnboardingComplete = onComplete,
                    viewModel = viewModel
                )
            }
        }
    }

    // ── Welcome Step ──────────────────────────────────────────

    @Test
    fun onboarding_welcomeStep_displaysGetStartedButton() {
        setOnboardingScreen(
            uiState = OnboardingUiState(currentStep = OnboardingStep.WELCOME)
        )

        composeTestRule
            .onNodeWithText(getString(R.string.onboarding_get_started))
            .assertIsDisplayed()
    }

    @Test
    fun onboarding_welcomeStep_buttonIsEnabled() {
        setOnboardingScreen(
            uiState = OnboardingUiState(currentStep = OnboardingStep.WELCOME)
        )

        composeTestRule
            .onNodeWithText(getString(R.string.onboarding_get_started))
            .assertIsEnabled()
    }

    // ── Step Progress ─────────────────────────────────────────

    @Test
    fun onboarding_displaysStepIndicator() {
        setOnboardingScreen(
            uiState = OnboardingUiState(currentStep = OnboardingStep.WELCOME)
        )

        // Step 1 of 5
        composeTestRule
            .onNodeWithText(getString(R.string.onboarding_step_indicator, 1, 5))
            .assertIsDisplayed()
    }

    @Test
    fun onboarding_secondStep_displaysCorrectIndicator() {
        setOnboardingScreen(
            uiState = OnboardingUiState(currentStep = OnboardingStep.LAST_PERIOD)
        )

        // Step 2 of 5
        composeTestRule
            .onNodeWithText(getString(R.string.onboarding_step_indicator, 2, 5))
            .assertIsDisplayed()
    }

    // ── Navigation Buttons ────────────────────────────────────

    @Test
    fun onboarding_nonWelcomeStep_showsContinueButton() {
        setOnboardingScreen(
            uiState = OnboardingUiState(
                currentStep = OnboardingStep.CYCLE_LENGTH,
                lastPeriodDate = LocalDate.now()
            )
        )

        composeTestRule
            .onNodeWithText(getString(R.string.onboarding_continue))
            .assertIsDisplayed()
    }

    @Test
    fun onboarding_lastStep_showsCompleteButton() {
        setOnboardingScreen(
            uiState = OnboardingUiState(
                currentStep = OnboardingStep.USER_GOAL,
                lastPeriodDate = LocalDate.now(),
                userGoal = UserGoal.TRACK_CYCLE
            )
        )

        composeTestRule
            .onNodeWithText(getString(R.string.onboarding_complete_setup))
            .assertIsDisplayed()
    }

    @Test
    fun onboarding_getStarted_callsNextStep() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true) {
            every { this@mockk.uiState } returns MutableStateFlow(
                OnboardingUiState(currentStep = OnboardingStep.WELCOME)
            )
        }
        composeTestRule.setContent {
            BloomCycleTheme {
                OnboardingScreen(
                    onOnboardingComplete = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule
            .onNodeWithText(getString(R.string.onboarding_get_started))
            .performClick()

        verify { viewModel.nextStep() }
    }

    @Test
    fun onboarding_lastStep_completeButton_callsCompleteOnboarding() {
        val viewModel = mockk<OnboardingViewModel>(relaxed = true) {
            every { this@mockk.uiState } returns MutableStateFlow(
                OnboardingUiState(
                    currentStep = OnboardingStep.USER_GOAL,
                    lastPeriodDate = LocalDate.now(),
                    userGoal = UserGoal.TRACK_CYCLE
                )
            )
        }
        composeTestRule.setContent {
            BloomCycleTheme {
                OnboardingScreen(
                    onOnboardingComplete = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule
            .onNodeWithText(getString(R.string.onboarding_complete_setup))
            .performClick()

        verify { viewModel.completeOnboarding() }
    }
}
