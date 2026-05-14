package com.bloomcycle.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bloomcycle.app.domain.model.UserGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val LAST_PERIOD_DATE = longPreferencesKey("last_period_date")
        private val AVERAGE_CYCLE_LENGTH = intPreferencesKey("average_cycle_length")
        private val AVERAGE_PERIOD_DURATION = intPreferencesKey("average_period_duration")
        private val BIRTH_YEAR = intPreferencesKey("birth_year")
        private val USER_GOAL = stringPreferencesKey("user_goal")
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")

        // Notification preferences
        private val PERIOD_REMINDERS_ENABLED = booleanPreferencesKey("period_reminders_enabled")
        private val DAILY_LOG_REMINDERS_ENABLED = booleanPreferencesKey("daily_log_reminders_enabled")
        private val FERTILE_WINDOW_ALERTS_ENABLED = booleanPreferencesKey("fertile_window_alerts_enabled")
        private val PERIOD_REMINDER_DAYS_BEFORE = intPreferencesKey("period_reminder_days_before")

        // Privacy & security preferences
        private val SCREEN_SECURITY_ENABLED = booleanPreferencesKey("screen_security_enabled")

        const val DEFAULT_CYCLE_LENGTH = 28
        const val DEFAULT_PERIOD_DURATION = 5
        const val DEFAULT_PERIOD_REMINDER_DAYS = 2
    }

    // ── Reads ─────────────────────────────────────────────────

    val onboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETED] ?: false
    }

    val averageCycleLength: Flow<Int> = dataStore.data.map { prefs ->
        prefs[AVERAGE_CYCLE_LENGTH] ?: DEFAULT_CYCLE_LENGTH
    }

    val averagePeriodDuration: Flow<Int> = dataStore.data.map { prefs ->
        prefs[AVERAGE_PERIOD_DURATION] ?: DEFAULT_PERIOD_DURATION
    }

    val lastPeriodDate: Flow<LocalDate?> = dataStore.data.map { prefs ->
        prefs[LAST_PERIOD_DATE]?.let { LocalDate.ofEpochDay(it) }
    }

    val birthYear: Flow<Int?> = dataStore.data.map { prefs ->
        prefs[BIRTH_YEAR]
    }

    val userGoal: Flow<UserGoal?> = dataStore.data.map { prefs ->
        prefs[USER_GOAL]?.let { runCatching { UserGoal.valueOf(it) }.getOrNull() }
    }

    val biometricEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[BIOMETRIC_ENABLED] ?: false
    }

    // ── Notification Preferences (Reads) ─────────────────────

    val periodRemindersEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[PERIOD_REMINDERS_ENABLED] ?: true
    }

    val dailyLogRemindersEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[DAILY_LOG_REMINDERS_ENABLED] ?: false
    }

    val fertileWindowAlertsEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[FERTILE_WINDOW_ALERTS_ENABLED] ?: false
    }

    val periodReminderDaysBefore: Flow<Int> = dataStore.data.map { prefs ->
        prefs[PERIOD_REMINDER_DAYS_BEFORE] ?: DEFAULT_PERIOD_REMINDER_DAYS
    }

    // ── Privacy & Security Preferences (Reads) ──────────────

    val screenSecurityEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[SCREEN_SECURITY_ENABLED] ?: false
    }

    // ── Writes ────────────────────────────────────────────────

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs -> prefs[ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setAverageCycleLength(length: Int) {
        dataStore.edit { prefs -> prefs[AVERAGE_CYCLE_LENGTH] = length }
    }

    suspend fun setAveragePeriodDuration(duration: Int) {
        dataStore.edit { prefs -> prefs[AVERAGE_PERIOD_DURATION] = duration }
    }

    suspend fun setLastPeriodDate(date: LocalDate) {
        dataStore.edit { prefs -> prefs[LAST_PERIOD_DATE] = date.toEpochDay() }
    }

    suspend fun setBirthYear(year: Int) {
        dataStore.edit { prefs -> prefs[BIRTH_YEAR] = year }
    }

    suspend fun setUserGoal(goal: UserGoal) {
        dataStore.edit { prefs -> prefs[USER_GOAL] = goal.name }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[BIOMETRIC_ENABLED] = enabled }
    }

    // ── Notification Preferences (Writes) ────────────────────

    suspend fun setPeriodRemindersEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[PERIOD_REMINDERS_ENABLED] = enabled }
    }

    suspend fun setDailyLogRemindersEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[DAILY_LOG_REMINDERS_ENABLED] = enabled }
    }

    suspend fun setFertileWindowAlertsEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[FERTILE_WINDOW_ALERTS_ENABLED] = enabled }
    }

    suspend fun setPeriodReminderDaysBefore(days: Int) {
        dataStore.edit { prefs -> prefs[PERIOD_REMINDER_DAYS_BEFORE] = days }
    }

    // ── Privacy & Security Preferences (Writes) ─────────────

    suspend fun setScreenSecurityEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[SCREEN_SECURITY_ENABLED] = enabled }
    }

    /**
     * Saves all onboarding data at once.
     */
    suspend fun saveOnboardingData(
        lastPeriodDate: LocalDate,
        averageCycleLength: Int,
        averagePeriodDuration: Int,
        birthYear: Int?,
        userGoal: UserGoal
    ) {
        dataStore.edit { prefs ->
            prefs[LAST_PERIOD_DATE] = lastPeriodDate.toEpochDay()
            prefs[AVERAGE_CYCLE_LENGTH] = averageCycleLength
            prefs[AVERAGE_PERIOD_DURATION] = averagePeriodDuration
            birthYear?.let { prefs[BIRTH_YEAR] = it }
            prefs[USER_GOAL] = userGoal.name
            prefs[ONBOARDING_COMPLETED] = true
        }
    }

    /**
     * Clears all user data (for data deletion / privacy).
     */
    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
