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
import com.bloomcycle.app.data.security.BiometricAuthManager
import com.bloomcycle.app.data.security.BiometricStatus
import com.bloomcycle.app.domain.repository.DailyLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    // Privacy & security
    val biometricEnabled: Boolean = false,
    val biometricAvailable: Boolean = false,
    val screenSecurityEnabled: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val dataDeleted: Boolean = false,
    val isLoaded: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val preferencesManager: UserPreferencesManager,
    private val notificationScheduler: NotificationScheduler,
    private val biometricAuthManager: BiometricAuthManager,
    private val dailyLogRepository: DailyLogRepository
) : ViewModel() {

    private val _showDeleteConfirmation = MutableStateFlow(false)
    private val _dataDeleted = MutableStateFlow(false)

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesManager.periodRemindersEnabled,
        preferencesManager.dailyLogRemindersEnabled,
        preferencesManager.fertileWindowAlertsEnabled,
        preferencesManager.periodReminderDaysBefore,
        combine(
            preferencesManager.averageCycleLength,
            preferencesManager.averagePeriodDuration,
            preferencesManager.biometricEnabled,
            preferencesManager.screenSecurityEnabled,
            _showDeleteConfirmation,
            _dataDeleted
        ) { values ->
            // Array destructure for 6-param combine
            PrivacyBundle(
                cycleLength = values[0] as Int,
                periodDuration = values[1] as Int,
                biometricEnabled = values[2] as Boolean,
                screenSecurityEnabled = values[3] as Boolean,
                showDeleteConfirm = values[4] as Boolean,
                dataDeleted = values[5] as Boolean
            )
        }
    ) { periodReminders, dailyReminders, fertileAlerts, reminderDays, privacy ->
        SettingsUiState(
            periodRemindersEnabled = periodReminders,
            dailyLogRemindersEnabled = dailyReminders,
            fertileWindowAlertsEnabled = fertileAlerts,
            periodReminderDaysBefore = reminderDays,
            averageCycleLength = privacy.cycleLength,
            averagePeriodDuration = privacy.periodDuration,
            notificationPermissionGranted = checkNotificationPermission(),
            biometricEnabled = privacy.biometricEnabled,
            biometricAvailable = biometricAuthManager.getStatus() == BiometricStatus.AVAILABLE,
            screenSecurityEnabled = privacy.screenSecurityEnabled,
            showDeleteConfirmation = privacy.showDeleteConfirm,
            dataDeleted = privacy.dataDeleted,
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

    // ── Privacy & Security ──────────────────────────────────────

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setBiometricEnabled(enabled)
        }
    }

    fun setScreenSecurity(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setScreenSecurityEnabled(enabled)
        }
    }

    fun requestDeleteAllData() {
        _showDeleteConfirmation.update { true }
    }

    fun cancelDeleteAllData() {
        _showDeleteConfirmation.update { false }
    }

    fun confirmDeleteAllData() {
        viewModelScope.launch {
            // Cancel notifications first
            notificationScheduler.cancelAllReminders()

            // Clear all preferences (resets to defaults)
            preferencesManager.clearAll()

            _showDeleteConfirmation.update { false }
            _dataDeleted.update { true }
        }
    }

    fun consumeDataDeletedEvent() {
        _dataDeleted.update { false }
    }

    // ── Helpers ──────────────────────────────────────────────────

    private fun rescheduleIfNeeded() {
        notificationScheduler.rescheduleReminders()
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun refreshPermissionState() {
        viewModelScope.launch {
            val current = preferencesManager.periodRemindersEnabled
                .stateIn(viewModelScope).value
            preferencesManager.setPeriodRemindersEnabled(current)
        }
    }
}

/** Internal helper to bundle 6 flows into one combine result. */
private data class PrivacyBundle(
    val cycleLength: Int,
    val periodDuration: Int,
    val biometricEnabled: Boolean,
    val screenSecurityEnabled: Boolean,
    val showDeleteConfirm: Boolean,
    val dataDeleted: Boolean
)
