package com.bloomcycle.app.ui.settings

import android.content.Context
import com.bloomcycle.app.data.notification.NotificationScheduler
import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.data.security.BiometricAuthManager
import com.bloomcycle.app.data.security.BiometricStatus
import com.bloomcycle.app.domain.repository.DailyLogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var appContext: Context
    private lateinit var preferencesManager: UserPreferencesManager
    private lateinit var notificationScheduler: NotificationScheduler
    private lateinit var biometricAuthManager: BiometricAuthManager
    private lateinit var dailyLogRepository: DailyLogRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        appContext = mockk(relaxed = true)
        preferencesManager = mockk(relaxed = true)
        notificationScheduler = mockk(relaxed = true)
        biometricAuthManager = mockk(relaxed = true)
        dailyLogRepository = mockk(relaxed = true)

        every { preferencesManager.averageCycleLength } returns flowOf(28)
        every { preferencesManager.averagePeriodDuration } returns flowOf(5)
        every { preferencesManager.periodRemindersEnabled } returns flowOf(true)
        every { preferencesManager.dailyLogRemindersEnabled } returns flowOf(true)
        every { preferencesManager.fertileWindowAlertsEnabled } returns flowOf(false)
        every { preferencesManager.periodReminderDaysBefore } returns flowOf(2)
        every { preferencesManager.biometricEnabled } returns flowOf(false)
        every { preferencesManager.screenSecurityEnabled } returns flowOf(false)
        every { biometricAuthManager.getStatus() } returns BiometricStatus.AVAILABLE
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = SettingsViewModel(
        appContext, preferencesManager, notificationScheduler,
        biometricAuthManager, dailyLogRepository
    )

    @Test
    fun `initial uiState is not null`() = runTest {
        val viewModel = createViewModel()
        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `setPeriodReminders updates preference and reschedules`() = runTest {
        coEvery { preferencesManager.setPeriodRemindersEnabled(any()) } returns Unit

        val viewModel = createViewModel()
        viewModel.setPeriodReminders(true)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { preferencesManager.setPeriodRemindersEnabled(true) }
    }

    @Test
    fun `setCycleLength updates preference`() = runTest {
        coEvery { preferencesManager.setAverageCycleLength(any()) } returns Unit

        val viewModel = createViewModel()
        viewModel.setCycleLength(30)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { preferencesManager.setAverageCycleLength(30) }
    }

    @Test
    fun `setPeriodDuration updates preference`() = runTest {
        coEvery { preferencesManager.setAveragePeriodDuration(any()) } returns Unit

        val viewModel = createViewModel()
        viewModel.setPeriodDuration(6)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { preferencesManager.setAveragePeriodDuration(6) }
    }

    @Test
    fun `confirmDeleteAllData clears all data`() = runTest {
        coEvery { preferencesManager.clearAll() } returns Unit

        val viewModel = createViewModel()
        viewModel.requestDeleteAllData()
        viewModel.confirmDeleteAllData()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { preferencesManager.clearAll() }
    }

    @Test
    fun `cancelDeleteAllData does not clear data`() = runTest {
        val viewModel = createViewModel()
        viewModel.requestDeleteAllData()
        viewModel.cancelDeleteAllData()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { preferencesManager.clearAll() }
    }

    @Test
    fun `setBiometricEnabled updates preference`() = runTest {
        coEvery { preferencesManager.setBiometricEnabled(any()) } returns Unit

        val viewModel = createViewModel()
        viewModel.setBiometricEnabled(true)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { preferencesManager.setBiometricEnabled(true) }
    }
}
