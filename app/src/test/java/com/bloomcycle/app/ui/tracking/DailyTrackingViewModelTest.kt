package com.bloomcycle.app.ui.tracking

import androidx.lifecycle.SavedStateHandle
import com.bloomcycle.app.domain.model.CervicalMucus
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.SexualActivity
import com.bloomcycle.app.domain.model.Symptom
import com.bloomcycle.app.domain.repository.DailyLogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class DailyTrackingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var dailyLogRepository: DailyLogRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf("date" to "2026-05-14"))
        dailyLogRepository = mockk(relaxed = true)

        every { dailyLogRepository.getLogByDate(any()) } returns flowOf(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = DailyTrackingViewModel(savedStateHandle, dailyLogRepository)

    @Test
    fun `initial uiState is not null`() = runTest {
        val viewModel = createViewModel()
        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `setFlowIntensity updates state`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setFlowIntensity(FlowIntensity.HEAVY)

        assertEquals(FlowIntensity.HEAVY, viewModel.uiState.value.flowIntensity)
    }

    @Test
    fun `setMood updates state`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setMood(Mood.HAPPY)

        assertEquals(Mood.HAPPY, viewModel.uiState.value.mood)
    }

    @Test
    fun `toggleSymptom adds symptom to state`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleSymptom(Symptom.CRAMPS)

        assertTrue(viewModel.uiState.value.symptoms.contains(Symptom.CRAMPS))
    }

    @Test
    fun `toggleSymptom twice removes symptom from state`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleSymptom(Symptom.CRAMPS)
        viewModel.toggleSymptom(Symptom.CRAMPS)

        assertTrue(!viewModel.uiState.value.symptoms.contains(Symptom.CRAMPS))
    }

    @Test
    fun `setSexualActivity updates state`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setSexualActivity(SexualActivity.PROTECTED)

        assertEquals(SexualActivity.PROTECTED, viewModel.uiState.value.sexualActivity)
    }

    @Test
    fun `setCervicalMucus updates state`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setCervicalMucus(CervicalMucus.EGG_WHITE)

        assertEquals(CervicalMucus.EGG_WHITE, viewModel.uiState.value.cervicalMucus)
    }

    @Test
    fun `setNotes updates state`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setNotes("Feeling better today")

        assertEquals("Feeling better today", viewModel.uiState.value.notes)
    }

    @Test
    fun `saveLog calls repository upsert`() = runTest {
        coEvery { dailyLogRepository.upsertLog(any()) } returns 1L

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setFlowIntensity(FlowIntensity.MEDIUM)
        viewModel.saveLog()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { dailyLogRepository.upsertLog(any()) }
    }

    @Test
    fun `deleteLog calls repository delete`() = runTest {
        val existingLog = DailyLog(id = 1, date = LocalDate.of(2026, 5, 14), flowIntensity = FlowIntensity.LIGHT)
        every { dailyLogRepository.getLogByDate(any()) } returns flowOf(existingLog)
        coEvery { dailyLogRepository.deleteLog(any()) } returns Unit

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteLog()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { dailyLogRepository.deleteLog(any()) }
    }
}
