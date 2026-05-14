package com.bloomcycle.app.domain.model

import java.time.LocalDate

/**
 * A user's daily health log entry with all tracked metrics.
 */
data class DailyLog(
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
)
