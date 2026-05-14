package com.bloomcycle.app.ui.insights

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
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

class InsightsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun getString(id: Int, vararg args: Any) =
        composeTestRule.activity.getString(id, *args)

    private fun setInsightsScreen(
        uiState: InsightsUiState = InsightsUiState(isLoaded = true),
        onNavigateToCycleGuide: () -> Unit = {},
        onNavigateToSymptomGuide: () -> Unit = {},
        onNavigateToHealthTips: (String?) -> Unit = {}
    ) {
        val viewModel = mockk<InsightsViewModel>(relaxed = true) {
            every { this@mockk.uiState } returns MutableStateFlow(uiState)
        }
        composeTestRule.setContent {
            BloomCycleTheme {
                InsightsScreen(
                    viewModel = viewModel,
                    onNavigateToCycleGuide = onNavigateToCycleGuide,
                    onNavigateToSymptomGuide = onNavigateToSymptomGuide,
                    onNavigateToHealthTips = onNavigateToHealthTips
                )
            }
        }
    }

    // ── Header ───────────────────────────────────────────────

    @Test
    fun insightsScreen_displaysTitle() {
        setInsightsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.insights_title))
            .assertIsDisplayed()
    }

    @Test
    fun insightsScreen_displaysSubtitle() {
        setInsightsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.insights_subtitle))
            .assertIsDisplayed()
    }

    // ── Empty State ──────────────────────────────────────────

    @Test
    fun insightsScreen_showsEmptyState_whenNoData() {
        setInsightsScreen(
            uiState = InsightsUiState(isLoaded = true, hasData = false)
        )

        composeTestRule
            .onNodeWithText(getString(R.string.insights_empty_title))
            .assertIsDisplayed()
    }

    @Test
    fun insightsScreen_showsEmptyDescription_whenNoData() {
        setInsightsScreen(
            uiState = InsightsUiState(isLoaded = true, hasData = false)
        )

        composeTestRule
            .onNodeWithText(getString(R.string.insights_empty_description))
            .assertIsDisplayed()
    }

    @Test
    fun insightsScreen_hidesEmptyState_whenHasData() {
        setInsightsScreen(
            uiState = InsightsUiState(isLoaded = true, hasData = true)
        )

        composeTestRule
            .onNodeWithText(getString(R.string.insights_empty_title))
            .assertDoesNotExist()
    }

    // ── Education Section ────────────────────────────────────

    @Test
    fun insightsScreen_displaysLearnSectionHeader() {
        setInsightsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.insights_learn_section))
            .assertIsDisplayed()
    }

    @Test
    fun insightsScreen_displaysLearnSubtitle() {
        setInsightsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.insights_learn_subtitle))
            .assertIsDisplayed()
    }

    @Test
    fun insightsScreen_displaysCyclePhaseGuideCard() {
        setInsightsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.insights_cycle_phase_guide))
            .assertIsDisplayed()
    }

    @Test
    fun insightsScreen_displaysSymptomGuideCard() {
        setInsightsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.insights_symptom_guide))
            .assertIsDisplayed()
    }

    @Test
    fun insightsScreen_displaysHealthTipsCard() {
        setInsightsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.insights_health_tips))
            .assertIsDisplayed()
    }

    // ── Navigation Callbacks ─────────────────────────────────

    @Test
    fun insightsScreen_cycleGuideCard_triggersNavigation() {
        val onNavigate = mockk<() -> Unit>(relaxed = true)
        setInsightsScreen(onNavigateToCycleGuide = onNavigate)

        composeTestRule
            .onNodeWithText(getString(R.string.insights_cycle_phase_guide))
            .performClick()

        verify { onNavigate() }
    }

    @Test
    fun insightsScreen_symptomGuideCard_triggersNavigation() {
        val onNavigate = mockk<() -> Unit>(relaxed = true)
        setInsightsScreen(onNavigateToSymptomGuide = onNavigate)

        composeTestRule
            .onNodeWithText(getString(R.string.insights_symptom_guide))
            .performClick()

        verify { onNavigate() }
    }

    @Test
    fun insightsScreen_healthTipsCard_triggersNavigation() {
        val onNavigate = mockk<(String?) -> Unit>(relaxed = true)
        setInsightsScreen(onNavigateToHealthTips = onNavigate)

        composeTestRule
            .onNodeWithText(getString(R.string.insights_health_tips))
            .performClick()

        verify { onNavigate(any()) }
    }
}
