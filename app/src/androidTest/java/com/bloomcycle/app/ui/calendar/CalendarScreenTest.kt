package com.bloomcycle.app.ui.calendar

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.bloomcycle.app.R
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.ui.theme.BloomCycleTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class CalendarScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun getString(id: Int, vararg args: Any) =
        composeTestRule.activity.getString(id, *args)

    private val testDate = LocalDate.of(2026, 5, 14)
    private val testMonth = YearMonth.of(2026, 5)

    private fun defaultCalendarDays(): List<CalendarDay> = listOf(
        CalendarDay(
            date = testDate,
            isCurrentMonth = true,
            isToday = false,
            markerType = DayMarkerType.NONE,
            cyclePhase = CyclePhase.FOLLICULAR,
            cycleDay = 14
        )
    )

    private fun setCalendarScreen(
        uiState: CalendarUiState = CalendarUiState(
            currentMonth = testMonth,
            selectedDate = testDate,
            calendarDays = defaultCalendarDays()
        ),
        onNavigateToTracking: (String) -> Unit = {}
    ) {
        val viewModel = mockk<CalendarViewModel>(relaxed = true) {
            every { this@mockk.uiState } returns MutableStateFlow(uiState)
        }
        composeTestRule.setContent {
            BloomCycleTheme {
                CalendarScreen(
                    onNavigateToTracking = onNavigateToTracking,
                    viewModel = viewModel
                )
            }
        }
    }

    // ── Legend ────────────────────────────────────────────────

    @Test
    fun calendarScreen_displaysLegend_periodLabel() {
        setCalendarScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.calendar_legend_period))
            .assertIsDisplayed()
    }

    @Test
    fun calendarScreen_displaysLegend_predictedLabel() {
        setCalendarScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.calendar_legend_predicted))
            .assertIsDisplayed()
    }

    @Test
    fun calendarScreen_displaysLegend_fertileLabel() {
        setCalendarScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.calendar_legend_fertile))
            .assertIsDisplayed()
    }

    @Test
    fun calendarScreen_displaysLegend_ovulationLabel() {
        setCalendarScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.calendar_legend_ovulation))
            .assertIsDisplayed()
    }

    // ── FAB ──────────────────────────────────────────────────

    @Test
    fun calendarScreen_displaysFab_logDay() {
        setCalendarScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.calendar_log_day))
            .assertIsDisplayed()
    }

    @Test
    fun calendarScreen_fab_triggersNavigation() {
        val onNavigate = mockk<(String) -> Unit>(relaxed = true)
        setCalendarScreen(onNavigateToTracking = onNavigate)

        composeTestRule
            .onNodeWithText(getString(R.string.calendar_log_day))
            .performClick()

        verify { onNavigate(any()) }
    }

    // ── Selected Day Card ────────────────────────────────────

    @Test
    fun calendarScreen_selectedDayCard_displaysCycleDayLabel() {
        setCalendarScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.calendar_cycle_day))
            .assertIsDisplayed()
    }

    @Test
    fun calendarScreen_selectedDayCard_displaysPhaseLabel() {
        setCalendarScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.calendar_phase))
            .assertIsDisplayed()
    }

    @Test
    fun calendarScreen_selectedDayCard_displaysStatusLabel() {
        setCalendarScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.calendar_status))
            .assertIsDisplayed()
    }

    @Test
    fun calendarScreen_selectedDayCard_displaysPhaseValue() {
        setCalendarScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.phase_follicular))
            .assertIsDisplayed()
    }

    // ── Add / Edit Log CTA ───────────────────────────────────

    @Test
    fun calendarScreen_selectedDayCard_displaysAddLogButton_whenNoLog() {
        setCalendarScreen()

        composeTestRule
            .onNodeWithText(getString(R.string.calendar_add_log))
            .assertIsDisplayed()
    }

    @Test
    fun calendarScreen_addLogButton_triggersNavigation() {
        val onNavigate = mockk<(String) -> Unit>(relaxed = true)
        setCalendarScreen(onNavigateToTracking = onNavigate)

        composeTestRule
            .onNodeWithText(getString(R.string.calendar_add_log))
            .performClick()

        verify { onNavigate(any()) }
    }

    @Test
    fun calendarScreen_selectedDayCard_displaysEditLogButton_whenLogExists() {
        setCalendarScreen(
            uiState = CalendarUiState(
                currentMonth = testMonth,
                selectedDate = testDate,
                calendarDays = listOf(
                    CalendarDay(
                        date = testDate,
                        isCurrentMonth = true,
                        isToday = false,
                        markerType = DayMarkerType.PERIOD,
                        hasLog = true,
                        cyclePhase = CyclePhase.MENSTRUAL,
                        cycleDay = 1
                    )
                ),
                selectedDayLog = DailyLog(
                    date = testDate,
                    flowIntensity = FlowIntensity.MEDIUM
                )
            )
        )

        composeTestRule
            .onNodeWithText(getString(R.string.calendar_edit_log))
            .assertIsDisplayed()
    }

    @Test
    fun calendarScreen_selectedDayCard_displaysTodaysLogHeader_whenLogExists() {
        setCalendarScreen(
            uiState = CalendarUiState(
                currentMonth = testMonth,
                selectedDate = testDate,
                calendarDays = listOf(
                    CalendarDay(
                        date = testDate,
                        isCurrentMonth = true,
                        isToday = false,
                        markerType = DayMarkerType.PERIOD,
                        hasLog = true,
                        cyclePhase = CyclePhase.MENSTRUAL,
                        cycleDay = 1
                    )
                ),
                selectedDayLog = DailyLog(
                    date = testDate,
                    flowIntensity = FlowIntensity.MEDIUM
                )
            )
        )

        composeTestRule
            .onNodeWithText(getString(R.string.calendar_todays_log))
            .assertIsDisplayed()
    }
}
