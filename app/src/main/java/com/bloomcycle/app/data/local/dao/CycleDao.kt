package com.bloomcycle.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bloomcycle.app.data.local.entity.CycleEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface CycleDao {

    @Query("SELECT * FROM cycles ORDER BY startDate DESC")
    fun getAllCycles(): Flow<List<CycleEntity>>

    @Query("SELECT * FROM cycles ORDER BY startDate DESC LIMIT 1")
    fun getMostRecentCycle(): Flow<CycleEntity?>

    @Query("SELECT * FROM cycles WHERE id = :id")
    fun getCycleById(id: Long): Flow<CycleEntity?>

    @Query("SELECT * FROM cycles WHERE startDate >= :date ORDER BY startDate ASC")
    fun getCyclesAfterDate(date: LocalDate): Flow<List<CycleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycle(cycle: CycleEntity): Long

    @Update
    suspend fun updateCycle(cycle: CycleEntity)

    @Delete
    suspend fun deleteCycle(cycle: CycleEntity)
}
