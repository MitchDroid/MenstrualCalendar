package com.bloomcycle.app.data.notification

import android.content.Context
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Method

/**
 * JVM unit tests for [NotificationScheduler].
 *
 * WorkManager interaction tests (enqueue, cancel, reschedule) are NOT possible
 * in JVM tests because WorkManager.getInstance(context) requires a real Android
 * Context with getApplicationContext(). Those behaviors belong in instrumented
 * (androidTest) tests.
 *
 * Here we focus on the time-calculation logic (calculateDelayToTargetHour),
 * which is pure Kotlin logic with no Android framework dependencies.
 */
class NotificationSchedulerTest {

    private lateinit var context: Context
    private lateinit var scheduler: NotificationScheduler

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        scheduler = NotificationScheduler(context)
    }

    // ── calculateDelayToTargetHour() ──────────────────────────
    // Access private method via reflection to test time logic.

    @Test
    fun `calculateDelayToTargetHour returns positive delay`() {
        val method = getCalculateDelayMethod()
        val delay = method.invoke(scheduler, 9) as Long

        assertTrue("Delay should be positive, got $delay", delay > 0)
    }

    @Test
    fun `calculateDelayToTargetHour returns delay under 24 hours`() {
        val method = getCalculateDelayMethod()
        val delay = method.invoke(scheduler, 9) as Long
        val twentyFourHoursMs = 24 * 60 * 60 * 1000L

        assertTrue(
            "Delay should be under 24 hours, got ${delay}ms",
            delay <= twentyFourHoursMs
        )
    }

    @Test
    fun `calculateDelayToTargetHour for midnight returns valid delay`() {
        val method = getCalculateDelayMethod()
        val delay = method.invoke(scheduler, 0) as Long

        assertTrue("Delay for midnight should be positive, got $delay", delay > 0)
    }

    @Test
    fun `calculateDelayToTargetHour for different hours all return positive`() {
        val method = getCalculateDelayMethod()

        for (hour in 0..23) {
            val delay = method.invoke(scheduler, hour) as Long
            assertTrue("Delay for hour $hour should be positive, got $delay", delay > 0)
        }
    }

    @Test
    fun `calculateDelayToTargetHour for noon returns valid delay`() {
        val method = getCalculateDelayMethod()
        val delay = method.invoke(scheduler, 12) as Long

        assertTrue("Delay for noon should be positive, got $delay", delay > 0)
        assertTrue(
            "Delay should be under 24h",
            delay <= 24 * 60 * 60 * 1000L
        )
    }

    @Test
    fun `calculateDelayToTargetHour for end of day returns valid delay`() {
        val method = getCalculateDelayMethod()
        val delay = method.invoke(scheduler, 23) as Long

        assertTrue("Delay for 23:00 should be positive, got $delay", delay > 0)
        assertTrue(
            "Delay should be under 24h",
            delay <= 24 * 60 * 60 * 1000L
        )
    }

    // ── Helper ────────────────────────────────────────────────

    private fun getCalculateDelayMethod(): Method {
        return NotificationScheduler::class.java
            .getDeclaredMethod("calculateDelayToTargetHour", Int::class.java)
            .also { it.isAccessible = true }
    }
}
