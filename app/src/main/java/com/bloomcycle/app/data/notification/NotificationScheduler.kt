package com.bloomcycle.app.data.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Schedules and cancels periodic WorkManager tasks for notifications.
 */
@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Schedules the daily reminder check.
     * Runs every 24 hours — WorkManager handles battery optimization and doze mode.
     *
     * Uses [ExistingPeriodicWorkPolicy.KEEP] so re-scheduling doesn't reset
     * the existing timer.
     */
    fun scheduleDailyReminders() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        // Calculate initial delay to target ~9:00 AM
        val initialDelay = calculateDelayToTargetHour(targetHour = 9)

        val dailyWork = PeriodicWorkRequestBuilder<ReminderWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag("bloomcycle_notifications")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            ReminderWorker.WORK_NAME_DAILY,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWork
        )
    }

    /**
     * Cancels all scheduled notification work.
     */
    fun cancelAllReminders() {
        WorkManager.getInstance(context).cancelUniqueWork(ReminderWorker.WORK_NAME_DAILY)
    }

    /**
     * Reschedules reminders — useful when user changes preferences.
     * Uses CANCEL_AND_REENQUEUE to reset the timer with new settings.
     */
    fun rescheduleReminders() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val initialDelay = calculateDelayToTargetHour(targetHour = 9)

        val dailyWork = PeriodicWorkRequestBuilder<ReminderWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag("bloomcycle_notifications")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            ReminderWorker.WORK_NAME_DAILY,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            dailyWork
        )
    }

    /**
     * Calculates milliseconds until the next occurrence of [targetHour]:00.
     * If it's already past the target hour today, schedules for tomorrow.
     */
    private fun calculateDelayToTargetHour(targetHour: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        return target.timeInMillis - now.timeInMillis
    }
}
