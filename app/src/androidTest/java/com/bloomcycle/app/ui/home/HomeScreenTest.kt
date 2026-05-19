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
import java.time.LocalTime

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

    /** Wait for staggered entrance animations to reveal content. */
    private fun waitForContent() {
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(getString(R.string.home_subtitle))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    // ── Skeleton Loading ─────────────────────────────────────

    @Test
    fun homeScreen_showsSkeleton_whenNotLoaded() {
        setHomeScreen(uiState = HomeUiState(isLoaded = false))

        // Real content should not be rendered while loading
        composeTestRule
            .onNodeWithText(getString(R.string.home_subtitle))
            .assertDoesNotExist()
    }

    // ── Header ────────────────────────────────────────────────

    @Test
    fun homeScreen_displaysGreeting() {
        setHomeScreen(uiState = HomeUiState(isLoaded = true, userName = ""))
        waitForContent()

        val hour = LocalTime.now().hour
        val expectedGreeting = when {
            hour < 12 -> getString(R.string.home_greeting_morning_anon)
            hour < 18 -> getString(R.string.home_greeting_afternoon_anon)
            else -> getString(R.string.home_greeting_evening_anon)
        }

        composeTestRule
            .onNodeWithText(expectedGreeting)
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysSubtitle() {
        setHomeScreen()
        waitForContent()

        composeTestRule
            .onNodeWithText(getString(R.string.home_subtitle))
            .assertIsDisplayed()
    }

    // ── Cycle Progress Ring ──────────────────────────────────

    @Test
    fun homeScreen_displaysCycleProgress() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, cycleDay = 14, cycleLength = 28)
        )
        waitForContent()

        composeTestRule
            .onNodeWithText(getString(R.string.home_cycle_progress, 14, 28))
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysPhaseInCircle() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, currentPhase = CyclePhase.OVULATION)
        )
        waitForContent()

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

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(getString(R.string.home_next_period))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onNodeWithText(getString(R.string.home_next_period))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("12d")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysCycleLengthCard() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, cycleLength = 28)
        )

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(getString(R.string.home_cycle_length))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onNodeWithText(getString(R.string.home_cycle_length))
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysFertilityStatus() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, fertilityStatus = FertilityStatus.HIGH)
        )

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(getString(R.string.fertility_high))
                .fetchSemanticsNodes().isNotEmpty()
        }

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

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(getString(R.string.home_log_today))
                .fetchSemanticsNodes().isNotEmpty()
        }

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

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(getString(R.string.home_edit_today_log))
                .fetchSemanticsNodes().isNotEmpty()
        }

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

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(getString(R.string.home_log_today))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onNodeWithText(getString(R.string.home_log_today))
            .performClick()

        verify { onNavigate(any()) }
    }

    // ── Today's Tip ───────────────────────────────────────────

    @Test
    fun homeScreen_displaysTodaysTipSection() {
        setHomeScreen()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(getString(R.string.home_todays_tip))
                .fetchSemanticsNodes().isNotEmpty()
        }

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
        waitForContent()

        composeTestRule
            .onAllNodesWithText(getString(R.string.phase_menstrual))[0]
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysPhase_follicular() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, currentPhase = CyclePhase.FOLLICULAR)
        )
        waitForContent()

        composeTestRule
            .onAllNodesWithText(getString(R.string.phase_follicular))[0]
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysPhase_ovulation() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, currentPhase = CyclePhase.OVULATION)
        )
        waitForContent()

        composeTestRule
            .onAllNodesWithText(getString(R.string.phase_ovulation))[0]
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysPhase_luteal() {
        setHomeScreen(
            uiState = HomeUiState(isLoaded = true, currentPhase = CyclePhase.LUTEAL)
        )
        waitForContent()

        composeTestRule
            .onAllNodesWithText(getString(R.string.phase_luteal))[0]
            .assertIsDisplayed()
    }
}
