package com.bloomcycle.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FertilityStatus
import com.bloomcycle.app.domain.repository.DailyLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "",
    val cycleDay: Int = 1,
    val daysUntilNextPeriod: Int = 0,
    val cycleLength: Int = UserPreferencesManager.DEFAULT_CYCLE_LENGTH,
    val periodDuration: Int = UserPreferencesManager.DEFAULT_PERIOD_DURATION,
    val currentPhase: CyclePhase = CyclePhase.MENSTRUAL,
    val fertilityStatus: FertilityStatus = FertilityStatus.LOW,
    val todayLog: DailyLog? = null,
    val isLoaded: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    preferencesManager: UserPreferencesManager,
    dailyLogRepository: DailyLogRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        preferencesManager.userName,
        preferencesManager.lastPeriodDate,
        preferencesManager.averageCycleLength,
        preferencesManager.averagePeriodDuration,
        dailyLogRepository.getLogByDate(LocalDate.now())
    ) { userName, lastPeriod, cycleLen, periodDur, todayLog ->
        if (lastPeriod == null) {
            return@combine HomeUiState(userName = userName, isLoaded = true)
        }

        val daysSince = ChronoUnit.DAYS.between(lastPeriod, LocalDate.now()).toInt()
        val cycleDay = (daysSince % cycleLen) + 1
        val daysUntilNext = cycleLen - cycleDay

        val ovulationDay = cycleLen - 14
        val phase = when {
            cycleDay in 1..periodDur -> CyclePhase.MENSTRUAL
            cycleDay in (periodDur + 1) until ovulationDay -> CyclePhase.FOLLICULAR
            cycleDay in ovulationDay..(ovulationDay + 1) -> CyclePhase.OVULATION
            else -> CyclePhase.LUTEAL
        }

        val fertility = when {
            cycleDay in (ovulationDay - 5)..ovulationDay -> {
                if (cycleDay == ovulationDay) FertilityStatus.PEAK else FertilityStatus.HIGH
            }
            cycleDay in (ovulationDay - 7)..(ovulationDay + 2) -> FertilityStatus.MEDIUM
            else -> FertilityStatus.LOW
        }

        HomeUiState(
            userName = userName,
            cycleDay = cycleDay,
            daysUntilNextPeriod = daysUntilNext,
            cycleLength = cycleLen,
            periodDuration = periodDur,
            currentPhase = phase,
            fertilityStatus = fertility,
            todayLog = todayLog,
            isLoaded = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )
}
