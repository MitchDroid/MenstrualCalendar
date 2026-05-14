package com.bloomcycle.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bloomcycle.app.domain.model.Cycle
import java.time.LocalDate

@Entity(tableName = "cycles")
data class CycleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val periodEndDate: LocalDate? = null,
    val cycleLength: Int? = null,
    val periodLength: Int? = null,
    val notes: String? = null
) {
    fun toDomain(): Cycle = Cycle(
        id = id,
        startDate = startDate,
        endDate = endDate,
        periodEndDate = periodEndDate,
        cycleLength = cycleLength,
        periodLength = periodLength,
        notes = notes
    )

    companion object {
        fun fromDomain(cycle: Cycle): CycleEntity = CycleEntity(
            id = cycle.id,
            startDate = cycle.startDate,
            endDate = cycle.endDate,
            periodEndDate = cycle.periodEndDate,
            cycleLength = cycle.cycleLength,
            periodLength = cycle.periodLength,
            notes = cycle.notes
        )
    }
}
