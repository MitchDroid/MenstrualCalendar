package com.bloomcycle.app.ui.education

import android.content.Context
import com.bloomcycle.app.R
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.domain.model.Symptom

// ── Data Models ─────────────────────────────────────────────────

data class PhaseGuide(
    val phase: CyclePhase,
    val title: String,
    val emoji: String,
    val durationInfo: String,
    val description: String,
    val whatHappens: List<String>,
    val commonSymptoms: List<String>,
    val selfCareTips: List<String>,
    val nutritionTips: List<String>,
    val exerciseTips: List<String>
)

data class SymptomGuide(
    val symptom: Symptom,
    val displayName: String,
    val emoji: String,
    val description: String,
    val whyItHappens: String,
    val managementTips: List<String>,
    val whenToSeeDoctor: String
)

data class HealthTip(
    val title: String,
    val emoji: String,
    val content: String,
    val category: TipCategory,
    val applicablePhases: List<CyclePhase>
)

enum class TipCategory {
    NUTRITION,
    EXERCISE,
    SELF_CARE,
    SLEEP,
    MENTAL_HEALTH
}

// ── Localized Content Provider ──────────────────────────────────

object EducationContentProvider {

    // ── Cycle Phase Guides ──────────────────────────────────────

    fun getPhaseGuides(context: Context): List<PhaseGuide> = listOf(
        PhaseGuide(
            phase = CyclePhase.MENSTRUAL,
            title = context.getString(R.string.edu_phase_menstrual_title),
            emoji = "\uD83C\uDF3A",
            durationInfo = context.getString(R.string.edu_phase_menstrual_duration),
            description = context.getString(R.string.edu_phase_menstrual_description),
            whatHappens = context.resources.getStringArray(R.array.edu_phase_menstrual_what_happens).toList(),
            commonSymptoms = context.resources.getStringArray(R.array.edu_phase_menstrual_symptoms).toList(),
            selfCareTips = context.resources.getStringArray(R.array.edu_phase_menstrual_self_care).toList(),
            nutritionTips = context.resources.getStringArray(R.array.edu_phase_menstrual_nutrition).toList(),
            exerciseTips = context.resources.getStringArray(R.array.edu_phase_menstrual_exercise).toList()
        ),
        PhaseGuide(
            phase = CyclePhase.FOLLICULAR,
            title = context.getString(R.string.edu_phase_follicular_title),
            emoji = "\uD83C\uDF31",
            durationInfo = context.getString(R.string.edu_phase_follicular_duration),
            description = context.getString(R.string.edu_phase_follicular_description),
            whatHappens = context.resources.getStringArray(R.array.edu_phase_follicular_what_happens).toList(),
            commonSymptoms = context.resources.getStringArray(R.array.edu_phase_follicular_symptoms).toList(),
            selfCareTips = context.resources.getStringArray(R.array.edu_phase_follicular_self_care).toList(),
            nutritionTips = context.resources.getStringArray(R.array.edu_phase_follicular_nutrition).toList(),
            exerciseTips = context.resources.getStringArray(R.array.edu_phase_follicular_exercise).toList()
        ),
        PhaseGuide(
            phase = CyclePhase.OVULATION,
            title = context.getString(R.string.edu_phase_ovulation_title),
            emoji = "\u2728",
            durationInfo = context.getString(R.string.edu_phase_ovulation_duration),
            description = context.getString(R.string.edu_phase_ovulation_description),
            whatHappens = context.resources.getStringArray(R.array.edu_phase_ovulation_what_happens).toList(),
            commonSymptoms = context.resources.getStringArray(R.array.edu_phase_ovulation_symptoms).toList(),
            selfCareTips = context.resources.getStringArray(R.array.edu_phase_ovulation_self_care).toList(),
            nutritionTips = context.resources.getStringArray(R.array.edu_phase_ovulation_nutrition).toList(),
            exerciseTips = context.resources.getStringArray(R.array.edu_phase_ovulation_exercise).toList()
        ),
        PhaseGuide(
            phase = CyclePhase.LUTEAL,
            title = context.getString(R.string.edu_phase_luteal_title),
            emoji = "\uD83C\uDF19",
            durationInfo = context.getString(R.string.edu_phase_luteal_duration),
            description = context.getString(R.string.edu_phase_luteal_description),
            whatHappens = context.resources.getStringArray(R.array.edu_phase_luteal_what_happens).toList(),
            commonSymptoms = context.resources.getStringArray(R.array.edu_phase_luteal_symptoms).toList(),
            selfCareTips = context.resources.getStringArray(R.array.edu_phase_luteal_self_care).toList(),
            nutritionTips = context.resources.getStringArray(R.array.edu_phase_luteal_nutrition).toList(),
            exerciseTips = context.resources.getStringArray(R.array.edu_phase_luteal_exercise).toList()
        )
    )

