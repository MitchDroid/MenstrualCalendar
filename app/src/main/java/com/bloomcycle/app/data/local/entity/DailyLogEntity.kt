package com.bloomcycle.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bloomcycle.app.domain.model.CervicalMucus
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.SexualActivity
import com.bloomcycle.app.domain.model.Symptom
import java.time.LocalDate

@Entity(
    tableName = "daily_logs",
    indices = [Index(value = ["date"], unique = true)]
)
data class DailyLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val flowIntensity: FlowIntensity? = null,
    val mood: Mood? = null,
    val symptoms: List<Symptom> = emptyList(),
    val sexualActivity: SexualActivity? = null,
    val notes: String? = null,
    val weight: Float? = null,
    val temperature: Float? = null,
    val cervicalMucus: CervicalMucus? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): DailyLog = DailyLog(
        id = id,
        date = date,
        flowIntensity = flowIntensity,
        mood = mood,
        symptoms = symptoms,
        sexualActivity = sexualActivity,
        notes = notes,
        weight = weight,
        temperature = temperature,
        cervicalMucus = cervicalMucus,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(log: DailyLog): DailyLogEntity = DailyLogEntity(
            id = log.id,
            date = log.date,
            flowIntensity = log.flowIntensity,
            mood = log.mood,
            symptoms = log.symptoms,
            sexualActivity = log.sexualActivity,
            notes = log.notes,
            weight = log.weight,
            temperature = log.temperature,
            cervicalMucus = log.cervicalMucus,
            createdAt = log.createdAt,
            updatedAt = log.updatedAt
        )
    }
}
