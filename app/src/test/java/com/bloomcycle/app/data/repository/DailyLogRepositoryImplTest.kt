package com.bloomcycle.app.data.repository

import com.bloomcycle.app.data.local.dao.DailyLogDao
import com.bloomcycle.app.data.local.entity.DailyLogEntity
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.Symptom
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class DailyLogRepositoryImplTest {

    private lateinit var dailyLogDao: DailyLogDao
    private lateinit var repository: DailyLogRepositoryImpl

    @Before
    fun setUp() {
        dailyLogDao = mockk(relaxed = true)
        repository = DailyLogRepositoryImpl(dailyLogDao)
    }

    // ── getAllLogs() ───────────────────────────────────────────

    @Test
    fun `getAllLogs returns empty list when no logs`() = runTest {
        every { dailyLogDao.getAllLogs() } returns flowOf(emptyList())

        val result = repository.getAllLogs().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAllLogs maps entities to domain models`() = runTest {
        val entity = DailyLogEntity(
            id = 1,
            date = LocalDate.of(2026, 5, 1),
            flowIntensity = FlowIntensity.HEAVY,
            mood = Mood.HAPPY,
            symptoms = listOf(Symptom.CRAMPS),
            sexualActivity = null,
            notes = "Test note",
            weight = null,
            temperature = 36.5f,
            cervicalMucus = null,
            createdAt = 0L,
            updatedAt = 0L
        )
        every { dailyLogDao.getAllLogs() } returns flowOf(listOf(entity))

        val result = repository.getAllLogs().first()

        assertEquals(1, result.size)
        assertEquals(LocalDate.of(2026, 5, 1), result[0].date)
        assertEquals(FlowIntensity.HEAVY, result[0].flowIntensity)
        assertEquals(Mood.HAPPY, result[0].mood)
    }

    // ── getLogByDate() ────────────────────────────────────────

    @Test
    fun `getLogByDate returns null when no log exists`() = runTest {
        every { dailyLogDao.getLogByDate(any()) } returns flowOf(null)

        val result = repository.getLogByDate(LocalDate.of(2026, 5, 1)).first()

        assertNull(result)
    }

    @Test
    fun `getLogByDate returns mapped domain model when log exists`() = runTest {
        val entity = DailyLogEntity(
            id = 1,
            date = LocalDate.of(2026, 5, 1),
            flowIntensity = FlowIntensity.LIGHT,
            mood = null, symptoms = emptyList(),
            sexualActivity = null, notes = null,
            weight = null, temperature = null,
            cervicalMucus = null,
            createdAt = 0L, updatedAt = 0L
        )
        every { dailyLogDao.getLogByDate(LocalDate.of(2026, 5, 1)) } returns flowOf(entity)

        val result = repository.getLogByDate(LocalDate.of(2026, 5, 1)).first()

        assertNotNull(result)
        assertEquals(FlowIntensity.LIGHT, result!!.flowIntensity)
    }

    // ── getLogsBetweenDates() ─────────────────────────────────

    @Test
    fun `getLogsBetweenDates returns filtered logs`() = runTest {
        val entities = listOf(
            DailyLogEntity(
                id = 1, date = LocalDate.of(2026, 5, 1),
                flowIntensity = null, mood = null, symptoms = emptyList(),
                sexualActivity = null, notes = null, weight = null,
                temperature = null, cervicalMucus = null,
                createdAt = 0L, updatedAt = 0L
            ),
            DailyLogEntity(
                id = 2, date = LocalDate.of(2026, 5, 5),
                flowIntensity = null, mood = null, symptoms = emptyList(),
                sexualActivity = null, notes = null, weight = null,
                temperature = null, cervicalMucus = null,
                createdAt = 0L, updatedAt = 0L
            )
        )
        every {
            dailyLogDao.getLogsBetweenDates(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31)
            )
        } returns flowOf(entities)

        val result = repository.getLogsBetweenDates(
            LocalDate.of(2026, 5, 1),
            LocalDate.of(2026, 5, 31)
        ).first()

        assertEquals(2, result.size)
    }

    // ── insertLog() ───────────────────────────────────────────

    @Test
    fun `insertLog delegates to dao and returns id`() = runTest {
        coEvery { dailyLogDao.insertLog(any()) } returns 42L

        val log = DailyLog(date = LocalDate.of(2026, 5, 1))
        val result = repository.insertLog(log)

        assertEquals(42L, result)
        coVerify { dailyLogDao.insertLog(any()) }
    }

    // ── updateLog() ───────────────────────────────────────────

    @Test
    fun `updateLog delegates to dao`() = runTest {
        coEvery { dailyLogDao.updateLog(any()) } returns Unit

        val log = DailyLog(id = 1, date = LocalDate.of(2026, 5, 1))
        repository.updateLog(log)

        coVerify { dailyLogDao.updateLog(any()) }
    }

    // ── deleteLog() ───────────────────────────────────────────

    @Test
    fun `deleteLog delegates to dao`() = runTest {
        coEvery { dailyLogDao.deleteLog(any()) } returns Unit

        val log = DailyLog(id = 1, date = LocalDate.of(2026, 5, 1))
        repository.deleteLog(log)

        coVerify { dailyLogDao.deleteLog(any()) }
    }

    // ── upsertLog() ───────────────────────────────────────────

    @Test
    fun `upsertLog delegates to dao and returns id`() = runTest {
        coEvery { dailyLogDao.upsertLog(any()) } returns 10L

        val log = DailyLog(date = LocalDate.of(2026, 5, 1))
        val result = repository.upsertLog(log)

        assertEquals(10L, result)
        coVerify { dailyLogDao.upsertLog(any()) }
    }
}
