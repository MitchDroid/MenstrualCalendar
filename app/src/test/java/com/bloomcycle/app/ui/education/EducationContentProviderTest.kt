package com.bloomcycle.app.ui.education

import android.content.Context
import android.content.res.Resources
import com.bloomcycle.app.R
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.domain.model.Symptom
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EducationContentProviderTest {

    private lateinit var context: Context
    private lateinit var resources: Resources

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        resources = mockk(relaxed = true)

        every { context.resources } returns resources
        every { context.getString(any()) } answers { "MockString_${firstArg<Int>()}" }
        every { resources.getStringArray(any()) } returns arrayOf("Item 1", "Item 2", "Item 3")
    }

    // ── getPhaseGuides() ───────────────────────────────────────

    @Test
    fun `getPhaseGuides returns exactly 4 guides`() {
        val guides = EducationContentProvider.getPhaseGuides(context)

        assertEquals(4, guides.size)
    }

    @Test
    fun `getPhaseGuides covers all cycle phases`() {
        val guides = EducationContentProvider.getPhaseGuides(context)
        val phases = guides.map { it.phase }.toSet()

        assertEquals(CyclePhase.entries.toSet(), phases)
    }

    @Test
    fun `getPhaseGuides each guide has non-empty lists`() {
        val guides = EducationContentProvider.getPhaseGuides(context)

        guides.forEach { guide ->
            assertTrue("whatHappens empty for ${guide.phase}", guide.whatHappens.isNotEmpty())
            assertTrue("commonSymptoms empty for ${guide.phase}", guide.commonSymptoms.isNotEmpty())
            assertTrue("selfCareTips empty for ${guide.phase}", guide.selfCareTips.isNotEmpty())
            assertTrue("nutritionTips empty for ${guide.phase}", guide.nutritionTips.isNotEmpty())
            assertTrue("exerciseTips empty for ${guide.phase}", guide.exerciseTips.isNotEmpty())
        }
    }

    @Test
    fun `getPhaseGuides each guide has emoji`() {
        val guides = EducationContentProvider.getPhaseGuides(context)

        guides.forEach { guide ->
            assertTrue("emoji empty for ${guide.phase}", guide.emoji.isNotEmpty())
        }
    }

    @Test
    fun `getPhaseGuides menstrual guide has correct emoji`() {
        val guides = EducationContentProvider.getPhaseGuides(context)
        val menstrual = guides.find { it.phase == CyclePhase.MENSTRUAL }!!

        assertEquals("\uD83C\uDF3A", menstrual.emoji) // 🌺
    }

    // ── getSymptomGuides() ─────────────────────────────────────

    @Test
    fun `getSymptomGuides returns exactly 10 guides`() {
        val guides = EducationContentProvider.getSymptomGuides(context)

        assertEquals(10, guides.size)
    }

    @Test
    fun `getSymptomGuides covers all symptoms`() {
        val guides = EducationContentProvider.getSymptomGuides(context)
        val symptoms = guides.map { it.symptom }.toSet()

        assertEquals(Symptom.entries.toSet(), symptoms)
    }

    @Test
    fun `getSymptomGuides each guide has management tips`() {
        val guides = EducationContentProvider.getSymptomGuides(context)

        guides.forEach { guide ->
            assertTrue("managementTips empty for ${guide.symptom}", guide.managementTips.isNotEmpty())
        }
    }

    @Test
    fun `getSymptomGuides each guide has emoji`() {
        val guides = EducationContentProvider.getSymptomGuides(context)

        guides.forEach { guide ->
            assertTrue("emoji empty for ${guide.symptom}", guide.emoji.isNotEmpty())
        }
    }

    // ── getHealthTips() ────────────────────────────────────────

    @Test
    fun `getHealthTips returns 19 tips`() {
        val tips = EducationContentProvider.getHealthTips(context)

        assertEquals(19, tips.size)
    }

    @Test
    fun `getHealthTips covers all tip categories`() {
        val tips = EducationContentProvider.getHealthTips(context)
        val categories = tips.map { it.category }.toSet()

        assertEquals(TipCategory.entries.toSet(), categories)
    }

    @Test
    fun `getHealthTips covers all cycle phases`() {
        val tips = EducationContentProvider.getHealthTips(context)
        val allPhases = tips.flatMap { it.applicablePhases }.toSet()

        assertEquals(CyclePhase.entries.toSet(), allPhases)
    }

    @Test
    fun `getHealthTips each tip has emoji`() {
        val tips = EducationContentProvider.getHealthTips(context)

        tips.forEach { tip ->
            assertTrue("emoji empty for tip '${tip.title}'", tip.emoji.isNotEmpty())
        }
    }

    @Test
    fun `getHealthTips each tip has non-empty applicable phases`() {
        val tips = EducationContentProvider.getHealthTips(context)

        tips.forEach { tip ->
            assertTrue("applicablePhases empty for '${tip.title}'", tip.applicablePhases.isNotEmpty())
        }
    }

    // ── getTipsForPhase() ──────────────────────────────────────

    @Test
    fun `getTipsForPhase returns only tips applicable to that phase`() {
        val menstrualTips = EducationContentProvider.getTipsForPhase(context, CyclePhase.MENSTRUAL)

        menstrualTips.forEach { tip ->
            assertTrue(
                "Tip '${tip.title}' not applicable to MENSTRUAL",
                CyclePhase.MENSTRUAL in tip.applicablePhases
            )
        }
    }

    @Test
    fun `getTipsForPhase menstrual returns at least 5 tips`() {
        val tips = EducationContentProvider.getTipsForPhase(context, CyclePhase.MENSTRUAL)

        // 5 menstrual-specific + 2 universal = 7
        assertTrue("Expected at least 5 menstrual tips, got ${tips.size}", tips.size >= 5)
    }

    @Test
    fun `getTipsForPhase includes universal tips`() {
        val follicularTips = EducationContentProvider.getTipsForPhase(context, CyclePhase.FOLLICULAR)

        // Universal tips (applicable to all phases) should be included
        val universalTips = follicularTips.filter { it.applicablePhases.size == CyclePhase.entries.size }
        assertTrue("Expected universal tips for FOLLICULAR, got ${universalTips.size}", universalTips.isNotEmpty())
    }

    // ── getPhaseGuide() ────────────────────────────────────────

    @Test
    fun `getPhaseGuide returns guide for valid phase`() {
        val guide = EducationContentProvider.getPhaseGuide(context, CyclePhase.OVULATION)

        assertNotNull(guide)
        assertEquals(CyclePhase.OVULATION, guide!!.phase)
    }

    // ── getSymptomGuide() ──────────────────────────────────────

    @Test
    fun `getSymptomGuide returns guide for valid symptom`() {
        val guide = EducationContentProvider.getSymptomGuide(context, Symptom.CRAMPS)

        assertNotNull(guide)
        assertEquals(Symptom.CRAMPS, guide!!.symptom)
    }
}
