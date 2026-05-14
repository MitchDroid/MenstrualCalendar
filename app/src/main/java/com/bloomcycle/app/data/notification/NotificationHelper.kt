package com.bloomcycle.app.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bloomcycle.app.MainActivity
import com.bloomcycle.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        // Channel IDs
        const val CHANNEL_PERIOD_REMINDER = "period_reminders"
        const val CHANNEL_DAILY_REMINDER = "daily_reminders"
        const val CHANNEL_FERTILE_WINDOW = "fertile_window"

        // Notification IDs
        const val NOTIFICATION_PERIOD_REMINDER = 1001
        const val NOTIFICATION_DAILY_REMINDER = 1002
        const val NOTIFICATION_FERTILE_WINDOW = 1003
    }

    /**
     * Creates all notification channels. Safe to call multiple times —
     * creating an existing channel is a no-op.
     */
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)

            val periodChannel = NotificationChannel(
                CHANNEL_PERIOD_REMINDER,
                "Period Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders before your next predicted period"
                enableVibration(true)
            }

            val dailyChannel = NotificationChannel(
                CHANNEL_DAILY_REMINDER,
                "Daily Log Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminders to log your symptoms and mood"
            }

            val fertileChannel = NotificationChannel(
                CHANNEL_FERTILE_WINDOW,
                "Fertile Window Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when your fertile window begins"
                enableVibration(true)
            }

            manager.createNotificationChannels(
                listOf(periodChannel, dailyChannel, fertileChannel)
            )
        }
    }

    // ── Notification Builders ────────────────────────────────────

    fun showPeriodReminder(daysUntil: Int) {
        val title = when {
            daysUntil == 0 -> "\uD83C\uDF3A Period starting today"
            daysUntil == 1 -> "\uD83C\uDF3A Period expected tomorrow"
            else -> "\uD83C\uDF3A Period in $daysUntil days"
        }
        val message = when {
            daysUntil == 0 -> "Your period is predicted to start today. Be prepared and take care of yourself!"
            daysUntil == 1 -> "Your period is likely starting tomorrow. Stock up on supplies and plan for comfort."
            else -> "Your next period is predicted in $daysUntil days. Time to prepare!"
        }

        showNotification(
            channelId = CHANNEL_PERIOD_REMINDER,
            notificationId = NOTIFICATION_PERIOD_REMINDER,
            title = title,
            message = message
        )
    }

    fun showDailyLogReminder() {
        showNotification(
            channelId = CHANNEL_DAILY_REMINDER,
            notificationId = NOTIFICATION_DAILY_REMINDER,
            title = "\uD83D\uDCDD Time to log your day",
            message = "Take a moment to record your symptoms, mood, and flow. Consistent tracking unlocks better insights!"
        )
    }

    fun showFertileWindowAlert(isStarting: Boolean) {
        val title = if (isStarting) {
            "\u2728 Fertile window starting"
        } else {
            "\u2728 You're in your fertile window"
        }
        val message = if (isStarting) {
            "Your fertile window is beginning. Check the Insights tab for details."
        } else {
            "You're currently in your fertile window. Open BloomCycle for more information."
        }

        showNotification(
            channelId = CHANNEL_FERTILE_WINDOW,
            notificationId = NOTIFICATION_FERTILE_WINDOW,
            title = title,
            message = message
        )
    }

    // ── Private ─────────────────────────────────────────────────

    private fun showNotification(
        channelId: String,
        notificationId: Int,
        title: String,
        message: String
    ) {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(notificationId, notification)
    }
}
