package com.bloomcycle.app.domain.repository

import com.bloomcycle.app.domain.model.DailyLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface DailyLogRepository {

    fun getAllLogs(): Flow<List<DailyLog>>

    fun getLogByDate(date: LocalDate): Flow<DailyLog?>

    fun getLogsBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyLog>>

    suspend fun insertLog(log: DailyLog): Long

    suspend fun updateLog(log: DailyLog)

    suspend fun deleteLog(log: DailyLog)

    suspend fun upsertLog(log: DailyLog): Long
}