    // ── Symptom Guides ──────────────────────────────────────────

    fun getSymptomGuides(context: Context): List<SymptomGuide> = listOf(
        SymptomGuide(
            symptom = Symptom.CRAMPS,
            displayName = context.getString(R.string.edu_symptom_cramps_name),
            emoji = "\uD83E\uDD1F",
            description = context.getString(R.string.edu_symptom_cramps_desc),
            whyItHappens = context.getString(R.string.edu_symptom_cramps_why),
            managementTips = context.resources.getStringArray(R.array.edu_symptom_cramps_tips).toList(),
            whenToSeeDoctor = context.getString(R.string.edu_symptom_cramps_doctor)
        ),
        SymptomGuide(
            symptom = Symptom.HEADACHE,
            displayName = context.getString(R.string.edu_symptom_headache_name),
            emoji = "\uD83E\uDD15",
            description = context.getString(R.string.edu_symptom_headache_desc),
            whyItHappens = context.getString(R.string.edu_symptom_headache_why),
            managementTips = context.resources.getStringArray(R.array.edu_symptom_headache_tips).toList(),
            whenToSeeDoctor = context.getString(R.string.edu_symptom_headache_doctor)
        ),
        SymptomGuide(
            symptom = Symptom.BLOATING,
            displayName = context.getString(R.string.edu_symptom_bloating_name),
            emoji = "\uD83C\uDF88",
            description = context.getString(R.string.edu_symptom_bloating_desc),
            whyItHappens = context.getString(R.string.edu_symptom_bloating_why),
            managementTips = context.resources.getStringArray(R.array.edu_symptom_bloating_tips).toList(),
            whenToSeeDoctor = context.getString(R.string.edu_symptom_bloating_doctor)
        ),
        SymptomGuide(
            symptom = Symptom.ACNE,
            displayName = context.getString(R.string.edu_symptom_acne_name),
            emoji = "\uD83D\uDCA2",
            description = context.getString(R.string.edu_symptom_acne_desc),
            whyItHappens = context.getString(R.string.edu_symptom_acne_why),
            managementTips = context.resources.getStringArray(R.array.edu_symptom_acne_tips).toList(),
            whenToSeeDoctor = context.getString(R.string.edu_symptom_acne_doctor)
        ),
        SymptomGuide(
            symptom = Symptom.MOOD_SWINGS,
            displayName = context.getString(R.string.edu_symptom_mood_swings_name),
            emoji = "\uD83C\uDFA2",
            description = context.getString(R.string.edu_symptom_mood_swings_desc),
            whyItHappens = context.getString(R.string.edu_symptom_mood_swings_why),
            managementTips = context.resources.getStringArray(R.array.edu_symptom_mood_swings_tips).toList(),
            whenToSeeDoctor = context.getString(R.string.edu_symptom_mood_swings_doctor)
        ),
        SymptomGuide(
            symptom = Symptom.FATIGUE,
            displayName = context.getString(R.string.edu_symptom_fatigue_name),
            emoji = "\uD83D\uDE34",
            description = context.getString(R.string.edu_symptom_fatigue_desc),
            whyItHappens = context.getString(R.string.edu_symptom_fatigue_why),
            managementTips = context.resources.getStringArray(R.array.edu_symptom_fatigue_tips).toList(),
            whenToSeeDoctor = context.getString(R.string.edu_symptom_fatigue_doctor)
        ),
        SymptomGuide(
            symptom = Symptom.TENDER_BREASTS,
            displayName = context.getString(R.string.edu_symptom_tender_breasts_name),
            emoji = "\uD83E\uDE77",
            description = context.getString(R.string.edu_symptom_tender_breasts_desc),
            whyItHappens = context.getString(R.string.edu_symptom_tender_breasts_why),
            managementTips = context.resources.getStringArray(R.array.edu_symptom_tender_breasts_tips).toList(),
            whenToSeeDoctor = context.getString(R.string.edu_symptom_tender_breasts_doctor)
        ),
        SymptomGuide(
            symptom = Symptom.BACK_PAIN,
            displayName = context.getString(R.string.edu_symptom_back_pain_name),
            emoji = "\uD83D\uDECB",
            description = context.getString(R.string.edu_symptom_back_pain_desc),
            whyItHappens = context.getString(R.string.edu_symptom_back_pain_why),
            managementTips = context.resources.getStringArray(R.array.edu_symptom_back_pain_tips).toList(),
            whenToSeeDoctor = context.getString(R.string.edu_symptom_back_pain_doctor)
        ),
        SymptomGuide(
            symptom = Symptom.NAUSEA,
            displayName = context.getString(R.string.edu_symptom_nausea_name),
            emoji = "\uD83E\uDD22",
            description = context.getString(R.string.edu_symptom_nausea_desc),
            whyItHappens = context.getString(R.string.edu_symptom_nausea_why),
            managementTips = context.resources.getStringArray(R.array.edu_symptom_nausea_tips).toList(),
            whenToSeeDoctor = context.getString(R.string.edu_symptom_nausea_doctor)
        ),
        SymptomGuide(
            symptom = Symptom.FOOD_CRAVINGS,
            displayName = context.getString(R.string.edu_symptom_food_cravings_name),
            emoji = "\uD83C\uDF69",
            description = context.getString(R.string.edu_symptom_food_cravings_desc),
            whyItHappens = context.getString(R.string.edu_symptom_food_cravings_why),
            managementTips = context.resources.getStringArray(R.array.edu_symptom_food_cravings_tips).toList(),
            whenToSeeDoctor = context.getString(R.string.edu_symptom_food_cravings_doctor)
        )
    )

