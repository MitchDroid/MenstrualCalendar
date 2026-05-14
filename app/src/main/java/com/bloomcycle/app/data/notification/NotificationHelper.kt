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
                context.getString(R.string.notif_channel_period),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notif_channel_period_desc)
                enableVibration(true)
            }

            val dailyChannel = NotificationChannel(
                CHANNEL_DAILY_REMINDER,
                context.getString(R.string.notif_channel_daily),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notif_channel_daily_desc)
            }

            val fertileChannel = NotificationChannel(
                CHANNEL_FERTILE_WINDOW,
                context.getString(R.string.notif_channel_fertile),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notif_channel_fertile_desc)
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
            daysUntil == 0 -> context.getString(R.string.notif_period_today_title)
            daysUntil == 1 -> context.getString(R.string.notif_period_tomorrow_title)
            else -> context.getString(R.string.notif_period_days_title, daysUntil)
        }
        val message = when {
            daysUntil == 0 -> context.getString(R.string.notif_period_today_body)
            daysUntil == 1 -> context.getString(R.string.notif_period_tomorrow_body)
            else -> context.getString(R.string.notif_period_days_body, daysUntil)
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
            title = context.getString(R.string.notif_daily_title),
            message = context.getString(R.string.notif_daily_body)
        )
    }

    fun showFertileWindowAlert(isStarting: Boolean) {
        val title = if (isStarting) {
            context.getString(R.string.notif_fertile_starting_title)
        } else {
            context.getString(R.string.notif_fertile_active_title)
        }
        val message = if (isStarting) {
            context.getString(R.string.notif_fertile_starting_body)
        } else {
            context.getString(R.string.notif_fertile_active_body)
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
