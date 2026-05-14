package com.bloomcycle.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bloomcycle.app.data.local.converter.Converters
import com.bloomcycle.app.data.local.dao.CycleDao
import com.bloomcycle.app.data.local.dao.DailyLogDao
import com.bloomcycle.app.data.local.entity.CycleEntity
import com.bloomcycle.app.data.local.entity.DailyLogEntity

@Database(
    entities = [
        CycleEntity::class,
        DailyLogEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class BloomCycleDatabase : RoomDatabase() {
    abstract fun cycleDao(): CycleDao
    abstract fun dailyLogDao(): DailyLogDao
}
