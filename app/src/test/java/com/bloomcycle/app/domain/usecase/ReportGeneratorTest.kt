package com.bloomcycle.app.domain.usecase

import android.content.Context
import android.content.res.Resources
import com.bloomcycle.app.R
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.Symptom
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class ReportGeneratorTest {

    private lateinit var context: Context
    private lateinit var resources: Resources
    private lateinit var predictionEngine: CyclePredictionEngine
    private lateinit var reportGenerator: ReportGenerator

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        resources = mockk(relaxed = true)
        predictionEngine = mockk(relaxed = true)

        every { context.resources } returns resources
        every { context.getString(any()) } answers { "MockString_${firstArg<Int>()}" }
        every { context.getString(any(), *anyVararg()) } answers { "MockFormatted_${firstArg<Int>()}" }

        reportGenerator = ReportGenerator(context, predictionEngine)
    }

    // ── generateSummaryReport() ────────────────────────────────

    @Test
    fun `generateSummaryReport with empty logs returns zero totals`() {
        every { predictionEngine.computeCycleStats(any(), any(), any()) } returns CycleStats(
            averageCycleLength = 28f, shortestCycle = 28, longestCycle = 28,
            cycleVariation = 0, averagePeriodDuration = 5f, totalCyclesTracked = 0, totalDaysLogged = 0
        )
        every { predictionEngine.computeSymptomFrequency(any()) } returns emptyList()
        every { predictionEngine.computeMoodDistribution(any()) } returns emptyList()
        every { predictionEngine.computeFlowPattern(any()) } returns emptyList()

        val result = reportGenerator.generateSummaryReport(emptyList(), 28, 5)

        assertEquals(0, result.totalDaysTracked)
        assertEquals(0, result.totalPeriodsDetected)
        assertEquals(null, result.firstLogDate)
        assertEquals(null, result.lastLogDate)
    }

    @Test
    fun `generateSummaryReport with logs populates date range`() {
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 3, 1), flowIntensity = FlowIntensity.HEAVY),
            DailyLog(date = LocalDate.of(2026, 3, 2), flowIntensity = FlowIntensity.MEDIUM),
            DailyLog(date = LocalDate.of(2026, 4, 1), flowIntensity = FlowIntensity.HEAVY)
        )
        every { predictionEngine.computeCycleStats(any(), any(), any()) } returns CycleStats(
            averageCycleLength = 31f, shortestCycle = 31, longestCycle = 31,
            cycleVariation = 0, averagePeriodDuration = 2f, totalCyclesTracked = 1, totalDaysLogged = 3
        )
        every { predictionEngine.computeSymptomFrequency(any()) } returns emptyList()
        every { predictionEngine.computeMoodDistribution(any()) } returns emptyList()
        every { predictionEngine.computeFlowPattern(any()) } returns emptyList()

        val result = reportGenerator.generateSummaryReport(logs, 28, 5)

        assertEquals(3, result.totalDaysTracked)
        assertEquals(LocalDate.of(2026, 3, 1), result.firstLogDate)
        assertEquals(LocalDate.of(2026, 4, 1), result.lastLogDate)
    }

    @Test
    fun `generateSummaryReport delegates stats computation to predictionEngine`() {
        val logs = listOf(DailyLog(date = LocalDate.of(2026, 5, 1)))
        every { predictionEngine.computeCycleStats(any(), any(), any()) } returns CycleStats(
            averageCycleLength = 28f, shortestCycle = 28, longestCycle = 28,
            cycleVariation = 0, averagePeriodDuration = 5f, totalCyclesTracked = 0, totalDaysLogged = 1
        )
        every { predictionEngine.computeSymptomFrequency(any()) } returns emptyList()
        every { predictionEngine.computeMoodDistribution(any()) } returns emptyList()
        every { predictionEngine.computeFlowPattern(any()) } returns emptyList()

        reportGenerator.generateSummaryReport(logs, 28, 5)

        verify { predictionEngine.computeCycleStats(any(), 28, 5) }
        verify { predictionEngine.computeSymptomFrequency(any()) }
        verify { predictionEngine.computeMoodDistribution(any()) }
        verify { predictionEngine.computeFlowPattern(any()) }
    }

    // ── generateCsvExport() ────────────────────────────────────

    @Test
    fun `generateCsvExport starts with header from string resource`() {
        every { context.getString(R.string.report_csv_header) } returns "Date,Flow Intensity,Mood,Symptoms,Sexual Activity,Cervical Mucus,Temperature,Weight,Notes"

        val result = reportGenerator.generateCsvExport(emptyList())

        assertTrue(result.startsWith("Date,Flow Intensity,Mood,Symptoms"))
    }

    @Test
    fun `generateCsvExport includes log data rows`() {
        every { context.getString(R.string.report_csv_header) } returns "Header"
        val logs = listOf(
            DailyLog(
                date = LocalDate.of(2026, 5, 1),
                flowIntensity = FlowIntensity.HEAVY,
                mood = Mood.SAD,
                symptoms = listOf(Symptom.CRAMPS)
            )
        )

        val result = reportGenerator.generateCsvExport(logs)
        val lines = result.trim().lines()

        assertEquals(2, lines.size) // header + 1 data row
        assertTrue(lines[1].contains("2026-05-01"))
        assertTrue(lines[1].contains("Heavy"))
        assertTrue(lines[1].contains("Sad"))
        assertTrue(lines[1].contains("Cramps"))
    }

    @Test
    fun `generateCsvExport handles null fields gracefully`() {
        every { context.getString(R.string.report_csv_header) } returns "Header"
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 1))
        )

        val result = reportGenerator.generateCsvExport(logs)
        val dataLine = result.trim().lines()[1]

        // Null fields should produce empty strings, not "null"
        assertFalse(dataLine.contains("null"))
    }

    @Test
    fun `generateCsvExport sorts logs by date`() {
        every { context.getString(R.string.report_csv_header) } returns "Header"
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 3)),
            DailyLog(date = LocalDate.of(2026, 5, 1)),
            DailyLog(date = LocalDate.of(2026, 5, 2))
        )

        val result = reportGenerator.generateCsvExport(logs)
        val lines = result.trim().lines()

        assertTrue(lines[1].startsWith("2026-05-01"))
        assertTrue(lines[2].startsWith("2026-05-02"))
        assertTrue(lines[3].startsWith("2026-05-03"))
    }

    @Test
    fun `generateCsvExport escapes quotes in notes`() {
        every { context.getString(R.string.report_csv_header) } returns "Header"
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 1), notes = "She said \"hello\"")
        )

        val result = reportGenerator.generateCsvExport(logs)

        assertTrue(result.contains("\"\"hello\"\""))
    }

    // ── generateTextSummary() ──────────────────────────────────

    @Test
    fun `generateTextSummary uses localized strings from context`() {
        every { context.getString(R.string.report_header) } returns "Informe de Salud"
        every { context.getString(R.string.report_generated, any()) } returns "Generado: May 14, 2026"
        every { context.getString(R.string.report_overview_section) } returns "── Resumen ──"
        every { context.getString(R.string.report_total_days_tracked, any()) } returns "Total días: 0"
        every { context.getString(R.string.report_periods_detected, any()) } returns "Periodos: 0"
        every { context.getString(R.string.report_cycle_stats_section) } returns "── Estadísticas ──"
        every { context.getString(R.string.report_footer_app) } returns "Generado por BloomCycle"
        every { context.getString(R.string.report_footer_tagline) } returns "Seguimiento privado"

        val report = CycleSummaryReport(
            generatedAt = LocalDate.of(2026, 5, 14),
            totalDaysTracked = 0,
            firstLogDate = null, lastLogDate = null,
            totalPeriodsDetected = 0,
            cycleHistory = emptyList(),
            averageCycleLength = null, averagePeriodLength = null,
            shortestCycle = null, longestCycle = null,
            cycleStats = null,
            symptomFrequencies = emptyList(),
            moodDistribution = emptyList(),
            flowPatterns = emptyList()
        )

        val result = reportGenerator.generateTextSummary(report)

        assertTrue(result.contains("Informe de Salud"))
        assertTrue(result.contains("Generado por BloomCycle"))
    }

    @Test
    fun `generateTextSummary contains decorated separator lines`() {
        val report = CycleSummaryReport(
            generatedAt = LocalDate.of(2026, 5, 14),
            totalDaysTracked = 0,
            firstLogDate = null, lastLogDate = null,
            totalPeriodsDetected = 0,
            cycleHistory = emptyList(),
            averageCycleLength = null, averagePeriodLength = null,
            shortestCycle = null, longestCycle = null,
            cycleStats = null,
            symptomFrequencies = emptyList(),
            moodDistribution = emptyList(),
            flowPatterns = emptyList()
        )

        val result = reportGenerator.generateTextSummary(report)

        assertTrue(result.contains("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"))
    }
}
