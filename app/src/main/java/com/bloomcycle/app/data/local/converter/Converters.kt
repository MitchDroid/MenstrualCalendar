package com.bloomcycle.app.data.local.converter

import androidx.room.TypeConverter
import com.bloomcycle.app.domain.model.CervicalMucus
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.SexualActivity
import com.bloomcycle.app.domain.model.Symptom
import java.time.LocalDate

class Converters {

    // ── LocalDate ↔ Long (epoch day) ──────────────────────────

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun toLocalDate(epochDay: Long?): LocalDate? = epochDay?.let { LocalDate.ofEpochDay(it) }

    // ── List<Symptom> ↔ String (comma-separated) ─────────────

    @TypeConverter
    fun fromSymptomList(symptoms: List<Symptom>?): String? =
        symptoms?.joinToString(",") { it.name }

    @TypeConverter
    fun toSymptomList(data: String?): List<Symptom> =
        if (data.isNullOrBlank()) emptyList()
        else data.split(",").mapNotNull { name ->
            runCatching { Symptom.valueOf(name.trim()) }.getOrNull()
        }

    // ── FlowIntensity ↔ String ────────────────────────────────

    @TypeConverter
    fun fromFlowIntensity(value: FlowIntensity?): String? = value?.name

    @TypeConverter
    fun toFlowIntensity(value: String?): FlowIntensity? =
        value?.let { runCatching { FlowIntensity.valueOf(it) }.getOrNull() }

    // ── Mood ↔ String ─────────────────────────────────────────

    @TypeConverter
    fun fromMood(value: Mood?): String? = value?.name

    @TypeConverter
    fun toMood(value: String?): Mood? =
        value?.let { runCatching { Mood.valueOf(it) }.getOrNull() }

    // ── SexualActivity ↔ String ───────────────────────────────

    @TypeConverter
    fun fromSexualActivity(value: SexualActivity?): String? = value?.name

    @TypeConverter
    fun toSexualActivity(value: String?): SexualActivity? =
        value?.let { runCatching { SexualActivity.valueOf(it) }.getOrNull() }

    // ── CervicalMucus ↔ String ────────────────────────────────

    @TypeConverter
    fun fromCervicalMucus(value: CervicalMucus?): String? = value?.name

    @TypeConverter
    fun toCervicalMucus(value: String?): CervicalMucus? =
        value?.let { runCatching { CervicalMucus.valueOf(it) }.getOrNull() }
}
