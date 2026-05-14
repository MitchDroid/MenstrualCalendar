package com.bloomcycle.app.ui.calendar

import com.bloomcycle.app.data.preferences.UserPreferencesManager
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
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dailyLogRepository: DailyLogRepository
    private lateinit var preferencesManager: UserPreferencesManager

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        dailyLogRepository = mockk(relaxed = true)
        preferencesManager = mockk(relaxed = true)

        every { dailyLogRepository.getAllLogs() } returns flowOf(emptyList())
        every { preferencesManager.averageCycleLength } returns flowOf(28)
        every { preferencesManager.averagePeriodDuration } returns flowOf(5)
        every { preferencesManager.lastPeriodDate } returns flowOf(LocalDate.of(2026, 5, 1))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState is not null`() = runTest {
        val viewModel = CalendarViewModel(dailyLogRepository, preferencesManager)
        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `selectDate updates selected date in state`() = runTest {
        val viewModel = CalendarViewModel(dailyLogRepository, preferencesManager)
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        val targetDate = LocalDate.of(2026, 5, 15)
        viewModel.selectDate(targetDate)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(targetDate, viewModel.uiState.value.selectedDate)
        collector.cancel()
    }

    @Test
    fun `navigateMonth forward changes displayed month`() = runTest {
        val viewModel = CalendarViewModel(dailyLogRepository, preferencesManager)
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val initialMonth = viewModel.uiState.value.currentMonth

        viewModel.navigateMonth(1)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(initialMonth.plusMonths(1), viewModel.uiState.value.currentMonth)
        collector.cancel()
    }

    @Test
    fun `navigateMonth backward changes displayed month`() = runTest {
        val viewModel = CalendarViewModel(dailyLogRepository, preferencesManager)
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val initialMonth = viewModel.uiState.value.currentMonth

        viewModel.navigateMonth(-1)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(initialMonth.minusMonths(1), viewModel.uiState.value.currentMonth)
        collector.cancel()
    }

    @Test
    fun `goToToday resets to current month`() = runTest {
        val viewModel = CalendarViewModel(dailyLogRepository, preferencesManager)
        val collector = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.navigateMonth(3)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.goToToday()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(YearMonth.now(), viewModel.uiState.value.currentMonth)
        collector.cancel()
    }
}
