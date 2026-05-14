package com.bloomcycle.app.domain.usecase

import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FertilityStatus
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.Symptom
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class CyclePredictionEngineTest {

    private lateinit var engine: CyclePredictionEngine

    @Before
    fun setUp() {
        engine = CyclePredictionEngine()
    }

    // ── predict() ──────────────────────────────────────────────

    @Test
    fun `predict returns menstrual phase on cycle day 1`() {
        val lastPeriod = LocalDate.of(2026, 5, 1)
        val today = LocalDate.of(2026, 5, 1)

        val result = engine.predict(lastPeriod, cycleLength = 28, periodDuration = 5, today = today)

        assertEquals(CyclePhase.MENSTRUAL, result.currentPhase)
        assertEquals(1, result.currentCycleDay)
    }

    @Test
    fun `predict returns menstrual phase on day 5 with 5-day period`() {
        val lastPeriod = LocalDate.of(2026, 5, 1)
        val today = LocalDate.of(2026, 5, 5)

        val result = engine.predict(lastPeriod, cycleLength = 28, periodDuration = 5, today = today)

        assertEquals(CyclePhase.MENSTRUAL, result.currentPhase)
        assertEquals(5, result.currentCycleDay)
    }

    @Test
    fun `predict returns follicular phase after period ends`() {
        val lastPeriod = LocalDate.of(2026, 5, 1)
        val today = LocalDate.of(2026, 5, 8) // Day 8

        val result = engine.predict(lastPeriod, cycleLength = 28, periodDuration = 5, today = today)

        assertEquals(CyclePhase.FOLLICULAR, result.currentPhase)
    }

    @Test
    fun `predict returns ovulation phase around day 14 for 28-day cycle`() {
        val lastPeriod = LocalDate.of(2026, 5, 1)
        // Ovulation day = 28 - 14 = 14
        val today = LocalDate.of(2026, 5, 14) // Day 14

        val result = engine.predict(lastPeriod, cycleLength = 28, periodDuration = 5, today = today)

        assertEquals(CyclePhase.OVULATION, result.currentPhase)
    }

    @Test
    fun `predict returns luteal phase after ovulation`() {
        val lastPeriod = LocalDate.of(2026, 5, 1)
        val today = LocalDate.of(2026, 5, 20) // Day 20

        val result = engine.predict(lastPeriod, cycleLength = 28, periodDuration = 5, today = today)

        assertEquals(CyclePhase.LUTEAL, result.currentPhase)
    }

    @Test
    fun `predict calculates correct next period start`() {
        val lastPeriod = LocalDate.of(2026, 5, 1)
        val today = LocalDate.of(2026, 5, 10)

        val result = engine.predict(lastPeriod, cycleLength = 28, periodDuration = 5, today = today)

        assertEquals(LocalDate.of(2026, 5, 29), result.nextPeriodStart)
    }

    @Test
    fun `predict calculates correct next period end`() {
        val lastPeriod = LocalDate.of(2026, 5, 1)
        val today = LocalDate.of(2026, 5, 10)

        val result = engine.predict(lastPeriod, cycleLength = 28, periodDuration = 5, today = today)

        assertEquals(LocalDate.of(2026, 6, 2), result.nextPeriodEnd)
    }

    @Test
    fun `predict returns peak fertility on ovulation day`() {
        val lastPeriod = LocalDate.of(2026, 5, 1)
        val ovulationDay = 14 // 28 - 14
        val today = lastPeriod.plusDays((ovulationDay - 1).toLong())

        val result = engine.predict(lastPeriod, cycleLength = 28, periodDuration = 5, today = today)

        assertEquals(FertilityStatus.PEAK, result.fertilityStatus)
    }

    @Test
    fun `predict returns high fertility in fertile window`() {
        val lastPeriod = LocalDate.of(2026, 5, 1)
        // Ovulation day = 14, fertile window = day 9-14
        val today = LocalDate.of(2026, 5, 11) // Day 11 = ovulation-3

        val result = engine.predict(lastPeriod, cycleLength = 28, periodDuration = 5, today = today)

        assertEquals(FertilityStatus.HIGH, result.fertilityStatus)
    }

    @Test
    fun `predict returns low fertility during menstrual phase`() {
        val lastPeriod = LocalDate.of(2026, 5, 1)
        val today = LocalDate.of(2026, 5, 3) // Day 3

        val result = engine.predict(lastPeriod, cycleLength = 28, periodDuration = 5, today = today)

        assertEquals(FertilityStatus.LOW, result.fertilityStatus)
    }

    @Test
    fun `predict daysUntilNextPeriod is non-negative`() {
        val lastPeriod = LocalDate.of(2026, 5, 1)
        val today = LocalDate.of(2026, 5, 28) // Day 28 = just before next period

        val result = engine.predict(lastPeriod, cycleLength = 28, periodDuration = 5, today = today)

        assertTrue(result.daysUntilNextPeriod >= 0)
    }

    @Test
    fun `predict wraps correctly into next cycle`() {
        val lastPeriod = LocalDate.of(2026, 4, 1)
        val today = LocalDate.of(2026, 5, 2) // 31 days later → day 4 of next cycle

        val result = engine.predict(lastPeriod, cycleLength = 28, periodDuration = 5, today = today)

        assertEquals(4, result.currentCycleDay)
        assertEquals(CyclePhase.MENSTRUAL, result.currentPhase)
    }

    @Test
    fun `predict with short cycle length adjusts ovulation day`() {
        val lastPeriod = LocalDate.of(2026, 5, 1)
        val today = LocalDate.of(2026, 5, 7) // Day 7

        // 21-day cycle → ovulation day = 21 - 14 = 7
        val result = engine.predict(lastPeriod, cycleLength = 21, periodDuration = 4, today = today)

        assertEquals(CyclePhase.OVULATION, result.currentPhase)
    }

    @Test
    fun `predict with long cycle length`() {
        val lastPeriod = LocalDate.of(2026, 5, 1)
        val today = LocalDate.of(2026, 5, 10) // Day 10

        // 35-day cycle → ovulation day = 35 - 14 = 21
        val result = engine.predict(lastPeriod, cycleLength = 35, periodDuration = 5, today = today)

        assertEquals(CyclePhase.FOLLICULAR, result.currentPhase)
    }

    // ── computeCycleStats() ────────────────────────────────────

    @Test
    fun `computeCycleStats with no logs returns defaults`() {
        val result = engine.computeCycleStats(emptyList(), defaultCycleLength = 28, defaultPeriodDuration = 5)

        assertEquals(28f, result.averageCycleLength)
        assertEquals(5f, result.averagePeriodDuration)
        assertEquals(0, result.totalCyclesTracked)
    }

    @Test
    fun `computeCycleStats with single period returns defaults for cycle length`() {
        val logs = createPeriodLogs(LocalDate.of(2026, 4, 1), days = 5)

        val result = engine.computeCycleStats(logs, defaultCycleLength = 28, defaultPeriodDuration = 5)

        assertEquals(28f, result.averageCycleLength)
        assertEquals(1, result.totalCyclesTracked)
        assertEquals(5, result.totalDaysLogged)
    }

    @Test
    fun `computeCycleStats with two periods calculates cycle length`() {
        val period1 = createPeriodLogs(LocalDate.of(2026, 3, 1), days = 5)
        val period2 = createPeriodLogs(LocalDate.of(2026, 3, 29), days = 5)
        val logs = period1 + period2

        val result = engine.computeCycleStats(logs, defaultCycleLength = 28, defaultPeriodDuration = 5)

        assertEquals(28f, result.averageCycleLength)
        assertEquals(5f, result.averagePeriodDuration)
    }

    @Test
    fun `computeCycleStats with three periods calculates average`() {
        val period1 = createPeriodLogs(LocalDate.of(2026, 1, 1), days = 5)
        val period2 = createPeriodLogs(LocalDate.of(2026, 1, 29), days = 4) // 28-day cycle
        val period3 = createPeriodLogs(LocalDate.of(2026, 2, 28), days = 5) // 31-day cycle
        val logs = period1 + period2 + period3

        val result = engine.computeCycleStats(logs, defaultCycleLength = 28, defaultPeriodDuration = 5)

        // Jan 1→Jan 29 = 28 days, Jan 29→Feb 28 = 30 days → average = 29.0
        assertEquals(29.0f, result.averageCycleLength)
        assertEquals(28, result.shortestCycle)
        assertEquals(30, result.longestCycle)
        assertEquals(2, result.cycleVariation)
    }

    @Test
    fun `computeCycleStats filters outlier cycle lengths`() {
        val period1 = createPeriodLogs(LocalDate.of(2026, 1, 1), days = 5)
        // Only 10 days apart → outlier (<15), should be filtered
        val period2 = createPeriodLogs(LocalDate.of(2026, 1, 11), days = 3)
        val period3 = createPeriodLogs(LocalDate.of(2026, 2, 8), days = 5) // 28 days from period2

        val logs = period1 + period2 + period3

        val result = engine.computeCycleStats(logs, defaultCycleLength = 28, defaultPeriodDuration = 5)

        // The 10-day cycle should be filtered, only the 28-day cycle counts
        assertEquals(28f, result.averageCycleLength)
    }

    @Test
    fun `computeCycleStats correctly counts total days logged`() {
        val logs = createPeriodLogs(LocalDate.of(2026, 3, 1), days = 5)

        val result = engine.computeCycleStats(logs, defaultCycleLength = 28, defaultPeriodDuration = 5)

        assertEquals(5, result.totalDaysLogged)
    }

    // ── computeSymptomFrequency() ──────────────────────────────

    @Test
    fun `computeSymptomFrequency with no logs returns empty`() {
        val result = engine.computeSymptomFrequency(emptyList())

        assertTrue(result.isEmpty())
    }

    @Test
    fun `computeSymptomFrequency with no symptoms returns empty`() {
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 1), symptoms = emptyList())
        )

        val result = engine.computeSymptomFrequency(logs)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `computeSymptomFrequency counts symptom occurrences`() {
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 1), symptoms = listOf(Symptom.CRAMPS, Symptom.HEADACHE)),
            DailyLog(date = LocalDate.of(2026, 5, 2), symptoms = listOf(Symptom.CRAMPS)),
            DailyLog(date = LocalDate.of(2026, 5, 3), symptoms = listOf(Symptom.BLOATING))
        )

        val result = engine.computeSymptomFrequency(logs)

        val cramps = result.find { it.symptom == Symptom.CRAMPS }!!
        assertEquals(2, cramps.count)

        val headache = result.find { it.symptom == Symptom.HEADACHE }!!
        assertEquals(1, headache.count)
    }

    @Test
    fun `computeSymptomFrequency sorts by count descending`() {
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 1), symptoms = listOf(Symptom.CRAMPS, Symptom.HEADACHE)),
            DailyLog(date = LocalDate.of(2026, 5, 2), symptoms = listOf(Symptom.CRAMPS, Symptom.HEADACHE)),
            DailyLog(date = LocalDate.of(2026, 5, 3), symptoms = listOf(Symptom.CRAMPS))
        )

        val result = engine.computeSymptomFrequency(logs)

        assertEquals(Symptom.CRAMPS, result.first().symptom)
        assertEquals(3, result.first().count)
    }

    @Test
    fun `computeSymptomFrequency calculates percentages correctly`() {
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 1), symptoms = listOf(Symptom.CRAMPS)),
            DailyLog(date = LocalDate.of(2026, 5, 2), symptoms = listOf(Symptom.CRAMPS)),
            DailyLog(date = LocalDate.of(2026, 5, 3), symptoms = listOf(Symptom.BLOATING)),
            DailyLog(date = LocalDate.of(2026, 5, 4), symptoms = listOf(Symptom.BLOATING))
        )

        val result = engine.computeSymptomFrequency(logs)

        val cramps = result.find { it.symptom == Symptom.CRAMPS }!!
        assertEquals(0.5f, cramps.percentage, 0.01f)
    }

    // ── computeMoodDistribution() ──────────────────────────────

    @Test
    fun `computeMoodDistribution with no moods returns empty`() {
        val logs = listOf(DailyLog(date = LocalDate.of(2026, 5, 1)))

        val result = engine.computeMoodDistribution(logs)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `computeMoodDistribution counts mood occurrences`() {
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 1), mood = Mood.HAPPY),
            DailyLog(date = LocalDate.of(2026, 5, 2), mood = Mood.HAPPY),
            DailyLog(date = LocalDate.of(2026, 5, 3), mood = Mood.SAD),
            DailyLog(date = LocalDate.of(2026, 5, 4), mood = Mood.ANXIOUS)
        )

        val result = engine.computeMoodDistribution(logs)

        val happy = result.find { it.mood == Mood.HAPPY }!!
        assertEquals(2, happy.count)
        assertEquals(0.5f, happy.percentage, 0.01f)
    }

    @Test
    fun `computeMoodDistribution sorts by count descending`() {
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 1), mood = Mood.CALM),
            DailyLog(date = LocalDate.of(2026, 5, 2), mood = Mood.CALM),
            DailyLog(date = LocalDate.of(2026, 5, 3), mood = Mood.CALM),
            DailyLog(date = LocalDate.of(2026, 5, 4), mood = Mood.SAD)
        )

        val result = engine.computeMoodDistribution(logs)

        assertEquals(Mood.CALM, result.first().mood)
    }

    // ── computeFlowPattern() ───────────────────────────────────

    @Test
    fun `computeFlowPattern with no flow data returns empty`() {
        val logs = listOf(DailyLog(date = LocalDate.of(2026, 5, 1)))

        val result = engine.computeFlowPattern(logs)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `computeFlowPattern excludes NONE intensity`() {
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 1), flowIntensity = FlowIntensity.NONE),
            DailyLog(date = LocalDate.of(2026, 5, 2), flowIntensity = FlowIntensity.LIGHT)
        )

        val result = engine.computeFlowPattern(logs)

        assertTrue(result.none { it.intensity == FlowIntensity.NONE })
        assertEquals(1, result.size)
    }

    @Test
    fun `computeFlowPattern counts intensity distribution`() {
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 1), flowIntensity = FlowIntensity.HEAVY),
            DailyLog(date = LocalDate.of(2026, 5, 2), flowIntensity = FlowIntensity.HEAVY),
            DailyLog(date = LocalDate.of(2026, 5, 3), flowIntensity = FlowIntensity.MEDIUM),
            DailyLog(date = LocalDate.of(2026, 5, 4), flowIntensity = FlowIntensity.LIGHT),
            DailyLog(date = LocalDate.of(2026, 5, 5), flowIntensity = FlowIntensity.SPOTTING)
        )

        val result = engine.computeFlowPattern(logs)

        val heavy = result.find { it.intensity == FlowIntensity.HEAVY }!!
        assertEquals(2, heavy.dayCount)
        assertEquals(0.4f, heavy.percentage, 0.01f)
    }

    // ── Helpers ─────────────────────────────────────────────────

    private fun createPeriodLogs(startDate: LocalDate, days: Int): List<DailyLog> =
        (0 until days).map { offset ->
            DailyLog(
                date = startDate.plusDays(offset.toLong()),
                flowIntensity = if (offset < 2) FlowIntensity.HEAVY else FlowIntensity.MEDIUM
            )
        }
}
