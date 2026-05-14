package com.bloomcycle.app.ui.util

import androidx.annotation.StringRes
import com.bloomcycle.app.R
import com.bloomcycle.app.domain.model.CervicalMucus
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.SexualActivity
import com.bloomcycle.app.domain.model.Symptom

/**
 * Centralised mapping from domain enums to translatable string resource IDs.
 *
 * Usage in @Composable code:  stringResource(intensity.displayNameRes())
 * Usage in non-Composable code: context.getString(intensity.displayNameRes())
 */

@StringRes
fun FlowIntensity.displayNameRes(): Int = when (this) {
    FlowIntensity.NONE -> R.string.flow_none
    FlowIntensity.SPOTTING -> R.string.flow_spotting
    FlowIntensity.LIGHT -> R.string.flow_light
    FlowIntensity.MEDIUM -> R.string.flow_medium
    FlowIntensity.HEAVY -> R.string.flow_heavy
}

@StringRes
fun Mood.displayNameRes(): Int = when (this) {
    Mood.HAPPY -> R.string.mood_happy
    Mood.SAD -> R.string.mood_sad
    Mood.ANXIOUS -> R.string.mood_anxious
    Mood.IRRITABLE -> R.string.mood_irritable
    Mood.CALM -> R.string.mood_calm
    Mood.ENERGETIC -> R.string.mood_energetic
}

@StringRes
fun Symptom.displayNameRes(): Int = when (this) {
    Symptom.CRAMPS -> R.string.symptom_cramps
    Symptom.HEADACHE -> R.string.symptom_headache
    Symptom.BLOATING -> R.string.symptom_bloating
    Symptom.ACNE -> R.string.symptom_acne
    Symptom.MOOD_SWINGS -> R.string.symptom_mood_swings
    Symptom.FATIGUE -> R.string.symptom_fatigue
    Symptom.TENDER_BREASTS -> R.string.symptom_tender_breasts
    Symptom.BACK_PAIN -> R.string.symptom_back_pain
    Symptom.NAUSEA -> R.string.symptom_nausea
    Symptom.FOOD_CRAVINGS -> R.string.symptom_food_cravings
}

@StringRes
fun CervicalMucus.displayNameRes(): Int = when (this) {
    CervicalMucus.DRY -> R.string.cervical_dry
    CervicalMucus.STICKY -> R.string.cervical_sticky
    CervicalMucus.CREAMY -> R.string.cervical_creamy
    CervicalMucus.WATERY -> R.string.cervical_watery
    CervicalMucus.EGG_WHITE -> R.string.cervical_egg_white
}

@StringRes
fun SexualActivity.displayNameRes(): Int = when (this) {
    SexualActivity.PROTECTED -> R.string.sexual_protected
    SexualActivity.UNPROTECTED -> R.string.sexual_unprotected
    SexualActivity.SOLO -> R.string.sexual_solo
}
