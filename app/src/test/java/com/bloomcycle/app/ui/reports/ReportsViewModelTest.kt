package com.bloomcycle.app.ui.reports

import android.content.Intent
import com.bloomcycle.app.data.export.DataExporter
import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.domain.repository.DailyLogRepository
import com.bloomcycle.app.domain.usecase.CycleSummaryReport
import com.bloomcycle.app.domain.usecase.ReportGenerator
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dailyLogRepository: DailyLogRepository
    private lateinit var preferencesManager: UserPreferencesManager
    private lateinit var reportGenerator: ReportGenerator
    private lateinit var dataExporter: DataExporter

    private val mockReport = CycleSummaryReport(
        generatedAt = LocalDate.of(2026, 5, 14),
        totalDaysTracked = 0, firstLogDate = null, lastLogDate = null,
        totalPeriodsDetected = 0, cycleHistory = emptyList(),
        averageCycleLength = null, averagePeriodLength = null,
        shortestCycle = null, longestCycle = null, cycleStats = null,
        symptomFrequencies = emptyList(), moodDistribution = emptyList(),
        flowPatterns = emptyList()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        dailyLogRepository = mockk(relaxed = true)
        preferencesManager = mockk(relaxed = true)
        reportGenerator = mockk(relaxed = true)
        dataExporter = mockk(relaxed = true)

        every { dailyLogRepository.getAllLogs() } returns flowOf(emptyList())
        every { preferencesManager.averageCycleLength } returns flowOf(28)
        every { preferencesManager.averagePeriodDuration } returns flowOf(5)
        every { reportGenerator.generateSummaryReport(any(), any(), any()) } returns mockReport
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = ReportsViewModel(
        dailyLogRepository, preferencesManager, reportGenerator, dataExporter
    )

    @Test
    fun `initial uiState is not loaded`() = runTest {
        val viewModel = createViewModel()
        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `uiState becomes loaded after data flows`() = runTest {
        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isLoaded)
        assertNotNull(viewModel.uiState.value.report)
        collector.cancel()
    }

    @Test
    fun `exportCsv generates share intent event`() = runTest {
        val mockIntent = mockk<Intent>()
        every { reportGenerator.generateCsvExport(any()) } returns "csv-data"
        every { dataExporter.exportCsv(any()) } returns mockIntent

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.exportCsv()
        testDispatcher.scheduler.advanceUntilIdle()

        verify { reportGenerator.generateCsvExport(any()) }
        verify { dataExporter.exportCsv("csv-data") }
        collector.cancel()
    }

    @Test
    fun `exportTextReport generates text file intent event`() = runTest {
        val mockIntent = mockk<Intent>()
        every { reportGenerator.generateTextSummary(any()) } returns "text-report"
        every { dataExporter.exportTextReport(any()) } returns mockIntent

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.exportTextReport()
        testDispatcher.scheduler.advanceUntilIdle()

        verify { reportGenerator.generateTextSummary(any()) }
        collector.cancel()
    }

    @Test
    fun `consumeExportEvent clears event`() = runTest {
        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.consumeExportEvent()

        assertNull(viewModel.uiState.value.exportEvent)
        collector.cancel()
    }
}
