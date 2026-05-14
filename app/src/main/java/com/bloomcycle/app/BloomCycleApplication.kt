package com.bloomcycle.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.bloomcycle.app.data.notification.NotificationHelper
import com.bloomcycle.app.data.notification.NotificationScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BloomCycleApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var notificationScheduler: NotificationScheduler

    override fun onCreate() {
        super.onCreate()

        // Create notification channels (safe to call multiple times)
        notificationHelper.createNotificationChannels()

        // Schedule daily reminder check via WorkManager
        notificationScheduler.scheduleDailyReminders()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
