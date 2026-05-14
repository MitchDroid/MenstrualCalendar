package com.bloomcycle.app.domain.model

import java.time.LocalDate

/**
 * Represents a single menstrual cycle from period start to the day
 * before the next period begins.
 */
data class Cycle(
    val id: Long = 0,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val periodEndDate: LocalDate? = null,
    val cycleLength: Int? = null,
    val periodLength: Int? = null,
    val notes: String? = null
)
