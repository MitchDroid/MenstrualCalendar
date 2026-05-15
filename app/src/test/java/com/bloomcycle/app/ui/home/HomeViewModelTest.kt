package com.bloomcycle.app.ui.home

import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FertilityStatus
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.repository.DailyLogRepository
import io.mockk.every
import io.mockk.mockk
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
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var preferencesManager: UserPreferencesManager
    private lateinit var dailyLogRepository: DailyLogRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        preferencesManager = mockk(relaxed = true)
        dailyLogRepository = mockk(relaxed = true)

        every { preferencesManager.userName } returns flowOf("Karen")
        every { preferencesManager.lastPeriodDate } returns flowOf(LocalDate.of(2026, 5, 1))
        every { preferencesManager.averageCycleLength } returns flowOf(28)
        every { preferencesManager.averagePeriodDuration } returns flowOf(5)
        every { dailyLogRepository.getLogByDate(any()) } returns flowOf(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = HomeViewModel(preferencesManager, dailyLogRepository)

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

    // ── Null last period date ─────────────────────────────────

    @Test
    fun `uiState handles null last period date gracefully`() = runTest {
        every { preferencesManager.lastPeriodDate } returns flowOf(null)

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isLoaded)
        assertNull(viewModel.uiState.value.todayLog)
        collector.cancel()
    }

    // ── Loaded state with data ────────────────────────────────

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

    @Test
    fun `uiState reflects cycle length from preferences`() = runTest {
        every { preferencesManager.averageCycleLength } returns flowOf(32)

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(32, viewModel.uiState.value.cycleLength)
        collector.cancel()
    }

    @Test
    fun `uiState reflects period duration from preferences`() = runTest {
        every { preferencesManager.averagePeriodDuration } returns flowOf(7)

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(7, viewModel.uiState.value.periodDuration)
        collector.cancel()
    }

    // ── Cycle day computation ─────────────────────────────────

    @Test
    fun `cycleDay is 1 on the last period date`() = runTest {
        // Last period = today → cycle day 1
        every { preferencesManager.lastPeriodDate } returns flowOf(LocalDate.now())

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.cycleDay)
        collector.cancel()
    }

    @Test
    fun `daysUntilNextPeriod is positive when mid-cycle`() = runTest {
        // Set last period far enough back to be mid-cycle
        every { preferencesManager.lastPeriodDate } returns flowOf(LocalDate.now().minusDays(10))

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.daysUntilNextPeriod > 0)
        collector.cancel()
    }

    // ── Cycle phase detection ─────────────────────────────────

    @Test
    fun `menstrual phase on first day of period`() = runTest {
        every { preferencesManager.lastPeriodDate } returns flowOf(LocalDate.now())

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(CyclePhase.MENSTRUAL, viewModel.uiState.value.currentPhase)
        collector.cancel()
    }

    @Test
    fun `luteal phase toward end of cycle`() = runTest {
        // Day 22 of a 28-day cycle → luteal
        every { preferencesManager.lastPeriodDate } returns flowOf(LocalDate.now().minusDays(21))

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(CyclePhase.LUTEAL, viewModel.uiState.value.currentPhase)
        collector.cancel()
    }

    // ── Fertility status ──────────────────────────────────────

    @Test
    fun `low fertility during menstrual phase`() = runTest {
        every { preferencesManager.lastPeriodDate } returns flowOf(LocalDate.now())

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(FertilityStatus.LOW, viewModel.uiState.value.fertilityStatus)
        collector.cancel()
    }

    // ── Today's log ───────────────────────────────────────────

    @Test
    fun `todayLog is null when no log exists`() = runTest {
        every { dailyLogRepository.getLogByDate(any()) } returns flowOf(null)

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.todayLog)
        collector.cancel()
    }

    @Test
    fun `todayLog is populated when log exists`() = runTest {
        val todayLog = DailyLog(
            id = 1,
            date = LocalDate.now(),
            flowIntensity = FlowIntensity.LIGHT
        )
        every { dailyLogRepository.getLogByDate(any()) } returns flowOf(todayLog)

        val viewModel = createViewModel()
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.todayLog)
        assertEquals(FlowIntensity.LIGHT, viewModel.uiState.value.todayLog!!.flowIntensity)
        collector.cancel()
    }
}
