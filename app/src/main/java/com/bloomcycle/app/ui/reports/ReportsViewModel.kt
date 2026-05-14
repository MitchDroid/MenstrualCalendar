package com.bloomcycle.app.ui.reports

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomcycle.app.data.export.DataExporter
import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.repository.DailyLogRepository
import com.bloomcycle.app.domain.usecase.CycleSummaryReport
import com.bloomcycle.app.domain.usecase.ReportGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportsUiState(
    val report: CycleSummaryReport? = null,
    val isLoaded: Boolean = false,
    val exportEvent: ExportEvent? = null
)

sealed interface ExportEvent {
    data class ShareCsv(val intent: Intent) : ExportEvent
    data class ShareTextFile(val intent: Intent) : ExportEvent
    data class ShareTextInline(val intent: Intent) : ExportEvent
}

@HiltViewModel
class ReportsViewModel @Inject constructor(
    dailyLogRepository: DailyLogRepository,
    preferencesManager: UserPreferencesManager,
    private val reportGenerator: ReportGenerator,
    private val dataExporter: DataExporter
) : ViewModel() {

    private val _exportEvent = MutableStateFlow<ExportEvent?>(null)

    // Cache logs for export operations
    private var cachedLogs: List<DailyLog> = emptyList()
    private var cachedReport: CycleSummaryReport? = null

    val uiState: StateFlow<ReportsUiState> = combine(
        dailyLogRepository.getAllLogs(),
        preferencesManager.averageCycleLength,
        preferencesManager.averagePeriodDuration,
        _exportEvent
    ) { logs, cycleLength, periodDuration, exportEvent ->
        cachedLogs = logs

        val report = reportGenerator.generateSummaryReport(logs, cycleLength, periodDuration)
        cachedReport = report

        ReportsUiState(
            report = report,
            isLoaded = true,
            exportEvent = exportEvent
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ReportsUiState()
    )

    // ── Export Actions ───────────────────────────────────────────

    fun exportCsv() {
        viewModelScope.launch {
            val csv = reportGenerator.generateCsvExport(cachedLogs)
            val intent = dataExporter.exportCsv(csv)
            _exportEvent.update { ExportEvent.ShareCsv(intent) }
        }
    }

    fun exportTextReport() {
        viewModelScope.launch {
            val report = cachedReport ?: return@launch
            val text = reportGenerator.generateTextSummary(report)
            val intent = dataExporter.exportTextReport(text)
            _exportEvent.update { ExportEvent.ShareTextFile(intent) }
        }
    }

    fun shareTextSummary() {
        viewModelScope.launch {
            val report = cachedReport ?: return@launch
            val text = reportGenerator.generateTextSummary(report)
            val intent = dataExporter.shareTextSummary(text)
            _exportEvent.update { ExportEvent.ShareTextInline(intent) }
        }
    }

    fun consumeExportEvent() {
        _exportEvent.update { null }
    }
}
