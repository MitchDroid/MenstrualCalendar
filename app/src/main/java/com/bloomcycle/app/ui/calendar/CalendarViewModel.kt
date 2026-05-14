package com.bloomcycle.app.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.repository.DailyLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Visual marker type for each calendar day.
 */
enum class DayMarkerType {
    PERIOD,
    PREDICTED_PERIOD,
    FERTILE,
    OVULATION,
    LOGGED,
    NONE
}

/**
 * Data for a single calendar day cell.
 */
data class CalendarDay(
    val date: LocalDate,
    val dayOfMonth: Int = date.dayOfMonth,
    val isCurrentMonth: Boolean = true,
    val isToday: Boolean = date == LocalDate.now(),
    val markerType: DayMarkerType = DayMarkerType.NONE,
    val hasLog: Boolean = false,
    val cyclePhase: CyclePhase? = null,
    val cycleDay: Int? = null
)

/**
 * Full UI state for the calendar screen.
 */
data class CalendarUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val calendarDays: List<CalendarDay> = emptyList(),
    val selectedDayLog: DailyLog? = null,
    val lastPeriodDate: LocalDate? = null,
    val cycleLength: Int = UserPreferencesManager.DEFAULT_CYCLE_LENGTH,
    val periodDuration: Int = UserPreferencesManager.DEFAULT_PERIOD_DURATION
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val dailyLogRepository: DailyLogRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<CalendarUiState> = combine(
        _currentMonth,
        _selectedDate,
        preferencesManager.lastPeriodDate,
        preferencesManager.averageCycleLength,
        preferencesManager.averagePeriodDuration,
        dailyLogRepository.getAllLogs()
    ) { values ->
        val month = values[0] as YearMonth
        val selected = values[1] as LocalDate
        val lastPeriod = values[2] as LocalDate?
        val cycleLen = values[3] as Int
        val periodDur = values[4] as Int
        @Suppress("UNCHECKED_CAST")
        val allLogs = values[5] as List<DailyLog>

        val logsByDate = allLogs.associateBy { it.date }
        val days = buildCalendarDays(month, lastPeriod, cycleLen, periodDur, logsByDate)

        CalendarUiState(
            currentMonth = month,
            selectedDate = selected,
            calendarDays = days,
            selectedDayLog = logsByDate[selected],
            lastPeriodDate = lastPeriod,
            cycleLength = cycleLen,
            periodDuration = periodDur
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CalendarUiState()
    )

    // ── Actions ──────────────────────────────────────────────

    fun selectDate(date: LocalDate) {
        _selectedDate.update { date }
    }

    fun navigateMonth(offset: Int) {
        _currentMonth.update { it.plusMonths(offset.toLong()) }
    }

    fun goToToday() {
        _currentMonth.update { YearMonth.now() }
        _selectedDate.update { LocalDate.now() }
    }

    // ── Calendar Day Builder ─────────────────────────────────

    private fun buildCalendarDays(
        month: YearMonth,
        lastPeriodDate: LocalDate?,
        cycleLength: Int,
        periodDuration: Int,
        logsByDate: Map<LocalDate, DailyLog>
    ): List<CalendarDay> {
        val firstOfMonth = month.atDay(1)
        val lastOfMonth = month.atEndOfMonth()

        // Pad to start of week (Monday = 1)
        val startDayOfWeek = firstOfMonth.dayOfWeek.value // 1=Mon, 7=Sun
        val startDate = firstOfMonth.minusDays((startDayOfWeek - 1).toLong())

        // Build 6 weeks of days (42 cells)
        val days = mutableListOf<CalendarDay>()
        var date = startDate

        repeat(42) {
            val isCurrentMonth = date.month == month.month && date.year == month.year
            val log = logsByDate[date]
            val markerType = computeMarkerType(date, lastPeriodDate, cycleLength, periodDuration, log)
            val cycleDay = computeCycleDay(date, lastPeriodDate, cycleLength)
            val phase = computeCyclePhase(cycleDay, periodDuration, cycleLength)

            days.add(
                CalendarDay(
                    date = date,
                    isCurrentMonth = isCurrentMonth,
                    isToday = date == LocalDate.now(),
                    markerType = markerType,
                    hasLog = log != null,
                    cyclePhase = phase,
                    cycleDay = cycleDay
                )
            )
            date = date.plusDays(1)
        }

        return days
    }

    private fun computeMarkerType(
        date: LocalDate,
        lastPeriodDate: LocalDate?,
        cycleLength: Int,
        periodDuration: Int,
        log: DailyLog?
    ): DayMarkerType {
        // If user logged flow for this day, show as period
        if (log?.flowIntensity != null && log.flowIntensity != FlowIntensity.NONE) {
            return DayMarkerType.PERIOD
        }

        if (lastPeriodDate == null) {
            return if (log != null) DayMarkerType.LOGGED else DayMarkerType.NONE
        }

        val daysSinceLastPeriod = ChronoUnit.DAYS.between(lastPeriodDate, date).toInt()
        if (daysSinceLastPeriod < 0) {
            return if (log != null) DayMarkerType.LOGGED else DayMarkerType.NONE
        }

        val cycleDay = (daysSinceLastPeriod % cycleLength) + 1

        return when {
            // Period days (1 to periodDuration)
            cycleDay in 1..periodDuration -> {
                // First cycle = actual period, future cycles = predicted
                val cycleNumber = daysSinceLastPeriod / cycleLength
                if (cycleNumber == 0 || date <= LocalDate.now()) {
                    DayMarkerType.PERIOD
                } else {
                    DayMarkerType.PREDICTED_PERIOD
                }
            }
            // Ovulation day (typically cycle day 14 for 28-day cycle)
            cycleDay == cycleLength - 14 -> DayMarkerType.OVULATION
            // Fertile window (5 days before ovulation + ovulation day)
            cycleDay in (cycleLength - 19)..(cycleLength - 14) -> DayMarkerType.FERTILE
            // Has a log entry
            log != null -> DayMarkerType.LOGGED
            else -> DayMarkerType.NONE
        }
    }

    private fun computeCycleDay(
        date: LocalDate,
        lastPeriodDate: LocalDate?,
        cycleLength: Int
    ): Int? {
        if (lastPeriodDate == null) return null
        val daysSince = ChronoUnit.DAYS.between(lastPeriodDate, date).toInt()
        if (daysSince < 0) return null
        return (daysSince % cycleLength) + 1
    }

    private fun computeCyclePhase(
        cycleDay: Int?,
        periodDuration: Int,
        cycleLength: Int
    ): CyclePhase? {
        if (cycleDay == null) return null
        val ovulationDay = cycleLength - 14
        return when {
            cycleDay in 1..periodDuration -> CyclePhase.MENSTRUAL
            cycleDay in (periodDuration + 1) until ovulationDay -> CyclePhase.FOLLICULAR
            cycleDay in ovulationDay..(ovulationDay + 1) -> CyclePhase.OVULATION
            else -> CyclePhase.LUTEAL
        }
    }
}
