package com.bloomcycle.app.data.repository

import com.bloomcycle.app.data.local.dao.CycleDao
import com.bloomcycle.app.data.local.entity.CycleEntity
import com.bloomcycle.app.domain.model.Cycle
import com.bloomcycle.app.domain.repository.CycleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CycleRepositoryImpl @Inject constructor(
    private val cycleDao: CycleDao
) : CycleRepository {

    override fun getAllCycles(): Flow<List<Cycle>> =
        cycleDao.getAllCycles().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getCurrentCycle(): Flow<Cycle?> =
        cycleDao.getMostRecentCycle().map { it?.toDomain() }

    override fun getCycleById(id: Long): Flow<Cycle?> =
        cycleDao.getCycleById(id).map { it?.toDomain() }

    override fun getCyclesAfterDate(date: LocalDate): Flow<List<Cycle>> =
        cycleDao.getCyclesAfterDate(date).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun insertCycle(cycle: Cycle): Long =
        cycleDao.insertCycle(CycleEntity.fromDomain(cycle))

    override suspend fun updateCycle(cycle: Cycle) =
        cycleDao.updateCycle(CycleEntity.fromDomain(cycle))

    override suspend fun deleteCycle(cycle: Cycle) =
        cycleDao.deleteCycle(CycleEntity.fromDomain(cycle))
}
