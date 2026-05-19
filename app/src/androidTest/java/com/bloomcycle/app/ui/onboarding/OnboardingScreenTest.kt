package com.bloomcycle.app.ui.onboarding

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
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

    /**
     * Wait for the entrance animation (150ms delay + AnimatedVisibility)
     * to complete so content nodes appear in the tree.
     */
    private fun waitForButtonText(text: String) {
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(text)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForDotIndicator() {
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithContentDescription("Step 1", substring = true, useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    // ── Welcome Step ──────────────────────────────────────────

    @Test
    fun onboarding_welcomeStep_displaysGetStartedButton() {
        setOnboardingScreen(
            uiState = OnboardingUiState(currentStep = OnboardingStep.WELCOME)
        )

        val text = getString(R.string.onboarding_get_started)
        waitForButtonText(text)

        composeTestRule
            .onNodeWithText(text)
            .assertIsDisplayed()
    }

    @Test
    fun onboarding_welcomeStep_buttonIsEnabled() {
        setOnboardingScreen(
            uiState = OnboardingUiState(currentStep = OnboardingStep.WELCOME)
        )

        val text = getString(R.string.onboarding_get_started)
        waitForButtonText(text)

        composeTestRule
            .onNodeWithText(text)
            .assertIsEnabled()
    }

    // ── Step Progress (Dot Indicator) ────────────────────────────

    @Test
    fun onboarding_displaysStepDotIndicator_withCorrectActiveStep() {
        setOnboardingScreen(
            uiState = OnboardingUiState(currentStep = OnboardingStep.WELCOME)
        )

        waitForDotIndicator()

        // Verify dot indicator is rendered with the correct active step
        composeTestRule
            .onAllNodesWithContentDescription("Step 1 active", useUnmergedTree = true)
            .assertCountEquals(1)
    }

    @Test
    fun onboarding_thirdStep_displaysCorrectActiveDot() {
        setOnboardingScreen(
            uiState = OnboardingUiState(currentStep = OnboardingStep.LAST_PERIOD)
        )

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithContentDescription("Step 3 active", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // LAST_PERIOD is step index 2 → "Step 3 active"
        composeTestRule
            .onAllNodesWithContentDescription("Step 3 active", useUnmergedTree = true)
            .assertCountEquals(1)

        // Steps before it should be completed
        composeTestRule
            .onAllNodesWithContentDescription("Step 1 completed", useUnmergedTree = true)
            .assertCountEquals(1)
        composeTestRule
            .onAllNodesWithContentDescription("Step 2 completed", useUnmergedTree = true)
            .assertCountEquals(1)
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

        val text = getString(R.string.onboarding_continue)
        waitForButtonText(text)

        composeTestRule
            .onNodeWithText(text)
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

        val text = getString(R.string.onboarding_complete_setup)
        waitForButtonText(text)

        composeTestRule
            .onNodeWithText(text)
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

        val text = getString(R.string.onboarding_get_started)
        waitForButtonText(text)

        composeTestRule
            .onNodeWithText(text)
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

        val text = getString(R.string.onboarding_complete_setup)
        waitForButtonText(text)

        composeTestRule
            .onNodeWithText(text)
            .performClick()

        verify { viewModel.completeOnboarding() }
    }
}
