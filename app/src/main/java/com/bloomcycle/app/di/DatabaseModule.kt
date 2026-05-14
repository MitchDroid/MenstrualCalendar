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
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCycleDao(database: BloomCycleDatabase): CycleDao =
        database.cycleDao()

    @Provides
    fun provideDailyLogDao(database: BloomCycleDatabase): DailyLogDao =
        database.dailyLogDao()
}
