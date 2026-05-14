package com.bloomcycle.app.di

import com.bloomcycle.app.data.repository.CycleRepositoryImpl
import com.bloomcycle.app.data.repository.DailyLogRepositoryImpl
import com.bloomcycle.app.domain.repository.CycleRepository
import com.bloomcycle.app.domain.repository.DailyLogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCycleRepository(impl: CycleRepositoryImpl): CycleRepository

    @Binds
    @Singleton
    abstract fun bindDailyLogRepository(impl: DailyLogRepositoryImpl): DailyLogRepository
}
