package com.bloomcycle.app.domain.usecase

import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FertilityStatus
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.Symptom
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

// ── Data Models ──────────────────────────────────────────────

data class CyclePrediction(
    val nextPeriodStart: LocalDate,
    val nextPeriodEnd: LocalDate,
    val fertileWindowStart: LocalDate,
    val fertileWindowEnd: LocalDate,
    val ovulationDate: LocalDate,
    val currentCycleDay: Int,
    val currentPhase: CyclePhase,
    val fertilityStatus: FertilityStatus,
    val daysUntilNextPeriod: Int
)

data class CycleStats(
    val averageCycleLength: Float,
    val shortestCycle: Int,
    val longestCycle: Int,
    val cycleVariation: Int,
    val averagePeriodDuration: Float,
    val totalCyclesTracked: Int,
    val totalDaysLogged: Int
)

data class SymptomFrequency(
    val symptom: Symptom,
    val count: Int,
    val percentage: Float
)

data class MoodDistribution(
    val mood: Mood,
    val count: Int,
    val percentage: Float
)

data class FlowPattern(
    val intensity: FlowIntensity,
    val dayCount: Int,
    val percentage: Float
)

/**
 * Pure prediction engine. No Android dependencies — testable in isolation.
 */
@Singleton
class CyclePredictionEngine @Inject constructor() {

    // ── Predictions ──────────────────────────────────────────

    fun predict(
        lastPeriodDate: LocalDate,
        cycleLength: Int,
        periodDuration: Int,
        today: LocalDate = LocalDate.now()
    ): CyclePrediction {
        val daysSince = ChronoUnit.DAYS.between(lastPeriodDate, today).toInt()
        val cycleDay = (daysSince % cycleLength) + 1
        val currentCycleStart = today.minusDays((cycleDay - 1).toLong())

        val nextPeriodStart = currentCycleStart.plusDays(cycleLength.toLong())
        val nextPeriodEnd = nextPeriodStart.plusDays((periodDuration - 1).toLong())

        val ovulationDay = cycleLength - 14
        val ovulationDate = currentCycleStart.plusDays((ovulationDay - 1).toLong())
        val fertileStart = ovulationDate.minusDays(5)
        val fertileEnd = ovulationDate.plusDays(1)

        val phase = when {
            cycleDay in 1..periodDuration -> CyclePhase.MENSTRUAL
            cycleDay in (periodDuration + 1) until ovulationDay -> CyclePhase.FOLLICULAR
            cycleDay in ovulationDay..(ovulationDay + 1) -> CyclePhase.OVULATION
            else -> CyclePhase.LUTEAL
        }

        val fertility = when {
            cycleDay == ovulationDay -> FertilityStatus.PEAK
            cycleDay in (ovulationDay - 5)..ovulationDay -> FertilityStatus.HIGH
            cycleDay in (ovulationDay - 7)..(ovulationDay + 2) -> FertilityStatus.MEDIUM
            else -> FertilityStatus.LOW
        }

        val daysUntilNext = ChronoUnit.DAYS.between(today, nextPeriodStart).toInt()

        return CyclePrediction(
            nextPeriodStart = nextPeriodStart,
            nextPeriodEnd = nextPeriodEnd,
            fertileWindowStart = fertileStart,
            fertileWindowEnd = fertileEnd,
            ovulationDate = ovulationDate,
            currentCycleDay = cycleDay,
            currentPhase = phase,
            fertilityStatus = fertility,
            daysUntilNextPeriod = daysUntilNext.coerceAtLeast(0)
        )
    }

    // ── Cycle Statistics ─────────────────────────────────────

