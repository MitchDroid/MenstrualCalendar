package com.bloomcycle.app.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.domain.model.UserGoal
import com.bloomcycle.app.domain.repository.DailyLogRepository
import com.bloomcycle.app.domain.usecase.CyclePrediction
import com.bloomcycle.app.domain.usecase.CyclePredictionEngine
import com.bloomcycle.app.domain.usecase.CycleStats
import com.bloomcycle.app.domain.usecase.FlowPattern
import com.bloomcycle.app.domain.usecase.MoodDistribution
import com.bloomcycle.app.domain.usecase.SymptomFrequency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class InsightsUiState(
    val prediction: CyclePrediction? = null,
    val cycleStats: CycleStats? = null,
    val symptomFrequencies: List<SymptomFrequency> = emptyList(),
    val moodDistribution: List<MoodDistribution> = emptyList(),
    val flowPatterns: List<FlowPattern> = emptyList(),
    val userGoal: UserGoal? = null,
    val hasData: Boolean = false,
    val isLoaded: Boolean = false
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    dailyLogRepository: DailyLogRepository,
    preferencesManager: UserPreferencesManager,
    private val predictionEngine: CyclePredictionEngine
) : ViewModel() {

    val uiState: StateFlow<InsightsUiState> = combine(
        dailyLogRepository.getAllLogs(),
        preferencesManager.lastPeriodDate,
        preferencesManager.averageCycleLength,
        preferencesManager.averagePeriodDuration,
        preferencesManager.userGoal
    ) { logs, lastPeriod, cycleLen, periodDur, goal ->

        val prediction = lastPeriod?.let {
            predictionEngine.predict(it, cycleLen, periodDur)
        }

        val stats = predictionEngine.computeCycleStats(logs, cycleLen, periodDur)
        val symptoms = predictionEngine.computeSymptomFrequency(logs)
        val moods = predictionEngine.computeMoodDistribution(logs)
        val flows = predictionEngine.computeFlowPattern(logs)

        InsightsUiState(
            prediction = prediction,
            cycleStats = stats,
            symptomFrequencies = symptoms,
            moodDistribution = moods,
            flowPatterns = flows,
            userGoal = goal,
            hasData = logs.isNotEmpty(),
            isLoaded = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InsightsUiState()
    )
}
