package com.bloomcycle.app.di

import android.content.Context
import androidx.room.Room
import com.bloomcycle.app.data.local.BloomCycleDatabase
import com.bloomcycle.app.data.local.dao.CycleDao
import com.bloomcycle.app.data.local.dao.DailyLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BloomCycleDatabase =
        Room.databaseBuilder(
            context,
            BloomCycleDatabase::class.java,
            "bloomcycle_database"
        )
            // When adding new schema versions, register migrations here:
            // .addMigrations(MIGRATION_1_2, MIGRATION_2_3, ...)
            // Room's AutoMigration (annotated on the Database class) handles
            // simple additive changes automatically. Use manual Migration
            // objects only for complex transformations (column renames,
            // data conversions, index changes, etc.).
            //
            // NEVER use fallbackToDestructiveMigration() in production —
            // it silently wipes all user data on schema changes.
            .build()

    @Provides
    fun provideCycleDao(database: BloomCycleDatabase): CycleDao =
        database.cycleDao()

    @Provides
    fun provideDailyLogDao(database: BloomCycleDatabase): DailyLogDao =
        database.dailyLogDao()
}
