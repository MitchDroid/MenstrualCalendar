package com.bloomcycle.app.domain.usecase

import android.content.Context
import com.bloomcycle.app.R
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FlowIntensity
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

        val cycleHistory = buildCycleHistory(sortedLogs)
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
            val symptoms = log.symptoms.joinToString("; ") { formatEnum(it.name) }
            sb.appendLine(
                listOf(
                    log.date.toString(),
                    log.flowIntensity?.name?.let { formatEnum(it) } ?: "",
                    log.mood?.name?.let { formatEnum(it) } ?: "",
                    "\"$symptoms\"",
                    log.sexualActivity?.name?.let { formatEnum(it) } ?: "",
                    log.cervicalMucus?.name?.let { formatEnum(it) } ?: "",
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
                        formatEnum(freq.symptom.name),
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
                        formatEnum(mood.mood.name),
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
                        formatEnum(flow.intensity.name),
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

    // ── Private Helpers ─────────────────────────────────────────

    /**
     * Detects period starts from flow data and builds a history of cycles
     * with period start/end dates and cycle lengths.
     */
    private fun buildCycleHistory(logs: List<DailyLog>): List<CycleHistoryEntry> {
        val periodDays = logs
            .filter { it.flowIntensity != null && it.flowIntensity != FlowIntensity.NONE }
            .map { it.date }
            .sorted()

        if (periodDays.isEmpty()) return emptyList()

        // Group consecutive (±1 day gap) period days into period spans
        val periods = mutableListOf<MutableList<LocalDate>>()
        var currentPeriod = mutableListOf(periodDays.first())

        for (i in 1 until periodDays.size) {
            val gap = ChronoUnit.DAYS.between(periodDays[i - 1], periodDays[i])
            if (gap > 3) {
                periods.add(currentPeriod)
                currentPeriod = mutableListOf(periodDays[i])
            } else {
                currentPeriod.add(periodDays[i])
            }
        }
        periods.add(currentPeriod)

        // Build cycle history entries
        return periods.mapIndexed { index, periodSpan ->
            val start = periodSpan.first()
            val end = periodSpan.last()
            val periodLength = ChronoUnit.DAYS.between(start, end).toInt() + 1

            val cycleLength = if (index + 1 < periods.size) {
                val nextStart = periods[index + 1].first()
                ChronoUnit.DAYS.between(start, nextStart).toInt()
            } else {
                null // Most recent period — cycle not yet complete
            }

            CycleHistoryEntry(
                periodStart = start,
                periodEnd = end,
                periodLength = periodLength,
                cycleLength = cycleLength
            )
        }
    }

    private fun formatEnum(name: String): String =
        name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
}
