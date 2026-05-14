package com.bloomcycle.app.data.repository

import com.bloomcycle.app.data.local.dao.DailyLogDao
import com.bloomcycle.app.data.local.entity.DailyLogEntity
import com.bloomcycle.app.domain.model.DailyLog
import com.bloomcycle.app.domain.repository.DailyLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyLogRepositoryImpl @Inject constructor(
    private val dailyLogDao: DailyLogDao
) : DailyLogRepository {

    override fun getAllLogs(): Flow<List<DailyLog>> =
        dailyLogDao.getAllLogs().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getLogByDate(date: LocalDate): Flow<DailyLog?> =
        dailyLogDao.getLogByDate(date).map { it?.toDomain() }

    override fun getLogsBetweenDates(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<DailyLog>> =
        dailyLogDao.getLogsBetweenDates(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun insertLog(log: DailyLog): Long =
        dailyLogDao.insertLog(DailyLogEntity.fromDomain(log))

    override suspend fun updateLog(log: DailyLog) =
        dailyLogDao.updateLog(DailyLogEntity.fromDomain(log))

    override suspend fun deleteLog(log: DailyLog) =
        dailyLogDao.deleteLog(DailyLogEntity.fromDomain(log))

    override suspend fun upsertLog(log: DailyLog): Long =
        dailyLogDao.upsertLog(DailyLogEntity.fromDomain(log))
}
