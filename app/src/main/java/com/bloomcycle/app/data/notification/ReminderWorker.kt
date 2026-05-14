package com.bloomcycle.app.data.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.domain.usecase.CyclePredictionEngine
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate

/**
 * WorkManager Worker that checks cycle predictions and fires appropriate
 * notifications based on user preferences.
 *
 * Runs daily — scheduled by [NotificationScheduler].
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val preferencesManager: UserPreferencesManager,
    private val predictionEngine: CyclePredictionEngine,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME_DAILY = "bloomcycle_daily_reminder"
        const val WORK_NAME_PERIOD = "bloomcycle_period_check"
    }

    override suspend fun doWork(): Result {
        return try {
            val today = LocalDate.now()

            // Read user preferences
            val lastPeriodDate = preferencesManager.lastPeriodDate.first() ?: return Result.success()
            val cycleLength = preferencesManager.averageCycleLength.first()
            val periodDuration = preferencesManager.averagePeriodDuration.first()

            // Read notification toggles
            val periodRemindersEnabled = preferencesManager.periodRemindersEnabled.first()
            val dailyRemindersEnabled = preferencesManager.dailyLogRemindersEnabled.first()
            val fertileAlertsEnabled = preferencesManager.fertileWindowAlertsEnabled.first()

            // Compute prediction
            val prediction = predictionEngine.predict(lastPeriodDate, cycleLength, periodDuration, today)

            // ── Period Reminder ──────────────────────────────
            if (periodRemindersEnabled) {
                val daysUntil = prediction.daysUntilNextPeriod
                val reminderDays = preferencesManager.periodReminderDaysBefore.first()

                if (daysUntil in 0..reminderDays) {
                    notificationHelper.showPeriodReminder(daysUntil)
                }
            }

            // ── Daily Log Reminder ──────────────────────────
            if (dailyRemindersEnabled) {
                notificationHelper.showDailyLogReminder()
            }

            // ── Fertile Window Alert ────────────────────────
            if (fertileAlertsEnabled) {
                val fertileStart = prediction.fertileWindowStart
                if (today == fertileStart) {
                    notificationHelper.showFertileWindowAlert(isStarting = true)
                } else if (today.isAfter(fertileStart) && !today.isAfter(prediction.fertileWindowEnd)) {
                    // Already in fertile window — don't spam every day,
                    // only on day 1 (handled above) and mid-window
                    val midFertile = fertileStart.plusDays(3)
                    if (today == midFertile) {
                        notificationHelper.showFertileWindowAlert(isStarting = false)
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            // Don't retry on failure — next daily run will pick it up
            Result.success()
        }
    }
}
