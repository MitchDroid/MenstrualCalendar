package com.bloomcycle.app.ui.onboarding

import com.bloomcycle.app.data.preferences.UserPreferencesManager
import com.bloomcycle.app.domain.model.UserGoal
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var preferencesManager: UserPreferencesManager

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        preferencesManager = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = OnboardingViewModel(preferencesManager)

    @Test
    fun `initial uiState is not null`() = runTest {
        val viewModel = createViewModel()
        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `initial step is WELCOME`() = runTest {
        val viewModel = createViewModel()
        assertEquals(OnboardingStep.WELCOME, viewModel.uiState.value.currentStep)
        assertEquals(0, viewModel.uiState.value.stepIndex)
    }

    @Test
    fun `nextStep advances to YOUR_NAME`() = runTest {
        val viewModel = createViewModel()

        viewModel.nextStep()

        assertEquals(OnboardingStep.YOUR_NAME, viewModel.uiState.value.currentStep)
        assertEquals(1, viewModel.uiState.value.stepIndex)
    }

    @Test
    fun `two nextSteps advances to LAST_PERIOD`() = runTest {
        val viewModel = createViewModel()

        viewModel.nextStep() // WELCOME → YOUR_NAME
        viewModel.nextStep() // YOUR_NAME → LAST_PERIOD

        assertEquals(OnboardingStep.LAST_PERIOD, viewModel.uiState.value.currentStep)
        assertEquals(2, viewModel.uiState.value.stepIndex)
    }

    @Test
    fun `previousStep goes back after two next steps`() = runTest {
        val viewModel = createViewModel()

        viewModel.nextStep() // WELCOME → YOUR_NAME
        viewModel.nextStep() // YOUR_NAME → LAST_PERIOD
        viewModel.previousStep() // LAST_PERIOD → YOUR_NAME

        assertEquals(OnboardingStep.YOUR_NAME, viewModel.uiState.value.currentStep)
        assertEquals(1, viewModel.uiState.value.stepIndex)
    }

    @Test
    fun `previousStep does not go below WELCOME`() = runTest {
        val viewModel = createViewModel()

        viewModel.previousStep()

        assertEquals(OnboardingStep.WELCOME, viewModel.uiState.value.currentStep)
        assertEquals(0, viewModel.uiState.value.stepIndex)
    }

    @Test
    fun `setLastPeriodDate updates state`() = runTest {
        val viewModel = createViewModel()
        val date = LocalDate.of(2026, 5, 1)

        viewModel.setLastPeriodDate(date)

        assertEquals(date, viewModel.uiState.value.lastPeriodDate)
    }

    @Test
    fun `setCycleLength updates state`() = runTest {
        val viewModel = createViewModel()

        viewModel.setCycleLength(30)

        assertEquals(30, viewModel.uiState.value.cycleLength)
    }

    @Test
    fun `setPeriodDuration updates state`() = runTest {
        val viewModel = createViewModel()

        viewModel.setPeriodDuration(6)

        assertEquals(6, viewModel.uiState.value.periodDuration)
    }

    @Test
    fun `setUserGoal updates state`() = runTest {
        val viewModel = createViewModel()

        viewModel.setUserGoal(UserGoal.TRYING_TO_CONCEIVE)

        assertEquals(UserGoal.TRYING_TO_CONCEIVE, viewModel.uiState.value.userGoal)
    }

    @Test
    fun `completeOnboarding saves data to preferences`() = runTest {
        coEvery { preferencesManager.saveOnboardingData(any(), any(), any(), any(), any(), any()) } returns Unit
        coEvery { preferencesManager.setOnboardingCompleted(true) } returns Unit

        val viewModel = createViewModel()
        viewModel.setLastPeriodDate(LocalDate.of(2026, 5, 1))
        viewModel.setCycleLength(28)
        viewModel.setPeriodDuration(5)
        viewModel.setUserGoal(UserGoal.TRACK_CYCLE)

        viewModel.completeOnboarding()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { preferencesManager.saveOnboardingData(any(), any(), any(), any(), any(), any()) }
    }
}
