package com.bloomcycle.app.data.notification

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.domain.model.FertilityStatus
import com.bloomcycle.app.domain.usecase.CyclePrediction
import com.bloomcycle.app.domain.usecase.CyclePredictionEngine
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class ReminderWorkerTest {

    private lateinit var context: Context
    private lateinit var workerParams: WorkerParameters
    private lateinit var preferencesManager: UserPreferencesManager
    private lateinit var predictionEngine: CyclePredictionEngine
    private lateinit var notificationHelper: NotificationHelper

    private val today = LocalDate.of(2026, 5, 14)

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        workerParams = mockk(relaxed = true)
        preferencesManager = mockk(relaxed = true)
        predictionEngine = mockk(relaxed = true)
        notificationHelper = mockk(relaxed = true)

        // Sensible defaults
        every { preferencesManager.lastPeriodDate } returns flowOf(LocalDate.of(2026, 5, 1))
        every { preferencesManager.averageCycleLength } returns flowOf(28)
        every { preferencesManager.averagePeriodDuration } returns flowOf(5)
        every { preferencesManager.periodRemindersEnabled } returns flowOf(false)
        every { preferencesManager.dailyLogRemindersEnabled } returns flowOf(false)
        every { preferencesManager.fertileWindowAlertsEnabled } returns flowOf(false)
        every { preferencesManager.periodReminderDaysBefore } returns flowOf(2)

        every { predictionEngine.predict(any(), any(), any(), any()) } returns createPrediction(
            daysUntilNextPeriod = 14,
            fertileWindowStart = today.plusDays(5),
            fertileWindowEnd = today.plusDays(10)
        )
    }

    private fun createWorker() = ReminderWorker(
        context, workerParams, preferencesManager, predictionEngine, notificationHelper
    )

    // ── Success cases ─────────────────────────────────────────

    @Test
    fun `doWork returns success`() = runTest {
        val worker = createWorker()
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork returns success when lastPeriodDate is null`() = runTest {
        every { preferencesManager.lastPeriodDate } returns flowOf(null)

        val worker = createWorker()
        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork returns success even on exception`() = runTest {
        // Force an exception via prediction engine
        every { predictionEngine.predict(any(), any(), any(), any()) } throws RuntimeException("test crash")

        val worker = createWorker()
        val result = worker.doWork()

        // Worker catches exceptions and returns success (next daily run picks up)
        assertEquals(ListenableWorker.Result.success(), result)
    }

    // ── Period reminders ──────────────────────────────────────

    @Test
    fun `doWork shows period reminder when enabled and period is within reminder days`() = runTest {
        every { preferencesManager.periodRemindersEnabled } returns flowOf(true)
        every { preferencesManager.periodReminderDaysBefore } returns flowOf(2)
        every { predictionEngine.predict(any(), any(), any(), any()) } returns createPrediction(
            daysUntilNextPeriod = 2 // Within the 2-day reminder window
        )

        val worker = createWorker()
        worker.doWork()

        verify { notificationHelper.showPeriodReminder(2) }
    }

    @Test
    fun `doWork shows period reminder when period is today`() = runTest {
        every { preferencesManager.periodRemindersEnabled } returns flowOf(true)
        every { preferencesManager.periodReminderDaysBefore } returns flowOf(2)
        every { predictionEngine.predict(any(), any(), any(), any()) } returns createPrediction(
            daysUntilNextPeriod = 0
        )

        val worker = createWorker()
        worker.doWork()

        verify { notificationHelper.showPeriodReminder(0) }
    }

    @Test
    fun `doWork does not show period reminder when disabled`() = runTest {
        every { preferencesManager.periodRemindersEnabled } returns flowOf(false)
        every { predictionEngine.predict(any(), any(), any(), any()) } returns createPrediction(
            daysUntilNextPeriod = 1
        )

        val worker = createWorker()
        worker.doWork()

        verify(exactly = 0) { notificationHelper.showPeriodReminder(any()) }
    }

    @Test
    fun `doWork does not show period reminder when period is far away`() = runTest {
        every { preferencesManager.periodRemindersEnabled } returns flowOf(true)
        every { preferencesManager.periodReminderDaysBefore } returns flowOf(2)
        every { predictionEngine.predict(any(), any(), any(), any()) } returns createPrediction(
            daysUntilNextPeriod = 10 // Outside the 2-day reminder window
        )

        val worker = createWorker()
        worker.doWork()

        verify(exactly = 0) { notificationHelper.showPeriodReminder(any()) }
    }

    // ── Daily log reminders ───────────────────────────────────

    @Test
    fun `doWork shows daily log reminder when enabled`() = runTest {
        every { preferencesManager.dailyLogRemindersEnabled } returns flowOf(true)

        val worker = createWorker()
        worker.doWork()

        verify { notificationHelper.showDailyLogReminder() }
    }

    @Test
    fun `doWork does not show daily log reminder when disabled`() = runTest {
        every { preferencesManager.dailyLogRemindersEnabled } returns flowOf(false)

        val worker = createWorker()
        worker.doWork()

        verify(exactly = 0) { notificationHelper.showDailyLogReminder() }
    }

    // ── Fertile window alerts ─────────────────────────────────

    @Test
    fun `doWork shows fertile window starting alert on first day`() = runTest {
        every { preferencesManager.fertileWindowAlertsEnabled } returns flowOf(true)
        every { predictionEngine.predict(any(), any(), any(), any()) } returns createPrediction(
            fertileWindowStart = today,
            fertileWindowEnd = today.plusDays(5)
        )

        val worker = createWorker()
        worker.doWork()

        verify { notificationHelper.showFertileWindowAlert(isStarting = true) }
    }

    @Test
    fun `doWork shows fertile window mid-alert on day 4 of window`() = runTest {
        every { preferencesManager.fertileWindowAlertsEnabled } returns flowOf(true)
        val fertileStart = today.minusDays(3) // 3 days ago → midFertile = fertileStart + 3 = today
        every { predictionEngine.predict(any(), any(), any(), any()) } returns createPrediction(
            fertileWindowStart = fertileStart,
            fertileWindowEnd = fertileStart.plusDays(6)
        )

        val worker = createWorker()
        worker.doWork()

        verify { notificationHelper.showFertileWindowAlert(isStarting = false) }
    }

    @Test
    fun `doWork does not show fertile alert when disabled`() = runTest {
        every { preferencesManager.fertileWindowAlertsEnabled } returns flowOf(false)
        every { predictionEngine.predict(any(), any(), any(), any()) } returns createPrediction(
            fertileWindowStart = today,
            fertileWindowEnd = today.plusDays(5)
        )

        val worker = createWorker()
        worker.doWork()

        verify(exactly = 0) { notificationHelper.showFertileWindowAlert(any()) }
    }

    @Test
    fun `doWork does not show fertile alert when not in fertile window`() = runTest {
        every { preferencesManager.fertileWindowAlertsEnabled } returns flowOf(true)
        every { predictionEngine.predict(any(), any(), any(), any()) } returns createPrediction(
            fertileWindowStart = today.plusDays(10),
            fertileWindowEnd = today.plusDays(15)
        )

        val worker = createWorker()
        worker.doWork()

        verify(exactly = 0) { notificationHelper.showFertileWindowAlert(any()) }
    }

    // ── All notifications together ────────────────────────────

    @Test
    fun `doWork can fire all three notification types simultaneously`() = runTest {
        every { preferencesManager.periodRemindersEnabled } returns flowOf(true)
        every { preferencesManager.dailyLogRemindersEnabled } returns flowOf(true)
        every { preferencesManager.fertileWindowAlertsEnabled } returns flowOf(true)
        every { preferencesManager.periodReminderDaysBefore } returns flowOf(2)
        every { predictionEngine.predict(any(), any(), any(), any()) } returns createPrediction(
            daysUntilNextPeriod = 1,
            fertileWindowStart = today,
            fertileWindowEnd = today.plusDays(5)
        )

        val worker = createWorker()
        worker.doWork()

        verify { notificationHelper.showPeriodReminder(1) }
        verify { notificationHelper.showDailyLogReminder() }
        verify { notificationHelper.showFertileWindowAlert(isStarting = true) }
    }

    @Test
    fun `doWork skips all notifications when all toggles are off`() = runTest {
        every { preferencesManager.periodRemindersEnabled } returns flowOf(false)
        every { preferencesManager.dailyLogRemindersEnabled } returns flowOf(false)
        every { preferencesManager.fertileWindowAlertsEnabled } returns flowOf(false)

        val worker = createWorker()
        worker.doWork()

        verify(exactly = 0) { notificationHelper.showPeriodReminder(any()) }
        verify(exactly = 0) { notificationHelper.showDailyLogReminder() }
        verify(exactly = 0) { notificationHelper.showFertileWindowAlert(any()) }
    }

    // ── Companion constants ───────────────────────────────────

    @Test
    fun `work name constants are non-empty`() {
        assertEquals("bloomcycle_daily_reminder", ReminderWorker.WORK_NAME_DAILY)
        assertEquals("bloomcycle_period_check", ReminderWorker.WORK_NAME_PERIOD)
    }

    // ── Helpers ───────────────────────────────────────────────

    private fun createPrediction(
        daysUntilNextPeriod: Int = 14,
        fertileWindowStart: LocalDate = today.plusDays(5),
        fertileWindowEnd: LocalDate = today.plusDays(10)
    ) = CyclePrediction(
        nextPeriodStart = today.plusDays(daysUntilNextPeriod.toLong()),
        nextPeriodEnd = today.plusDays(daysUntilNextPeriod.toLong() + 5),
        fertileWindowStart = fertileWindowStart,
        fertileWindowEnd = fertileWindowEnd,
        ovulationDate = fertileWindowEnd.minusDays(1),
        currentCycleDay = 14,
        currentPhase = CyclePhase.FOLLICULAR,
        fertilityStatus = FertilityStatus.LOW,
        daysUntilNextPeriod = daysUntilNextPeriod
    )
}
