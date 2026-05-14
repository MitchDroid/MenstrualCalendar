package com.bloomcycle.app.domain.usecase

import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FlowIntensity
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
 * Pure domain logic — no Android dependencies.
 */
@Singleton
class ReportGenerator @Inject constructor(
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

        // Header
        sb.appendLine("Date,Flow Intensity,Mood,Symptoms,Sexual Activity,Cervical Mucus,Temperature,Weight,Notes")

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
        sb.appendLine("    BloomCycle Health Report")
        sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        sb.appendLine()
        sb.appendLine("Generated: ${report.generatedAt.format(dateFormat)}")
        sb.appendLine()

        // ── Overview ────────────────────────────────
        sb.appendLine("── Overview ──")
        sb.appendLine("Total days tracked: ${report.totalDaysTracked}")
        report.firstLogDate?.let { sb.appendLine("First log: ${it.format(dateFormat)}") }
        report.lastLogDate?.let { sb.appendLine("Last log: ${it.format(dateFormat)}") }
        sb.appendLine("Periods detected: ${report.totalPeriodsDetected}")
        sb.appendLine()

        // ── Cycle Statistics ────────────────────────
        sb.appendLine("── Cycle Statistics ──")
        report.averageCycleLength?.let {
            sb.appendLine("Average cycle length: ${String.format("%.1f", it)} days")
        }
        report.averagePeriodLength?.let {
            sb.appendLine("Average period length: ${String.format("%.1f", it)} days")
        }
        report.shortestCycle?.let { sb.appendLine("Shortest cycle: $it days") }
        report.longestCycle?.let { sb.appendLine("Longest cycle: $it days") }
        sb.appendLine()

        // ── Cycle History ───────────────────────────
        if (report.cycleHistory.isNotEmpty()) {
            sb.appendLine("── Cycle History ──")
            report.cycleHistory.forEachIndexed { index, entry ->
                val cycleLenStr = entry.cycleLength?.let { "${it}d cycle" } ?: "ongoing"
                sb.appendLine(
                    "${index + 1}. ${entry.periodStart.format(dateFormat)} – " +
                            "${entry.periodEnd.format(dateFormat)} " +
                            "(${entry.periodLength}d period, $cycleLenStr)"
                )
            }
            sb.appendLine()
        }

        // ── Top Symptoms ────────────────────────────
        if (report.symptomFrequencies.isNotEmpty()) {
            sb.appendLine("── Top Symptoms ──")
            report.symptomFrequencies.take(5).forEach { freq ->
                sb.appendLine(
                    "• ${formatEnum(freq.symptom.name)}: ${freq.count} days " +
                            "(${(freq.percentage * 100).toInt()}%)"
                )
            }
            sb.appendLine()
        }

        // ── Mood Summary ────────────────────────────
        if (report.moodDistribution.isNotEmpty()) {
            sb.appendLine("── Mood Summary ──")
            report.moodDistribution.forEach { mood ->
                sb.appendLine(
                    "• ${formatEnum(mood.mood.name)}: ${mood.count} days " +
                            "(${(mood.percentage * 100).toInt()}%)"
                )
            }
            sb.appendLine()
        }

        // ── Flow Pattern ────────────────────────────
        if (report.flowPatterns.isNotEmpty()) {
            sb.appendLine("── Flow Pattern ──")
            report.flowPatterns.forEach { flow ->
                sb.appendLine(
                    "• ${formatEnum(flow.intensity.name)}: ${flow.dayCount} days " +
                            "(${(flow.percentage * 100).toInt()}%)"
                )
            }
            sb.appendLine()
        }

        sb.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        sb.appendLine("  Generated by BloomCycle")
        sb.appendLine("  Privacy-first period tracking")
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