    fun computeCycleStats(
        logs: List<DailyLog>,
        defaultCycleLength: Int,
        defaultPeriodDuration: Int
    ): CycleStats {
        val periodStartDates = detectPeriodStarts(logs)

        if (periodStartDates.size < 2) {
            return CycleStats(
                averageCycleLength = defaultCycleLength.toFloat(),
                shortestCycle = defaultCycleLength,
                longestCycle = defaultCycleLength,
                cycleVariation = 0,
                averagePeriodDuration = defaultPeriodDuration.toFloat(),
                totalCyclesTracked = periodStartDates.size,
                totalDaysLogged = logs.size
            )
        }

        val cycleLengths = periodStartDates.zipWithNext().map { (a, b) ->
            ChronoUnit.DAYS.between(a, b).toInt()
        }.filter { it in 15..60 } // Filter outliers

        val periodDurations = periodStartDates.map { start ->
            countConsecutivePeriodDays(logs, start)
        }

        val avgCycle = if (cycleLengths.isNotEmpty()) cycleLengths.average().toFloat() else defaultCycleLength.toFloat()
        val avgPeriod = if (periodDurations.isNotEmpty()) periodDurations.average().toFloat() else defaultPeriodDuration.toFloat()

        return CycleStats(
            averageCycleLength = avgCycle,
            shortestCycle = cycleLengths.minOrNull() ?: defaultCycleLength,
            longestCycle = cycleLengths.maxOrNull() ?: defaultCycleLength,
            cycleVariation = (cycleLengths.maxOrNull() ?: 0) - (cycleLengths.minOrNull() ?: 0),
            averagePeriodDuration = avgPeriod,
            totalCyclesTracked = cycleLengths.size,
            totalDaysLogged = logs.size
        )
    }

    // ── Symptom Analysis ─────────────────────────────────────

    fun computeSymptomFrequency(logs: List<DailyLog>): List<SymptomFrequency> {
        val logsWithSymptoms = logs.filter { it.symptoms.isNotEmpty() }
        if (logsWithSymptoms.isEmpty()) return emptyList()

        val total = logsWithSymptoms.size.toFloat()
        return Symptom.entries.map { symptom ->
            val count = logsWithSymptoms.count { symptom in it.symptoms }
            SymptomFrequency(symptom, count, count / total)
        }.filter { it.count > 0 }
            .sortedByDescending { it.count }
    }

    // ── Mood Analysis ────────────────────────────────────────

    fun computeMoodDistribution(logs: List<DailyLog>): List<MoodDistribution> {
        val logsWithMood = logs.filter { it.mood != null }
        if (logsWithMood.isEmpty()) return emptyList()

        val total = logsWithMood.size.toFloat()
        return Mood.entries.map { mood ->
            val count = logsWithMood.count { it.mood == mood }
            MoodDistribution(mood, count, count / total)
        }.filter { it.count > 0 }
            .sortedByDescending { it.count }
    }

    // ── Flow Analysis ────────────────────────────────────────

    fun computeFlowPattern(logs: List<DailyLog>): List<FlowPattern> {
        val logsWithFlow = logs.filter { it.flowIntensity != null && it.flowIntensity != FlowIntensity.NONE }
        if (logsWithFlow.isEmpty()) return emptyList()

        val total = logsWithFlow.size.toFloat()
        return FlowIntensity.entries
            .filter { it != FlowIntensity.NONE }
            .map { intensity ->
                val count = logsWithFlow.count { it.flowIntensity == intensity }
                FlowPattern(intensity, count, count / total)
            }.filter { it.dayCount > 0 }
    }

    // ── Helpers ──────────────────────────────────────────────

    private fun detectPeriodStarts(logs: List<DailyLog>): List<LocalDate> {
        val periodDays = logs
            .filter { it.flowIntensity != null && it.flowIntensity != FlowIntensity.NONE }
            .map { it.date }
            .sorted()

        if (periodDays.isEmpty()) return emptyList()

        val starts = mutableListOf(periodDays.first())
        for (i in 1 until periodDays.size) {
            val gap = ChronoUnit.DAYS.between(periodDays[i - 1], periodDays[i])
            if (gap > 3) { // Gap of >3 days = new period
                starts.add(periodDays[i])
            }
        }
        return starts
    }

    private fun countConsecutivePeriodDays(logs: List<DailyLog>, start: LocalDate): Int {
        val logMap = logs.associateBy { it.date }
        var day = start
        var count = 0
        while (true) {
            val log = logMap[day]
            if (log?.flowIntensity != null && log.flowIntensity != FlowIntensity.NONE) {
                count++
                day = day.plusDays(1)
            } else {
                break
            }
        }
        return count.coerceAtLeast(1)
    }
}
