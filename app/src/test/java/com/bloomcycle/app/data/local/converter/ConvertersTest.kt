package com.bloomcycle.app.data.local.converter

import com.bloomcycle.app.domain.model.CervicalMucus
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.SexualActivity
import com.bloomcycle.app.domain.model.Symptom
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class ConvertersTest {

    private lateinit var converters: Converters

    @Before
    fun setUp() {
        converters = Converters()
    }

    // ── LocalDate ↔ Long ──────────────────────────────────────

    @Test
    fun `fromLocalDate converts date to epoch day`() {
        val date = LocalDate.of(2026, 5, 14)
        val result = converters.fromLocalDate(date)

        assertEquals(date.toEpochDay(), result)
    }

    @Test
    fun `fromLocalDate returns null for null input`() {
        assertNull(converters.fromLocalDate(null))
    }

    @Test
    fun `toLocalDate converts epoch day to date`() {
        val date = LocalDate.of(2026, 5, 14)
        val epochDay = date.toEpochDay()

        val result = converters.toLocalDate(epochDay)

        assertEquals(date, result)
    }

    @Test
    fun `toLocalDate returns null for null input`() {
        assertNull(converters.toLocalDate(null))
    }

    @Test
    fun `LocalDate round-trip preserves value`() {
        val date = LocalDate.of(2026, 1, 1)
        val result = converters.toLocalDate(converters.fromLocalDate(date))

        assertEquals(date, result)
    }

    // ── List<Symptom> ↔ String ────────────────────────────────

    @Test
    fun `fromSymptomList converts list to comma-separated string`() {
        val symptoms = listOf(Symptom.CRAMPS, Symptom.HEADACHE, Symptom.BLOATING)

        val result = converters.fromSymptomList(symptoms)

        assertEquals("CRAMPS,HEADACHE,BLOATING", result)
    }

    @Test
    fun `fromSymptomList returns null for null input`() {
        assertNull(converters.fromSymptomList(null))
    }

    @Test
    fun `fromSymptomList handles empty list`() {
        val result = converters.fromSymptomList(emptyList())

        assertEquals("", result)
    }

    @Test
    fun `fromSymptomList handles single symptom`() {
        val result = converters.fromSymptomList(listOf(Symptom.FATIGUE))

        assertEquals("FATIGUE", result)
    }

    @Test
    fun `toSymptomList parses comma-separated string`() {
        val result = converters.toSymptomList("CRAMPS,HEADACHE,BLOATING")

        assertEquals(3, result.size)
        assertEquals(Symptom.CRAMPS, result[0])
        assertEquals(Symptom.HEADACHE, result[1])
        assertEquals(Symptom.BLOATING, result[2])
    }

    @Test
    fun `toSymptomList returns empty list for null`() {
        val result = converters.toSymptomList(null)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `toSymptomList returns empty list for blank string`() {
        val result = converters.toSymptomList("")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `toSymptomList returns empty list for whitespace-only string`() {
        val result = converters.toSymptomList("   ")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `toSymptomList skips invalid enum names gracefully`() {
        val result = converters.toSymptomList("CRAMPS,INVALID_SYMPTOM,HEADACHE")

        assertEquals(2, result.size)
        assertEquals(Symptom.CRAMPS, result[0])
        assertEquals(Symptom.HEADACHE, result[1])
    }

    @Test
    fun `toSymptomList trims whitespace around names`() {
        val result = converters.toSymptomList("CRAMPS , HEADACHE , BLOATING")

        assertEquals(3, result.size)
        assertEquals(Symptom.CRAMPS, result[0])
    }

    @Test
    fun `Symptom list round-trip preserves all values`() {
        val original = listOf(Symptom.CRAMPS, Symptom.FOOD_CRAVINGS, Symptom.TENDER_BREASTS)

        val result = converters.toSymptomList(converters.fromSymptomList(original))

        assertEquals(original, result)
    }

    @Test
    fun `Symptom list round-trip preserves all enum values`() {
        val allSymptoms = Symptom.entries.toList()

        val serialized = converters.fromSymptomList(allSymptoms)
        val deserialized = converters.toSymptomList(serialized)

        assertEquals(allSymptoms, deserialized)
    }

    // ── FlowIntensity ↔ String ────────────────────────────────

    @Test
    fun `fromFlowIntensity converts to name string`() {
        assertEquals("HEAVY", converters.fromFlowIntensity(FlowIntensity.HEAVY))
        assertEquals("NONE", converters.fromFlowIntensity(FlowIntensity.NONE))
    }

    @Test
    fun `fromFlowIntensity returns null for null`() {
        assertNull(converters.fromFlowIntensity(null))
    }

    @Test
    fun `toFlowIntensity parses valid string`() {
        assertEquals(FlowIntensity.HEAVY, converters.toFlowIntensity("HEAVY"))
        assertEquals(FlowIntensity.SPOTTING, converters.toFlowIntensity("SPOTTING"))
    }

    @Test
    fun `toFlowIntensity returns null for null`() {
        assertNull(converters.toFlowIntensity(null))
    }

    @Test
    fun `toFlowIntensity returns null for invalid string`() {
        assertNull(converters.toFlowIntensity("SUPER_HEAVY"))
    }

    @Test
    fun `FlowIntensity round-trip preserves all enum values`() {
        FlowIntensity.entries.forEach { original ->
            val result = converters.toFlowIntensity(converters.fromFlowIntensity(original))
            assertEquals("Round-trip failed for $original", original, result)
        }
    }

    // ── Mood ↔ String ─────────────────────────────────────────

    @Test
    fun `fromMood converts to name string`() {
        assertEquals("HAPPY", converters.fromMood(Mood.HAPPY))
        assertEquals("ANXIOUS", converters.fromMood(Mood.ANXIOUS))
    }

    @Test
    fun `fromMood returns null for null`() {
        assertNull(converters.fromMood(null))
    }

    @Test
    fun `toMood parses valid string`() {
        assertEquals(Mood.SAD, converters.toMood("SAD"))
        assertEquals(Mood.ENERGETIC, converters.toMood("ENERGETIC"))
    }

    @Test
    fun `toMood returns null for null`() {
        assertNull(converters.toMood(null))
    }

    @Test
    fun `toMood returns null for invalid string`() {
        assertNull(converters.toMood("ECSTATIC"))
    }

    @Test
    fun `Mood round-trip preserves all enum values`() {
        Mood.entries.forEach { original ->
            val result = converters.toMood(converters.fromMood(original))
            assertEquals("Round-trip failed for $original", original, result)
        }
    }

    // ── SexualActivity ↔ String ───────────────────────────────

    @Test
    fun `fromSexualActivity converts to name string`() {
        assertEquals("PROTECTED", converters.fromSexualActivity(SexualActivity.PROTECTED))
    }

    @Test
    fun `fromSexualActivity returns null for null`() {
        assertNull(converters.fromSexualActivity(null))
    }

    @Test
    fun `toSexualActivity parses valid string`() {
        assertEquals(SexualActivity.UNPROTECTED, converters.toSexualActivity("UNPROTECTED"))
        assertEquals(SexualActivity.SOLO, converters.toSexualActivity("SOLO"))
    }

    @Test
    fun `toSexualActivity returns null for null`() {
        assertNull(converters.toSexualActivity(null))
    }

    @Test
    fun `toSexualActivity returns null for invalid string`() {
        assertNull(converters.toSexualActivity("INVALID"))
    }

    @Test
    fun `SexualActivity round-trip preserves all enum values`() {
        SexualActivity.entries.forEach { original ->
            val result = converters.toSexualActivity(converters.fromSexualActivity(original))
            assertEquals("Round-trip failed for $original", original, result)
        }
    }

    // ── CervicalMucus ↔ String ────────────────────────────────

    @Test
    fun `fromCervicalMucus converts to name string`() {
        assertEquals("EGG_WHITE", converters.fromCervicalMucus(CervicalMucus.EGG_WHITE))
        assertEquals("DRY", converters.fromCervicalMucus(CervicalMucus.DRY))
    }

    @Test
    fun `fromCervicalMucus returns null for null`() {
        assertNull(converters.fromCervicalMucus(null))
    }

    @Test
    fun `toCervicalMucus parses valid string`() {
        assertEquals(CervicalMucus.CREAMY, converters.toCervicalMucus("CREAMY"))
        assertEquals(CervicalMucus.WATERY, converters.toCervicalMucus("WATERY"))
    }

    @Test
    fun `toCervicalMucus returns null for null`() {
        assertNull(converters.toCervicalMucus(null))
    }

    @Test
    fun `toCervicalMucus returns null for invalid string`() {
        assertNull(converters.toCervicalMucus("SLIPPERY"))
    }

    @Test
    fun `CervicalMucus round-trip preserves all enum values`() {
        CervicalMucus.entries.forEach { original ->
            val result = converters.toCervicalMucus(converters.fromCervicalMucus(original))
            assertEquals("Round-trip failed for $original", original, result)
        }
    }
}
