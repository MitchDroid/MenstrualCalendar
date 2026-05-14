package com.bloomcycle.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.domain.model.UserGoal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * Onboarding step progression.
 */
enum class OnboardingStep {
    WELCOME,
    LAST_PERIOD,
    CYCLE_LENGTH,
    PERIOD_DURATION,
    USER_GOAL
}

/**
 * UI state for the onboarding flow.
 */
data class OnboardingUiState(
    val currentStep: OnboardingStep = OnboardingStep.WELCOME,
    val lastPeriodDate: LocalDate? = null,
    val cycleLength: Int = UserPreferencesManager.DEFAULT_CYCLE_LENGTH,
    val periodDuration: Int = UserPreferencesManager.DEFAULT_PERIOD_DURATION,
    val userGoal: UserGoal? = null,
    val isSaving: Boolean = false,
    val isComplete: Boolean = false,
    val showDatePicker: Boolean = false
) {
    val stepIndex: Int get() = OnboardingStep.entries.indexOf(currentStep)
    val totalSteps: Int get() = OnboardingStep.entries.size
    val progress: Float get() = (stepIndex + 1).toFloat() / totalSteps
    val canGoBack: Boolean get() = currentStep != OnboardingStep.WELCOME
    val canProceed: Boolean get() = when (currentStep) {
        OnboardingStep.WELCOME -> true
        OnboardingStep.LAST_PERIOD -> lastPeriodDate != null
        OnboardingStep.CYCLE_LENGTH -> cycleLength in 18..45
        OnboardingStep.PERIOD_DURATION -> periodDuration in 1..14
        OnboardingStep.USER_GOAL -> userGoal != null
    }
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    // ── Navigation ───────────────────────────────────────────

    fun nextStep() {
        _uiState.update { state ->
            val steps = OnboardingStep.entries
            val nextIndex = state.stepIndex + 1
            if (nextIndex < steps.size) {
                state.copy(currentStep = steps[nextIndex])
            } else {
                state // Already at last step
            }
        }
    }

    fun previousStep() {
        _uiState.update { state ->
            val steps = OnboardingStep.entries
            val prevIndex = state.stepIndex - 1
            if (prevIndex >= 0) {
                state.copy(currentStep = steps[prevIndex])
            } else {
                state
            }
        }
    }

    // ── Data Updates ─────────────────────────────────────────

    fun setLastPeriodDate(date: LocalDate) {
        _uiState.update { it.copy(lastPeriodDate = date) }
    }

    fun setCycleLength(length: Int) {
        _uiState.update { it.copy(cycleLength = length.coerceIn(18, 45)) }
    }

    fun setPeriodDuration(duration: Int) {
        _uiState.update { it.copy(periodDuration = duration.coerceIn(1, 14)) }
    }

    fun setUserGoal(goal: UserGoal) {
        _uiState.update { it.copy(userGoal = goal) }
    }

    fun showDatePicker(show: Boolean) {
        _uiState.update { it.copy(showDatePicker = show) }
    }

    // ── Save & Complete ──────────────────────────────────────

    fun completeOnboarding() {
        val state = _uiState.value
        val periodDate = state.lastPeriodDate ?: return
        val goal = state.userGoal ?: return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            preferencesManager.saveOnboardingData(
                lastPeriodDate = periodDate,
                averageCycleLength = state.cycleLength,
                averagePeriodDuration = state.periodDuration,
                birthYear = null,
                userGoal = goal
            )
            _uiState.update { it.copy(isSaving = false, isComplete = true) }
        }
    }
}
