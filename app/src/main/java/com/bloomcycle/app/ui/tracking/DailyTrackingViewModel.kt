package com.bloomcycle.app.ui.tracking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomcycle.app.domain.model.CervicalMucus
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.SexualActivity
import com.bloomcycle.app.domain.model.Symptom
import com.bloomcycle.app.domain.repository.DailyLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class DailyTrackingUiState(
    val date: LocalDate = LocalDate.now(),
    val existingLogId: Long = 0,
    val flowIntensity: FlowIntensity? = null,
    val mood: Mood? = null,
    val symptoms: Set<Symptom> = emptySet(),
    val sexualActivity: SexualActivity? = null,
    val cervicalMucus: CervicalMucus? = null,
    val temperature: String = "",
    val weight: String = "",
    val notes: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
) {
    val formattedDate: String get() = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))
    val hasAnyData: Boolean get() = flowIntensity != null || mood != null ||
            symptoms.isNotEmpty() || sexualActivity != null || cervicalMucus != null ||
            temperature.isNotBlank() || weight.isNotBlank() || notes.isNotBlank()
}

@HiltViewModel
class DailyTrackingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dailyLogRepository: DailyLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyTrackingUiState())
    val uiState: StateFlow<DailyTrackingUiState> = _uiState.asStateFlow()

    init {
        val dateString = savedStateHandle.get<String>("date")
        val date = dateString?.let { LocalDate.parse(it) } ?: LocalDate.now()
        loadLogForDate(date)
    }

    private fun loadLogForDate(date: LocalDate) {
        _uiState.update { it.copy(date = date, isLoading = true) }
        viewModelScope.launch {
            val existingLog = dailyLogRepository.getLogByDate(date).first()
            _uiState.update { state ->
                if (existingLog != null) {
                    state.copy(
                        existingLogId = existingLog.id,
                        flowIntensity = existingLog.flowIntensity,
                        mood = existingLog.mood,
                        symptoms = existingLog.symptoms.toSet(),
                        sexualActivity = existingLog.sexualActivity,
                        cervicalMucus = existingLog.cervicalMucus,
                        temperature = existingLog.temperature?.toString() ?: "",
                        weight = existingLog.weight?.toString() ?: "",
                        notes = existingLog.notes ?: "",
                        isLoading = false
                    )
                } else {
                    state.copy(isLoading = false)
                }
            }
        }
    }

    // ── Field Updates ────────────────────────────────────────

    fun setFlowIntensity(intensity: FlowIntensity?) {
        _uiState.update {
            it.copy(flowIntensity = if (it.flowIntensity == intensity) null else intensity)
        }
    }

    fun setMood(mood: Mood?) {
        _uiState.update {
            it.copy(mood = if (it.mood == mood) null else mood)
        }
    }

    fun toggleSymptom(symptom: Symptom) {
        _uiState.update { state ->
            val updated = state.symptoms.toMutableSet()
            if (symptom in updated) updated.remove(symptom) else updated.add(symptom)
            state.copy(symptoms = updated)
        }
    }

    fun setSexualActivity(activity: SexualActivity?) {
        _uiState.update {
            it.copy(sexualActivity = if (it.sexualActivity == activity) null else activity)
        }
    }

    fun setCervicalMucus(mucus: CervicalMucus?) {
        _uiState.update {
            it.copy(cervicalMucus = if (it.cervicalMucus == mucus) null else mucus)
        }
    }

    fun setTemperature(temp: String) {
        _uiState.update { it.copy(temperature = temp) }
    }

    fun setWeight(weight: String) {
        _uiState.update { it.copy(weight = weight) }
    }

    fun setNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    // ── Save ─────────────────────────────────────────────────

    fun saveLog() {
        val state = _uiState.value
        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val log = DailyLog(
                id = state.existingLogId,
                date = state.date,
                flowIntensity = state.flowIntensity,
                mood = state.mood,
                symptoms = state.symptoms.toList(),
                sexualActivity = state.sexualActivity,
                cervicalMucus = state.cervicalMucus,
                temperature = state.temperature.toFloatOrNull(),
                weight = state.weight.toFloatOrNull(),
                notes = state.notes.ifBlank { null },
                updatedAt = System.currentTimeMillis()
            )
            dailyLogRepository.upsertLog(log)
            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }
    }

    fun deleteLog() {
        val state = _uiState.value
        if (state.existingLogId == 0L) return

        viewModelScope.launch {
            val log = DailyLog(id = state.existingLogId, date = state.date)
            dailyLogRepository.deleteLog(log)
            _uiState.update {
                DailyTrackingUiState(date = state.date, isLoading = false, isSaved = true)
            }
        }
    }
}
