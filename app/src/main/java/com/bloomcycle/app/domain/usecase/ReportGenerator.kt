package com.bloomcycle.app.domain.usecase

import android.content.Context
import com.bloomcycle.app.R
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.ui.util.displayNameRes
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

// ── Report Data Models ──────────────────────────────────────────

data class CycleHistoryEntry(
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val periodLength: Int,
    val cycleLength: Int?  // null for the most recent (ongoing)
)

data class CycleSummaryReport(
    val generatedAt: LocalDate,
    val totalDaysTracked: Int,
    val firstLogDate: LocalDate?,
    val lastLogDate: LocalDate?,
    val totalPeriodsDetected: Int,
    val cycleHistory: List<CycleHistoryEntry>,
    val averageCycleLength: Float?,
    val averagePeriodLength: Float?,
    val shortestCycle: Int?,
    val longestCycle: Int?,
    val cycleStats: CycleStats?,
    val symptomFrequencies: List<SymptomFrequency>,
    val moodDistribution: List<MoodDistribution>,
    val flowPatterns: List<FlowPattern>
)

/**
 * Generates structured reports and exportable data from daily logs.
 * Uses Android context for localized string resources.
 */
@Singleton
class ReportGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val predictionEngine: CyclePredictionEngine
) {

    /**
     * Builds a comprehensive cycle summary report from all logged data.
     */
    fun generateSummaryReport(
        logs: List<DailyLog>,
        defaultCycleLength: Int,
        defaultPeriodDuration: Int
    ): CycleSummaryReport {
        val sortedLogs = logs.sortedBy { it.date }
        val today = LocalDate.now()

        val cycleHistory = predictionEngine.computeCycleHistory(sortedLogs)
        val cycleLengths = cycleHistory.mapNotNull { it.cycleLength }.filter { it in 15..60 }

        return CycleSummaryReport(
            generatedAt = today,
            totalDaysTracked = sortedLogs.size,
            firstLogDate = sortedLogs.firstOrNull()?.date,
            lastLogDate = sortedLogs.lastOrNull()?.date,
            totalPeriodsDetected = cycleHistory.size,
            cycleHistory = cycleHistory,
            averageCycleLength = if (cycleLengths.isNotEmpty()) cycleLengths.average().toFloat() else null,
            averagePeriodLength = if (cycleHistory.isNotEmpty()) cycleHistory.map { it.periodLength }.average().toFloat() else null,
            shortestCycle = cycleLengths.minOrNull(),
            longestCycle = cycleLengths.maxOrNull(),
            cycleStats = predictionEngine.computeCycleStats(sortedLogs, defaultCycleLength, defaultPeriodDuration),
            symptomFrequencies = predictionEngine.computeSymptomFrequency(sortedLogs),
            moodDistribution = predictionEngine.computeMoodDistribution(sortedLogs),
            flowPatterns = predictionEngine.computeFlowPattern(sortedLogs)
        )
    }

    /**
     * Generates CSV content from daily logs for export.
     */
    fun generateCsvExport(logs: List<DailyLog>): String {
        val sb = StringBuilder()

        // Localized header
        sb.appendLine(context.getString(R.string.report_csv_header))

        // Rows sorted by date
        logs.sortedBy { it.date }.forEach { log ->
            val symptoms = log.symptoms.joinToString("; ") { context.getString(it.displayNameRes()) }
            sb.appendLine(
                listOf(
                    log.date.toString(),
                    log.flowIntensity?.let { context.getString(it.displayNameRes()) } ?: "",
                    log.mood?.let { context.getString(it.displayNameRes()) } ?: "",
                    "\"$symptoms\"",
                    log.sexualActivity?.let { context.getString(it.displayNameRes()) } ?: "",
                    log.cervicalMucus?.let { context.getString(it.displayNameRes()) } ?: "",
                    log.temperature?.let { String.format("%.1f", it) } ?: "",
                    log.weight?.let { String.format("%.1f", it) } ?: "",
                    "\"${log.notes?.replace("\"", "\"\"") ?: ""}\""
                ).joinToString(",")
            )
        }

        return sb.toString()
    }

    /**
     * Generates a human-readable text summary for sharing.
     */
    fun generateTextSummary(report: CycleSummaryReport): String {
        val dateFormat = DateTimeFormatter.ofPattern("MMM d, yyyy")
        val sb = StringBuilder()

        sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        sb.appendLine("    ${context.getString(R.string.report_header)}")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        sb.appendLine()
        sb.appendLine(context.getString(R.string.report_generated, report.generatedAt.format(dateFormat)))
        sb.appendLine()

        // ── Overview ────────────────────────────────
        sb.appendLine(context.getString(R.string.report_overview_section))
        sb.appendLine(context.getString(R.string.report_total_days_tracked, report.totalDaysTracked))
        report.firstLogDate?.let {
            sb.appendLine(context.getString(R.string.report_first_log, it.format(dateFormat)))
        }
        report.lastLogDate?.let {
            sb.appendLine(context.getString(R.string.report_last_log, it.format(dateFormat)))
        }
        sb.appendLine(context.getString(R.string.report_periods_detected, report.totalPeriodsDetected))
        sb.appendLine()

        // ── Cycle Statistics ────────────────────────
        sb.appendLine(context.getString(R.string.report_cycle_stats_section))
        report.averageCycleLength?.let {
            sb.appendLine(context.getString(R.string.report_avg_cycle, String.format("%.1f", it)))
        }
        report.averagePeriodLength?.let {
            sb.appendLine(context.getString(R.string.report_avg_period, String.format("%.1f", it)))
        }
        report.shortestCycle?.let {
            sb.appendLine(context.getString(R.string.report_shortest_cycle, it))
        }
        report.longestCycle?.let {
            sb.appendLine(context.getString(R.string.report_longest_cycle, it))
        }
        sb.appendLine()

        // ── Cycle History ───────────────────────────
        if (report.cycleHistory.isNotEmpty()) {
            sb.appendLine(context.getString(R.string.report_cycle_history_section))
            report.cycleHistory.forEachIndexed { index, entry ->
                val cycleLenStr = entry.cycleLength?.let {
                    context.getString(R.string.report_cycle_len, it)
                } ?: context.getString(R.string.report_cycle_ongoing)
                sb.appendLine(
                    context.getString(
                        R.string.report_cycle_entry,
                        index + 1,
                        entry.periodStart.format(dateFormat),
                        entry.periodEnd.format(dateFormat),
                        entry.periodLength,
                        cycleLenStr
                    )
                )
            }
            sb.appendLine()
        }

        // ── Top Symptoms ────────────────────────────
        if (report.symptomFrequencies.isNotEmpty()) {
            sb.appendLine(context.getString(R.string.report_top_symptoms_section))
            report.symptomFrequencies.take(5).forEach { freq ->
                sb.appendLine(
                    context.getString(
                        R.string.report_symptom_entry,
                        context.getString(freq.symptom.displayNameRes()),
                        freq.count,
                        (freq.percentage * 100).toInt()
                    )
                )
            }
            sb.appendLine()
        }

        // ── Mood Summary ────────────────────────────
        if (report.moodDistribution.isNotEmpty()) {
            sb.appendLine(context.getString(R.string.report_mood_section))
            report.moodDistribution.forEach { mood ->
                sb.appendLine(
                    context.getString(
                        R.string.report_mood_entry,
                        context.getString(mood.mood.displayNameRes()),
                        mood.count,
                        (mood.percentage * 100).toInt()
                    )
                )
            }
            sb.appendLine()
        }

        // ── Flow Pattern ────────────────────────────
        if (report.flowPatterns.isNotEmpty()) {
            sb.appendLine(context.getString(R.string.report_flow_section))
            report.flowPatterns.forEach { flow ->
                sb.appendLine(
                    context.getString(
                        R.string.report_flow_entry,
                        context.getString(flow.intensity.displayNameRes()),
                        flow.dayCount,
                        (flow.percentage * 100).toInt()
                    )
                )
            }
            sb.appendLine()
        }

        sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        sb.appendLine("  ${context.getString(R.string.report_footer_app)}")
        sb.appendLine("  ${context.getString(R.string.report_footer_tagline)}")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

        return sb.toString()
    }

}