    // ── Phase-Aware Health Tips ──────────────────────────────────

    fun getHealthTips(context: Context): List<HealthTip> = listOf(
        // ── Menstrual Phase Tips ────────────────────────────────
        HealthTip(
            title = context.getString(R.string.edu_tip_iron_recovery_title),
            emoji = "\uD83E\uDD57",
            content = context.getString(R.string.edu_tip_iron_recovery_content),
            category = TipCategory.NUTRITION,
            applicablePhases = listOf(CyclePhase.MENSTRUAL)
        ),
        HealthTip(
            title = context.getString(R.string.edu_tip_cozy_movement_title),
            emoji = "\uD83E\uDDD8",
            content = context.getString(R.string.edu_tip_cozy_movement_content),
            category = TipCategory.EXERCISE,
            applicablePhases = listOf(CyclePhase.MENSTRUAL)
        ),
        HealthTip(
            title = context.getString(R.string.edu_tip_rest_without_guilt_title),
            emoji = "\uD83D\uDCA4",
            content = context.getString(R.string.edu_tip_rest_without_guilt_content),
            category = TipCategory.SELF_CARE,
            applicablePhases = listOf(CyclePhase.MENSTRUAL)
        ),
        HealthTip(
            title = context.getString(R.string.edu_tip_sleep_sanctuary_title),
            emoji = "\uD83C\uDF19",
            content = context.getString(R.string.edu_tip_sleep_sanctuary_content),
            category = TipCategory.SLEEP,
            applicablePhases = listOf(CyclePhase.MENSTRUAL)
        ),
        HealthTip(
            title = context.getString(R.string.edu_tip_emotional_check_in_title),
            emoji = "\uD83D\uDCDD",
            content = context.getString(R.string.edu_tip_emotional_check_in_content),
            category = TipCategory.MENTAL_HEALTH,
            applicablePhases = listOf(CyclePhase.MENSTRUAL)
        ),

        // ── Follicular Phase Tips ───────────────────────────────
        HealthTip(
            title = context.getString(R.string.edu_tip_fresh_fermented_foods_title),
            emoji = "\uD83E\uDD66",
            content = context.getString(R.string.edu_tip_fresh_fermented_foods_content),
            category = TipCategory.NUTRITION,
            applicablePhases = listOf(CyclePhase.FOLLICULAR)
        ),
        HealthTip(
            title = context.getString(R.string.edu_tip_challenge_yourself_title),
            emoji = "\uD83C\uDFC3",
            content = context.getString(R.string.edu_tip_challenge_yourself_content),
            category = TipCategory.EXERCISE,
            applicablePhases = listOf(CyclePhase.FOLLICULAR)
        ),
        HealthTip(
            title = context.getString(R.string.edu_tip_creative_energy_boost_title),
            emoji = "\uD83C\uDFA8",
            content = context.getString(R.string.edu_tip_creative_energy_boost_content),
            category = TipCategory.SELF_CARE,
            applicablePhases = listOf(CyclePhase.FOLLICULAR)
        ),
        HealthTip(
            title = context.getString(R.string.edu_tip_optimize_schedule_title),
            emoji = "\u2B50",
            content = context.getString(R.string.edu_tip_optimize_schedule_content),
            category = TipCategory.MENTAL_HEALTH,
            applicablePhases = listOf(CyclePhase.FOLLICULAR)
        ),

        // ── Ovulation Phase Tips ────────────────────────────────
        HealthTip(
            title = context.getString(R.string.edu_tip_peak_performance_fuel_title),
            emoji = "\u26A1",
            content = context.getString(R.string.edu_tip_peak_performance_fuel_content),
            category = TipCategory.NUTRITION,
            applicablePhases = listOf(CyclePhase.OVULATION)
        ),
        HealthTip(
            title = context.getString(R.string.edu_tip_go_for_goals_title),
            emoji = "\uD83C\uDFC6",
            content = context.getString(R.string.edu_tip_go_for_goals_content),
            category = TipCategory.EXERCISE,
            applicablePhases = listOf(CyclePhase.OVULATION)
        ),
        HealthTip(
            title = context.getString(R.string.edu_tip_social_connection_title),
            emoji = "\uD83D\uDC9B",
            content = context.getString(R.string.edu_tip_social_connection_content),
            category = TipCategory.MENTAL_HEALTH,
            applicablePhases = listOf(CyclePhase.OVULATION)
        ),

        // ── Luteal Phase Tips ───────────────────────────────────
        HealthTip(
            title = context.getString(R.string.edu_tip_complex_carb_comfort_title),
            emoji = "\uD83C\uDF60",
            content = context.getString(R.string.edu_tip_complex_carb_comfort_content),
            category = TipCategory.NUTRITION,
            applicablePhases = listOf(CyclePhase.LUTEAL)
        ),
        HealthTip(
            title = context.getString(R.string.edu_tip_gentle_is_powerful_title),
            emoji = "\uD83E\uDD3E",
            content = context.getString(R.string.edu_tip_gentle_is_powerful_content),
            category = TipCategory.EXERCISE,
            applicablePhases = listOf(CyclePhase.LUTEAL)
        ),
        HealthTip(
            title = context.getString(R.string.edu_tip_pms_self_care_kit_title),
            emoji = "\uD83C\uDF3F",
            content = context.getString(R.string.edu_tip_pms_self_care_kit_content),
            category = TipCategory.SELF_CARE,
            applicablePhases = listOf(CyclePhase.LUTEAL)
        ),
        HealthTip(
            title = context.getString(R.string.edu_tip_wind_down_routine_title),
            emoji = "\uD83D\uDECC",
            content = context.getString(R.string.edu_tip_wind_down_routine_content),
            category = TipCategory.SLEEP,
            applicablePhases = listOf(CyclePhase.LUTEAL)
        ),
        HealthTip(
            title = context.getString(R.string.edu_tip_mood_management_title),
            emoji = "\uD83E\uDDD8\u200D\u2640\uFE0F",
            content = context.getString(R.string.edu_tip_mood_management_content),
            category = TipCategory.MENTAL_HEALTH,
            applicablePhases = listOf(CyclePhase.LUTEAL)
        ),

        // ── Universal Tips ──────────────────────────────────────
        HealthTip(
            title = context.getString(R.string.edu_tip_hydration_matters_title),
            emoji = "\uD83D\uDCA7",
            content = context.getString(R.string.edu_tip_hydration_matters_content),
            category = TipCategory.NUTRITION,
            applicablePhases = listOf(CyclePhase.MENSTRUAL, CyclePhase.FOLLICULAR, CyclePhase.OVULATION, CyclePhase.LUTEAL)
        ),
        HealthTip(
            title = context.getString(R.string.edu_tip_track_to_learn_title),
            emoji = "\uD83D\uDCCA",
            content = context.getString(R.string.edu_tip_track_to_learn_content),
            category = TipCategory.SELF_CARE,
            applicablePhases = listOf(CyclePhase.MENSTRUAL, CyclePhase.FOLLICULAR, CyclePhase.OVULATION, CyclePhase.LUTEAL)
        )
    )

    /** Get tips filtered by the current cycle phase */
    fun getTipsForPhase(context: Context, phase: CyclePhase): List<HealthTip> =
        getHealthTips(context).filter { phase in it.applicablePhases }

    /** Get the guide for a specific cycle phase */
    fun getPhaseGuide(context: Context, phase: CyclePhase): PhaseGuide? =
        getPhaseGuides(context).find { it.phase == phase }

    /** Get the guide for a specific symptom */
    fun getSymptomGuide(context: Context, symptom: Symptom): SymptomGuide? =
        getSymptomGuides(context).find { it.symptom == symptom }
}
