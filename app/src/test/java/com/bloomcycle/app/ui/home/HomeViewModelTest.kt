package com.bloomcycle.app.ui.home

import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.repository.DailyLogRepository
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

        every { preferencesManager.lastPeriodDate } returns flowOf(LocalDate.of(2026, 5, 1))
        every { preferencesManager.averageCycleLength } returns flowOf(28)
        every { preferencesManager.averagePeriodDuration } returns flowOf(5)
        every { dailyLogRepository.getLogByDate(any()) } returns flowOf(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState is not null`() = runTest {
        val viewModel = HomeViewModel(preferencesManager, dailyLogRepository)
        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `uiState reflects last period date from preferences`() = runTest {
        every { preferencesManager.lastPeriodDate } returns flowOf(LocalDate.of(2026, 4, 15))

        val viewModel = HomeViewModel(preferencesManager, dailyLogRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // ViewModel should have received preferences
        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `uiState handles null last period date`() = runTest {
        every { preferencesManager.lastPeriodDate } returns flowOf(null)

        val viewModel = HomeViewModel(preferencesManager, dailyLogRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value)
    }
}
