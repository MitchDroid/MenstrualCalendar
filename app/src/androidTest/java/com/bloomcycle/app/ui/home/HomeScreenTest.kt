package com.bloomcycle.app.ui.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.bloomcycle.app.R
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FertilityStatus
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.ui.theme.BloomCycleTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun getString(id: Int, vararg args: Any) =
        composeTestRule.activity.getString(id, *args)

    private fun setHomeScreen(
        uiState: HomeUiState = HomeUiState(isLoaded = true),
        onNavigateToTracking: (String) -> Unit = {}
    ) {
        val viewModel = mockk<HomeViewModel>(relaxed = true) {
            every { this@mockk.uiState } returns MutableStateFlow(uiState)
        }
        composeTestRule.setContent {
            BloomCycleTheme {
                HomeScreen(
                    onNavigateToTracking = onNavigateToTracking,
                    viewModel = viewModel
                )
            }
        }
    }

    // ── Header ────────────────────────────────────────────────

    @Test
    fun homeScreen_displaysAppName() {
        setHomeScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.app_name))
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysSubtitle() {
        setHomeScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.home_subtitle))
            .assertIsDisplayed()
    }

    // ── Cycle Day Circle ──────────────────────────────────────

    @Test
    fun homeScreen_displaysCycleDay() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, cycleDay = 14)
        )

        composeTestRule
            .onNodeWithText(getString(R.string.home_cycle_day, 14))
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysPhaseInCircle() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, currentPhase = CyclePhase.OVULATION)
        )

        composeTestRule
            .onAllNodesWithText(getString(R.string.phase_ovulation))[0]
            .assertIsDisplayed()
    }

    // ── Quick Stats ───────────────────────────────────────────

    @Test
    fun homeScreen_displaysNextPeriodCard() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, daysUntilNextPeriod = 12)
        )

        composeTestRule
            .onNodeWithText(getString(R.string.home_next_period))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("12 ${getString(R.string.days)}")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysCycleLengthCard() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, cycleLength = 28)
        )

        composeTestRule
            .onNodeWithText(getString(R.string.home_cycle_length))
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysFertilityStatus() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, fertilityStatus = FertilityStatus.HIGH)
        )

        composeTestRule
            .onNodeWithText(getString(R.string.fertility_high))
            .assertIsDisplayed()
    }

    // ── Log Today CTA ─────────────────────────────────────────

    @Test
    fun homeScreen_showsLogTodayButton_whenNoLogExists() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, todayLog = null)
        )

        composeTestRule
            .onNodeWithText(getString(R.string.home_log_today))
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_showsEditLogButton_whenLogExists() {
        setHomeScreen(
            uiState = HomeUiState(
                isLoaded = true,
                todayLog = DailyLog(date = LocalDate.now(), flowIntensity = FlowIntensity.LIGHT)
            )
        )

        composeTestRule
            .onNodeWithText(getString(R.string.home_edit_today_log))
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_logTodayButton_triggersNavigation() {
        val onNavigate = mockk<(String) -> Unit>(relaxed = true)
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, todayLog = null),
            onNavigateToTracking = onNavigate
        )

        composeTestRule
            .onNodeWithText(getString(R.string.home_log_today))
            .performClick()

        verify { onNavigate(any()) }
    }

    // ── Today's Tip ───────────────────────────────────────────

    @Test
    fun homeScreen_displaysTodaysTipSection() {
        setHomeScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.home_todays_tip))
            .assertIsDisplayed()
    }

    // ── Phase-specific content ────────────────────────────────

    @Test
    fun homeScreen_displaysPhase_menstrual() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, currentPhase = CyclePhase.MENSTRUAL)
        )

        composeTestRule
            .onAllNodesWithText(getString(R.string.phase_menstrual))[0]
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysPhase_follicular() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, currentPhase = CyclePhase.FOLLICULAR)
        )

        composeTestRule
            .onAllNodesWithText(getString(R.string.phase_follicular))[0]
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysPhase_ovulation() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, currentPhase = CyclePhase.OVULATION)
        )

        composeTestRule
            .onAllNodesWithText(getString(R.string.phase_ovulation))[0]
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysPhase_luteal() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, currentPhase = CyclePhase.LUTEAL)
        )

        composeTestRule
            .onAllNodesWithText(getString(R.string.phase_luteal))[0]
            .assertIsDisplayed()
    }
}
