package com.bloomcycle.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bloomcycle.app.data.local.converter.Converters
import com.bloomcycle.app.data.local.dao.CycleDao
import com.bloomcycle.app.data.local.dao.DailyLogDao
import com.bloomcycle.app.data.local.entity.CycleEntity
import com.bloomcycle.app.data.local.entity.DailyLogEntity

/**
 * BloomCycle Room database.
 *
 * ## Migration guide
 *
 * When changing the schema:
 * 1. Bump [version] (e.g., 1 → 2).
 * 2. For simple additive changes (new column with default, new table),
 *    add an `@AutoMigration(from = X, to = Y)` entry below.
 * 3. For complex changes (column renames, type changes, data transforms),
 *    write a manual [androidx.room.migration.Migration] and register it
 *    in [com.bloomcycle.app.di.DatabaseModule.provideDatabase].
 * 4. Build — Room generates the migration from the exported schemas
 *    in `app/schemas/`.
 *
 * NEVER use `fallbackToDestructiveMigration()` — it silently deletes
 * all user data when the schema version changes.
 */
@Database(
    entities = [
        CycleEntity::class,
        DailyLogEntity::class
    ],
    version = 1,
    exportSchema = true
    // When bumping to version 2, add:
    // autoMigrations = [AutoMigration(from = 1, to = 2)]
)
@TypeConverters(Converters::class)
abstract class BloomCycleDatabase : RoomDatabase() {
    abstract fun cycleDao(): CycleDao
    abstract fun dailyLogDao(): DailyLogDao
}
