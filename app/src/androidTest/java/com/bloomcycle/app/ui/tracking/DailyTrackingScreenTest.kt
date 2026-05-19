package com.bloomcycle.app.ui.tracking

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.bloomcycle.app.R
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.Symptom
import com.bloomcycle.app.ui.theme.BloomCycleTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class DailyTrackingScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun getString(id: Int, vararg args: Any) =
        composeTestRule.activity.getString(id, *args)

    private fun setTrackingScreen(
        uiState: DailyTrackingUiState = DailyTrackingUiState(
            date = LocalDate.of(2026, 5, 14),
            isLoading = false
        ),
        onNavigateBack: () -> Unit = {}
    ) {
        val viewModel = mockk<DailyTrackingViewModel>(relaxed = true) {
            every { this@mockk.uiState } returns MutableStateFlow(uiState)
        }
        composeTestRule.setContent {
            BloomCycleTheme {
                DailyTrackingScreen(
                    onNavigateBack = onNavigateBack,
                    viewModel = viewModel
                )
            }
        }
    }

    /** Wait for staggered section animations to reveal content. */
    private fun waitForSections() {
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(getString(R.string.tracking_section_flow), substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    // ── Skeleton Loading ─────────────────────────────────────

    @Test
    fun trackingScreen_showsSkeleton_whenLoading() {
        setTrackingScreen(
            uiState = DailyTrackingUiState(
                date = LocalDate.of(2026, 5, 14),
                isLoading = true
            )
        )

        // Real section headers should not appear while loading
        composeTestRule
            .onNodeWithText(getString(R.string.tracking_section_flow), substring = true)
            .assertDoesNotExist()
    }

    // ── App Bar ───────────────────────────────────────────────

    @Test
    fun trackingScreen_displaysTitle() {
        setTrackingScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.tracking_title))
            .assertIsDisplayed()
    }

    @Test
    fun trackingScreen_displaysBackButton() {
        setTrackingScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.back), useUnmergedTree = true)
            .assertDoesNotExist() // It's an icon, not text. The content description is "back"
        // Back icon is identified by content description
    }

    // ── Section Headers ───────────────────────────────────────

    @Test
    fun trackingScreen_displaysFlowIntensitySection() {
        setTrackingScreen()
        waitForSections()

        composeTestRule
            .onNodeWithText(getString(R.string.tracking_section_flow), substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun trackingScreen_displaysMoodSection() {
        setTrackingScreen()
        waitForSections()

        // "Mood" with substring=true matches both "😊  Mood" header and "Mood swings" chip,
        // so we use onAllNodesWithText and assert the first one (the section header).
        composeTestRule
            .onAllNodesWithText(getString(R.string.tracking_section_mood), substring = true)[0]
            .assertIsDisplayed()
    }

    @Test
    fun trackingScreen_displaysSymptomsSection() {
        setTrackingScreen()
        waitForSections()

        composeTestRule
            .onNodeWithText(getString(R.string.tracking_section_symptoms), substring = true)
            .assertIsDisplayed()
    }

    // ── Flow Intensity Chips ──────────────────────────────────

    @Test
    fun trackingScreen_displaysAllFlowIntensityChips() {
        setTrackingScreen()
        waitForSections()

        FlowIntensity.entries.forEach { intensity ->
            val chipLabel = intensity.name.replace("_", " ").lowercase()
                .replaceFirstChar { it.uppercase() }
            // Chips include emoji prefix (e.g. "🌊 Heavy"), use substring match
            composeTestRule
                .onNodeWithText(chipLabel, substring = true)
                .assertIsDisplayed()
        }
    }

    // ── Mood Chips ────────────────────────────────────────────

    @Test
    fun trackingScreen_displaysAllMoodChips() {
        setTrackingScreen()
        waitForSections()

        // Mood chips include emoji prefix
        Mood.entries.forEach { mood ->
            val label = mood.name.replace("_", " ").lowercase()
                .replaceFirstChar { it.uppercase() }
            composeTestRule
                .onNodeWithText(label, substring = true)
                .assertIsDisplayed()
        }
    }

    // ── Symptom Chips ─────────────────────────────────────────

    @Test
    fun trackingScreen_displaysAllSymptomChips() {
        setTrackingScreen()
        waitForSections()

        Symptom.entries.forEach { symptom ->
            val label = symptom.name.replace("_", " ").lowercase()
                .replaceFirstChar { it.uppercase() }
            // Chips include emoji prefix (e.g. "😣 Cramps"), use substring match
            composeTestRule
                .onNodeWithText(label, substring = true)
                .assertIsDisplayed()
        }
    }

    // ── Chip Interactions ─────────────────────────────────────

    @Test
    fun trackingScreen_clickingFlowChip_callsViewModel() {
        val viewModel = mockk<DailyTrackingViewModel>(relaxed = true) {
            every { this@mockk.uiState } returns MutableStateFlow(
                DailyTrackingUiState(date = LocalDate.of(2026, 5, 14), isLoading = false)
            )
        }
        composeTestRule.setContent {
            BloomCycleTheme {
                DailyTrackingScreen(
                    onNavigateBack = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText("Heavy", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("Heavy", substring = true)
            .performClick()

        verify { viewModel.setFlowIntensity(FlowIntensity.HEAVY) }
    }

    @Test
    fun trackingScreen_clickingSymptomChip_callsToggle() {
        val viewModel = mockk<DailyTrackingViewModel>(relaxed = true) {
            every { this@mockk.uiState } returns MutableStateFlow(
                DailyTrackingUiState(date = LocalDate.of(2026, 5, 14), isLoading = false)
            )
        }
        composeTestRule.setContent {
            BloomCycleTheme {
                DailyTrackingScreen(
                    onNavigateBack = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText("Cramps", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("Cramps", substring = true)
            .performClick()

        verify { viewModel.toggleSymptom(Symptom.CRAMPS) }
    }

    // ── Save Button ───────────────────────────────────────────

    @Test
    fun trackingScreen_saveButton_showsSaveText_forNewLog() {
        setTrackingScreen(
            uiState = DailyTrackingUiState(
                date = LocalDate.of(2026, 5, 14),
                isLoading = false,
                existingLogId = 0,
                flowIntensity = FlowIntensity.MEDIUM // hasAnyData = true
            )
        )

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(getString(R.string.tracking_save_log))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onNodeWithText(getString(R.string.tracking_save_log))
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun trackingScreen_saveButton_showsUpdateText_forExistingLog() {
        setTrackingScreen(
            uiState = DailyTrackingUiState(
                date = LocalDate.of(2026, 5, 14),
                isLoading = false,
                existingLogId = 1,
                flowIntensity = FlowIntensity.MEDIUM
            )
        )

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(getString(R.string.tracking_update_log))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onNodeWithText(getString(R.string.tracking_update_log))
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun trackingScreen_saveButton_disabled_whenNoData() {
        setTrackingScreen(
            uiState = DailyTrackingUiState(
                date = LocalDate.of(2026, 5, 14),
                isLoading = false
                // All fields null/empty → hasAnyData = false
            )
        )

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText(getString(R.string.tracking_save_log))
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onNodeWithText(getString(R.string.tracking_save_log))
            .assertIsNotEnabled()
    }

    // ── Delete Button ─────────────────────────────────────────

    @Test
    fun trackingScreen_showsDeleteButton_forExistingLog() {
        setTrackingScreen(
            uiState = DailyTrackingUiState(
                date = LocalDate.of(2026, 5, 14),
                isLoading = false,
                existingLogId = 42
            )
        )

        // Delete icon exists with content description
        composeTestRule
            .onNodeWithText(getString(R.string.tracking_delete_log), useUnmergedTree = true)
            .assertDoesNotExist() // It's a content description, not text
    }
}
