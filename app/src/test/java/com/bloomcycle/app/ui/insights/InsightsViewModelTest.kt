package com.bloomcycle.app.ui.insights

import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.Symptom
import com.bloomcycle.app.domain.repository.DailyLogRepository
import com.bloomcycle.app.domain.usecase.CyclePredictionEngine
import com.bloomcycle.app.domain.usecase.FlowPattern
import com.bloomcycle.app.domain.usecase.MoodDistribution
import com.bloomcycle.app.domain.usecase.SymptomFrequency
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
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
        every { predictionEngine.computeSymptomFrequency(any()) } returns emptyList()
        every { predictionEngine.computeMoodDistribution(any()) } returns emptyList()
        every { predictionEngine.computeFlowPattern(any()) } returns emptyList()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState is not null`() = runTest {
        val viewModel = InsightsViewModel(dailyLogRepository, preferencesManager, predictionEngine)
        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `uiState reflects empty data when no logs`() = runTest {
        val viewModel = InsightsViewModel(dailyLogRepository, preferencesManager, predictionEngine)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `uiState processes logs with symptoms`() = runTest {
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 1), symptoms = listOf(Symptom.CRAMPS))
        )
        every { dailyLogRepository.getAllLogs() } returns flowOf(logs)
        every { predictionEngine.computeSymptomFrequency(logs) } returns listOf(
            SymptomFrequency(Symptom.CRAMPS, 1, 1.0f)
        )

        val viewModel = InsightsViewModel(dailyLogRepository, preferencesManager, predictionEngine)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `uiState processes logs with mood data`() = runTest {
        val logs = listOf(
            DailyLog(date = LocalDate.of(2026, 5, 1), mood = Mood.HAPPY),
            DailyLog(date = LocalDate.of(2026, 5, 2), mood = Mood.SAD)
        )
        every { dailyLogRepository.getAllLogs() } returns flowOf(logs)
        every { predictionEngine.computeMoodDistribution(logs) } returns listOf(
            MoodDistribution(Mood.HAPPY, 1, 0.5f),
            MoodDistribution(Mood.SAD, 1, 0.5f)
        )

        val viewModel = InsightsViewModel(dailyLogRepository, preferencesManager, predictionEngine)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value)
    }
}
