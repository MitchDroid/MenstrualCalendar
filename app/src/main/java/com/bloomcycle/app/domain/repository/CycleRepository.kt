package com.bloomcycle.app.domain.repository

import com.bloomcycle.app.domain.model.Cycle
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CycleRepository {

    fun getAllCycles(): Flow<List<Cycle>>

    fun getCurrentCycle(): Flow<Cycle?>

    fun getCycleById(id: Long): Flow<Cycle?>

    fun getCyclesAfterDate(date: LocalDate): Flow<List<Cycle>>

    suspend fun insertCycle(cycle: Cycle): Long

    suspend fun updateCycle(cycle: Cycle)

    suspend fun deleteCycle(cycle: Cycle)
}
