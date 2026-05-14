package com.bloomcycle.app.data.repository

import com.bloomcycle.app.data.local.dao.CycleDao
import com.bloomcycle.app.data.local.entity.CycleEntity
import com.bloomcycle.app.domain.model.Cycle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class CycleRepositoryImplTest {

    private lateinit var cycleDao: CycleDao
    private lateinit var repository: CycleRepositoryImpl

    @Before
    fun setUp() {
        cycleDao = mockk(relaxed = true)
        repository = CycleRepositoryImpl(cycleDao)
    }

    // ── getAllCycles() ─────────────────────────────────────────

    @Test
    fun `getAllCycles returns empty list when no cycles`() = runTest {
        every { cycleDao.getAllCycles() } returns flowOf(emptyList())

        val result = repository.getAllCycles().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAllCycles maps entities to domain models`() = runTest {
        val entity = CycleEntity(
            id = 1,
            startDate = LocalDate.of(2026, 4, 1),
            endDate = LocalDate.of(2026, 4, 28),
            cycleLength = 28,
            periodLength = 5
        )
        every { cycleDao.getAllCycles() } returns flowOf(listOf(entity))

        val result = repository.getAllCycles().first()

        assertEquals(1, result.size)
        assertEquals(LocalDate.of(2026, 4, 1), result[0].startDate)
    }

    // ── getCurrentCycle() ─────────────────────────────────────

    @Test
    fun `getCurrentCycle returns null when no cycles`() = runTest {
        every { cycleDao.getMostRecentCycle() } returns flowOf(null)

        val result = repository.getCurrentCycle().first()

        assertNull(result)
    }

    // ── insertCycle() ─────────────────────────────────────────

    @Test
    fun `insertCycle delegates to dao and returns id`() = runTest {
        coEvery { cycleDao.insertCycle(any()) } returns 5L

        val cycle = Cycle(
            id = 0,
            startDate = LocalDate.of(2026, 5, 1),
            endDate = LocalDate.of(2026, 5, 28),
            cycleLength = 28,
            periodLength = 5
        )
        val result = repository.insertCycle(cycle)

        assertEquals(5L, result)
        coVerify { cycleDao.insertCycle(any()) }
    }

    // ── deleteCycle() ─────────────────────────────────────────

    @Test
    fun `deleteCycle delegates to dao`() = runTest {
        coEvery { cycleDao.deleteCycle(any()) } returns Unit

        val cycle = Cycle(
            id = 1,
            startDate = LocalDate.of(2026, 5, 1),
            endDate = LocalDate.of(2026, 5, 28),
            cycleLength = 28,
            periodLength = 5
        )
        repository.deleteCycle(cycle)

        coVerify { cycleDao.deleteCycle(any()) }
    }
}
