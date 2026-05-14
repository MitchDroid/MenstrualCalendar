package com.bloomcycle.app.ui.insights

import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FertilityStatus
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.Symptom
import com.bloomcycle.app.domain.model.UserGoal
import com.bloomcycle.app.domain.repository.DailyLogRepository
import com.bloomcycle.app.domain.usecase.CyclePrediction
import com.bloomcycle.app.domain.usecase.CyclePredictionEngine
import com.bloomcycle.app.domain.usecase.CycleStats
import com.bloomcycle.app.domain.usecase.FlowPattern
import com.bloomcycle.app.domain.usecase.MoodDistribution
import com.bloomcycle.app.domain.usecase.SymptomFrequency
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dailyLogRepository: DailyLogRepository
    private lateinit var preferencesManager: UserPreferencesManager
    private lateinit var predictionEngine: CyclePredictionEngine

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        dailyLogRepository = mockk(relaxed = true)
        preferencesManager = mockk(relaxed = true)
        predictionEngine = mockk(relaxed = true)

        every { dailyLogRepository.getAllLogs() } returns flowOf(emptyList())
        every { preferencesManager.averageCycleLength } returns flowOf(28)
        every { preferencesManager.averagePeriodDuration } returns flowOf(5)
        every { preferencesManager.lastPeriodDate } returns flowOf(LocalDate.of(2026, 5, 1))
        every { preferencesManager.userGoal } returns flowOf(null)
        every { predictionEngine.computeSymptomFrequency(any()) } returns emptyList()
        every { predictionEngine.computeMoodDistribution(any()) } returns emptyList()
        every { predictionEngine.computeFlowPattern(any()) } returns emptyList()
        every { predictionEngine.computeCycleStats(any(), any(), any()) } returns CycleStats(
            averageCycleLength = 28f, shortestCycle = 28, longestCycle = 28,
            cycleVariation = 0, averagePeriodDuration = 5f,
            totalCyclesTracked = 0, totalDaysLogged = 0
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = InsightsViewModel(
        dailyLogRepository, preferencesManager, predictionEngine
    )

    // ── Initial State ─────────────────────────────────────────

    @Test
    fun `initial uiState is not null`() = runTest {
        val viewModel = createViewModel()
        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `initial uiState is not loaded`() = runTest {
        val viewModel = createViewModel()
        assertFalse(viewModel.uiState.value.isLoaded)
    }

    // ── Loaded state ──────────────────────────────────────────

    @Test
    fun `uiState becomes loaded after data flows`() = runTest {
        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isLoaded)
        collector.cancel()
    }

    // ── hasData flag ──────────────────────────────────────────

    @Test
    fun `hasData is false when no logs exist`() = runTest {
        every { dailyLogRepository.getAllLogs() } returns flowOf(emptyList())

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.hasData)
        collector.cancel()
    }

    @Test
    fun `hasData is true when logs exist`() = runTest {
        val logs = listOf(DailyLog(date = LocalDate.of(2026, 5, 1)))
        every { dailyLogRepository.getAllLogs() } returns flowOf(logs)

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.hasData)
        collector.cancel()
    }

    // ── Prediction ────────────────────────────────────────────

    @Test
    fun `prediction is populated when lastPeriodDate exists`() = runTest {
        val prediction = CyclePrediction(
            nextPeriodStart = LocalDate.of(2026, 5, 29),
            nextPeriodEnd = LocalDate.of(2026, 6, 2),
            fertileWindowStart = LocalDate.of(2026, 5, 9),
            fertileWindowEnd = LocalDate.of(2026, 5, 15),
            ovulationDate = LocalDate.of(2026, 5, 14),
            currentCycleDay = 14,
            currentPhase = CyclePhase.OVULATION,
            fertilityStatus = FertilityStatus.PEAK,
            daysUntilNextPeriod = 15
        )
        every { predictionEngine.predict(any(), any(), any(), any()) } returns prediction

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.prediction)
        collector.cancel()
    }

    @Test
    fun `prediction is null when lastPeriodDate is null`() = runTest {
        every { preferencesManager.lastPeriodDate } returns flowOf(null)

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.prediction)
        collector.cancel()
    }

    // ── Symptom frequencies ───────────────────────────────────

    @Test
    fun `symptomFrequencies empty when no symptoms logged`() = runTest {
        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.symptomFrequencies.isEmpty())
        collector.cancel()
    }

    @Test
    fun `symptomFrequencies populated from predictionEngine`() = runTest {
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 1), symptoms = listOf(Symptom.CRAMPS, Symptom.HEADACHE)),
            DailyLog(date = LocalDate.of(2026, 5, 2), symptoms = listOf(Symptom.CRAMPS))
        )
        every { dailyLogRepository.getAllLogs() } returns flowOf(logs)
        every { predictionEngine.computeSymptomFrequency(logs) } returns listOf(
            SymptomFrequency(Symptom.CRAMPS, 2, 1.0f),
            SymptomFrequency(Symptom.HEADACHE, 1, 0.5f)
        )

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.symptomFrequencies.size)
        assertEquals(Symptom.CRAMPS, viewModel.uiState.value.symptomFrequencies[0].symptom)
        collector.cancel()
    }

    // ── Mood distribution ─────────────────────────────────────

    @Test
    fun `moodDistribution populated from predictionEngine`() = runTest {
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 1), mood = Mood.HAPPY),
            DailyLog(date = LocalDate.of(2026, 5, 2), mood = Mood.SAD)
        )
        every { dailyLogRepository.getAllLogs() } returns flowOf(logs)
        every { predictionEngine.computeMoodDistribution(logs) } returns listOf(
            MoodDistribution(Mood.HAPPY, 1, 0.5f),
            MoodDistribution(Mood.SAD, 1, 0.5f)
        )

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.moodDistribution.size)
        collector.cancel()
    }

    // ── Flow patterns ─────────────────────────────────────────

    @Test
    fun `flowPatterns empty when no flow data logged`() = runTest {
        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.flowPatterns.isEmpty())
        collector.cancel()
    }

    @Test
    fun `flowPatterns populated from predictionEngine`() = runTest {
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 1), flowIntensity = FlowIntensity.HEAVY),
            DailyLog(date = LocalDate.of(2026, 5, 2), flowIntensity = FlowIntensity.MEDIUM)
        )
        every { dailyLogRepository.getAllLogs() } returns flowOf(logs)
        every { predictionEngine.computeFlowPattern(logs) } returns listOf(
            FlowPattern(FlowIntensity.HEAVY, 1, 0.5f),
            FlowPattern(FlowIntensity.MEDIUM, 1, 0.5f)
        )

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.flowPatterns.size)
        collector.cancel()
    }

    // ── Cycle stats ───────────────────────────────────────────

    @Test
    fun `cycleStats populated from predictionEngine`() = runTest {
        val stats = CycleStats(
            averageCycleLength = 29f, shortestCycle = 27, longestCycle = 31,
            cycleVariation = 4, averagePeriodDuration = 5f,
            totalCyclesTracked = 3, totalDaysLogged = 15
        )
        every { predictionEngine.computeCycleStats(any(), any(), any()) } returns stats

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.cycleStats)
        assertEquals(29f, viewModel.uiState.value.cycleStats!!.averageCycleLength)
        collector.cancel()
    }

    // ── User goal ─────────────────────────────────────────────

    @Test
    fun `userGoal reflects preferences`() = runTest {
        every { preferencesManager.userGoal } returns flowOf(UserGoal.TRYING_TO_CONCEIVE)

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(UserGoal.TRYING_TO_CONCEIVE, viewModel.uiState.value.userGoal)
        collector.cancel()
    }

    @Test
    fun `userGoal is null when not set`() = runTest {
        every { preferencesManager.userGoal } returns flowOf(null)

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.userGoal)
        collector.cancel()
    }

    // ── Engine delegation ─────────────────────────────────────

    @Test
    fun `ViewModel delegates all compute calls to predictionEngine`() = runTest {
        val logs = listOf(DailyLog(date = LocalDate.of(2026, 5, 1)))
        every { dailyLogRepository.getAllLogs() } returns flowOf(logs)

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        verify { predictionEngine.computeCycleStats(logs, 28, 5) }
        verify { predictionEngine.computeSymptomFrequency(logs) }
        verify { predictionEngine.computeMoodDistribution(logs) }
        verify { predictionEngine.computeFlowPattern(logs) }
        collector.cancel()
    }
}
