package com.bloomcycle.app.ui.settings

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.bloomcycle.app.R
import com.bloomcycle.app.ui.theme.BloomCycleTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun getString(id: Int, vararg args: Any) =
        composeTestRule.activity.getString(id, *args)

    private fun setSettingsScreen(
        uiState: SettingsUiState = SettingsUiState(isLoaded = true),
        onNavigateToReports: () -> Unit = {},
        onNavigateToPrivacyPolicy: () -> Unit = {},
        onNavigateToDedication: () -> Unit = {}
    ) {
        val viewModel = mockk<SettingsViewModel>(relaxed = true) {
            every { this@mockk.uiState } returns MutableStateFlow(uiState)
        }
        composeTestRule.setContent {
            BloomCycleTheme {
                SettingsScreen(
                    onNavigateToReports = onNavigateToReports,
                    onNavigateToPrivacyPolicy = onNavigateToPrivacyPolicy,
                    onNavigateToDedication = onNavigateToDedication,
                    viewModel = viewModel
                )
            }
        }
    }

    // ── Header ───────────────────────────────────────────────

    @Test
    fun settingsScreen_displaysTitle() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_title))
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysSubtitle() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_subtitle))
            .assertIsDisplayed()
    }

    // ── Notifications Section ────────────────────────────────

    @Test
    fun settingsScreen_displaysNotificationsHeader() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_notifications))
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysPeriodRemindersToggle() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_period_reminders))
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysDailyLogRemindersToggle() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_daily_log_reminders))
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysFertileWindowAlertsToggle() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_fertile_window_alerts))
            .assertIsDisplayed()
    }

    // ── Privacy & Security ───────────────────────────────────

    @Test
    fun settingsScreen_displaysPrivacySecurityHeader() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_privacy_security))
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysBiometricLockOption() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_biometric_lock))
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysScreenSecurityOption() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_screen_security))
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysPrivacyPolicyLink() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_privacy_policy))
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_privacyPolicyLink_triggersNavigation() {
        val onNavigate = mockk<() -> Unit>(relaxed = true)
        setSettingsScreen(onNavigateToPrivacyPolicy = onNavigate)

        composeTestRule
            .onNodeWithText(getString(R.string.settings_privacy_policy))
            .performClick()

        verify { onNavigate() }
    }

    // ── Cycle Settings ───────────────────────────────────────

    @Test
    fun settingsScreen_displaysCycleSettingsHeader() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_cycle_settings))
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysAvgCycleLengthSlider() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_avg_cycle_length))
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysAvgPeriodDurationSlider() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_avg_period_duration))
            .performScrollTo()
            .assertIsDisplayed()
    }

    // ── Reports & Export ─────────────────────────────────────

    @Test
    fun settingsScreen_displaysReportsExportCard() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_reports_export))
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_reportsCard_triggersNavigation() {
        val onNavigate = mockk<() -> Unit>(relaxed = true)
        setSettingsScreen(onNavigateToReports = onNavigate)

        composeTestRule
            .onNodeWithText(getString(R.string.settings_reports_export))
            .performScrollTo()
            .performClick()

        verify { onNavigate() }
    }

    // ── Danger Zone ──────────────────────────────────────────

    @Test
    fun settingsScreen_displaysDangerZone() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_danger_zone))
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysDeleteAllDataButton() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_delete_all_data))
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_deleteButton_callsViewModel() {
        val viewModel = mockk<SettingsViewModel>(relaxed = true) {
            every { this@mockk.uiState } returns MutableStateFlow(
                SettingsUiState(isLoaded = true)
            )
        }
        composeTestRule.setContent {
            BloomCycleTheme {
                SettingsScreen(viewModel = viewModel)
            }
        }

        composeTestRule
            .onNodeWithText(getString(R.string.settings_delete_all_data))
            .performScrollTo()
            .performClick()

        verify { viewModel.requestDeleteAllData() }
    }

    // ── Delete Confirmation Dialog ───────────────────────────

    @Test
    fun settingsScreen_deleteConfirmation_displaysDialogTitle() {
        setSettingsScreen(
            uiState = SettingsUiState(
                isLoaded = true,
                showDeleteConfirmation = true
            )
        )

        composeTestRule
            .onNodeWithText(getString(R.string.settings_delete_dialog_title))
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_deleteConfirmation_displaysDeleteEverythingButton() {
        setSettingsScreen(
            uiState = SettingsUiState(
                isLoaded = true,
                showDeleteConfirmation = true
            )
        )

        composeTestRule
            .onNodeWithText(getString(R.string.settings_delete_everything))
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_deleteConfirmation_displaysCancelButton() {
        setSettingsScreen(
            uiState = SettingsUiState(
                isLoaded = true,
                showDeleteConfirmation = true
            )
        )

        composeTestRule
            .onNodeWithText(getString(R.string.cancel))
            .assertIsDisplayed()
    }

    // ── About Section ────────────────────────────────────────

    @Test
    fun settingsScreen_displaysVersionInfo() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_version))
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysAboutDescription() {
        setSettingsScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.settings_about_description))
            .performScrollTo()
            .assertIsDisplayed()
    }
}
