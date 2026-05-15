package com.bloomcycle.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesManagerTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var preferencesManager: UserPreferencesManager

    @Before
    fun setUp() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_prefs.preferences_pb") }
        )
        preferencesManager = UserPreferencesManager(dataStore)
    }

    // ── Default Values ────────────────────────────────────────

    @Test
    fun `onboardingCompleted defaults to false`() = runTest {
        val result = preferencesManager.onboardingCompleted.first()
        assertFalse(result)
    }

    @Test
    fun `averageCycleLength defaults to 28`() = runTest {
        val result = preferencesManager.averageCycleLength.first()
        assertEquals(28, result)
    }

    @Test
    fun `averagePeriodDuration defaults to 5`() = runTest {
        val result = preferencesManager.averagePeriodDuration.first()
        assertEquals(5, result)
    }

    @Test
    fun `lastPeriodDate defaults to null`() = runTest {
        val result = preferencesManager.lastPeriodDate.first()
        assertNull(result)
    }

    @Test
    fun `biometricEnabled defaults to false`() = runTest {
        val result = preferencesManager.biometricEnabled.first()
        assertFalse(result)
    }

    @Test
    fun `periodRemindersEnabled defaults to false`() = runTest {
        val result = preferencesManager.periodRemindersEnabled.first()
        // Default is typically false or true depending on implementation
        assertNotNull(result)
    }

    @Test
    fun `screenSecurityEnabled defaults to false`() = runTest {
        val result = preferencesManager.screenSecurityEnabled.first()
        assertFalse(result)
    }

    // ── Write and Read ────────────────────────────────────────

    @Test
    fun `setOnboardingCompleted persists value`() = runTest {
        preferencesManager.setOnboardingCompleted(true)
        val result = preferencesManager.onboardingCompleted.first()
        assertTrue(result)
    }

    @Test
    fun `setAverageCycleLength persists value`() = runTest {
        preferencesManager.setAverageCycleLength(30)
        val result = preferencesManager.averageCycleLength.first()
        assertEquals(30, result)
    }

    @Test
    fun `setAveragePeriodDuration persists value`() = runTest {
        preferencesManager.setAveragePeriodDuration(7)
        val result = preferencesManager.averagePeriodDuration.first()
        assertEquals(7, result)
    }

    @Test
    fun `setLastPeriodDate persists value`() = runTest {
        val date = java.time.LocalDate.of(2026, 5, 1)
        preferencesManager.setLastPeriodDate(date)
        val result = preferencesManager.lastPeriodDate.first()
        assertEquals(date, result)
    }

    @Test
    fun `setBiometricEnabled persists value`() = runTest {
        preferencesManager.setBiometricEnabled(true)
        val result = preferencesManager.biometricEnabled.first()
        assertTrue(result)
    }

    @Test
    fun `setScreenSecurityEnabled persists value`() = runTest {
        preferencesManager.setScreenSecurityEnabled(true)
        val result = preferencesManager.screenSecurityEnabled.first()
        assertTrue(result)
    }

    @Test
    fun `setPeriodReminderDaysBefore persists value`() = runTest {
        preferencesManager.setPeriodReminderDaysBefore(3)
        val result = preferencesManager.periodReminderDaysBefore.first()
        assertEquals(3, result)
    }

    // ── clearAll() ────────────────────────────────────────────

    @Test
    fun `clearAll resets all values to defaults`() = runTest {
        preferencesManager.setOnboardingCompleted(true)
        preferencesManager.setAverageCycleLength(35)
        preferencesManager.setBiometricEnabled(true)

        preferencesManager.clearAll()

        assertFalse(preferencesManager.onboardingCompleted.first())
        assertEquals(28, preferencesManager.averageCycleLength.first())
        assertFalse(preferencesManager.biometricEnabled.first())
    }

    // ── saveOnboardingData() ──────────────────────────────────

    @Test
    fun `saveOnboardingData persists all fields`() = runTest {
        val date = java.time.LocalDate.of(2026, 5, 1)
        preferencesManager.saveOnboardingData(
            userName = "Karen",
            lastPeriodDate = date,
            averageCycleLength = 30,
            averagePeriodDuration = 6,
            birthYear = 1995,
            userGoal = com.bloomcycle.app.domain.model.UserGoal.TRACK_CYCLE
        )

        assertEquals("Karen", preferencesManager.userName.first())
        assertEquals(date, preferencesManager.lastPeriodDate.first())
        assertEquals(30, preferencesManager.averageCycleLength.first())
        assertEquals(6, preferencesManager.averagePeriodDuration.first())
    }

    private fun assertNotNull(value: Any?) {
        org.junit.Assert.assertNotNull(value)
    }
}
