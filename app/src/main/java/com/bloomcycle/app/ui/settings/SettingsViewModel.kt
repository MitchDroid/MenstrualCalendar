package com.bloomcycle.app.ui.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bloomcycle.app.data.notification.NotificationScheduler
import com.bloomcycle.app.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val periodRemindersEnabled: Boolean = true,
    val dailyLogRemindersEnabled: Boolean = false,
    val fertileWindowAlertsEnabled: Boolean = false,
    val periodReminderDaysBefore: Int = UserPreferencesManager.DEFAULT_PERIOD_REMINDER_DAYS,
    val averageCycleLength: Int = UserPreferencesManager.DEFAULT_CYCLE_LENGTH,
    val averagePeriodDuration: Int = UserPreferencesManager.DEFAULT_PERIOD_DURATION,
    val notificationPermissionGranted: Boolean = false,
    val isLoaded: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val preferencesManager: UserPreferencesManager,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesManager.periodRemindersEnabled,
        preferencesManager.dailyLogRemindersEnabled,
        preferencesManager.fertileWindowAlertsEnabled,
        preferencesManager.periodReminderDaysBefore,
        combine(
            preferencesManager.averageCycleLength,
            preferencesManager.averagePeriodDuration
        ) { a, b -> a to b }
    ) { periodReminders, dailyReminders, fertileAlerts, reminderDays, (cycleLength, periodDuration) ->
        SettingsUiState(
            periodRemindersEnabled = periodReminders,
            dailyLogRemindersEnabled = dailyReminders,
            fertileWindowAlertsEnabled = fertileAlerts,
            periodReminderDaysBefore = reminderDays,
            averageCycleLength = cycleLength,
            averagePeriodDuration = periodDuration,
            notificationPermissionGranted = checkNotificationPermission(),
            isLoaded = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    // ── Notification Toggles ────────────────────────────────────

    fun setPeriodReminders(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setPeriodRemindersEnabled(enabled)
            rescheduleIfNeeded()
        }
    }

    fun setDailyLogReminders(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDailyLogRemindersEnabled(enabled)
            rescheduleIfNeeded()
        }
    }

    fun setFertileWindowAlerts(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setFertileWindowAlertsEnabled(enabled)
            rescheduleIfNeeded()
        }
    }

    fun setPeriodReminderDays(days: Int) {
        viewModelScope.launch {
            preferencesManager.setPeriodReminderDaysBefore(days.coerceIn(1, 7))
            rescheduleIfNeeded()
        }
    }

    // ── Cycle Settings ──────────────────────────────────────────

    fun setCycleLength(length: Int) {
        viewModelScope.launch {
            preferencesManager.setAverageCycleLength(length.coerceIn(18, 45))
        }
    }

    fun setPeriodDuration(duration: Int) {
        viewModelScope.launch {
            preferencesManager.setAveragePeriodDuration(duration.coerceIn(1, 14))
        }
    }

    // ── Helpers ──────────────────────────────────────────────────

    private fun rescheduleIfNeeded() {
        // WorkManager handles the scheduling — we just trigger a reschedule
        // so new preference values are picked up on next worker execution
        notificationScheduler.rescheduleReminders()
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Pre-Android 13 doesn't require runtime permission
        }
    }

    fun refreshPermissionState() {
        // Force a recomposition by rescheduling — the permission check
        // happens in the combine flow
        viewModelScope.launch {
            // Touch a pref to trigger reflow (no-op write)
            val current = preferencesManager.periodRemindersEnabled
                .stateIn(viewModelScope).value
            preferencesManager.setPeriodRemindersEnabled(current)
        }
    }
}
