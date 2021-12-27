package com.github.amrmsaraya.clock.feature_clock.di

import com.github.amrmsaraya.clock.feature_clock.domain.repository.ClockRepo
import com.github.amrmsaraya.clock.feature_clock.domain.usecase.ClockCRUDUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCase {

    @Singleton
    @Provides
    fun provideClockCRUDUseCase(clockRepo: ClockRepo): ClockCRUDUseCase {
        return ClockCRUDUseCase(clockRepo)
    }

}