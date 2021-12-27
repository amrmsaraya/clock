package com.github.amrmsaraya.clock.feature_timer.di

import com.github.amrmsaraya.clock.feature_timer.domain.repository.TimerRepo
import com.github.amrmsaraya.clock.feature_timer.domain.usecase.TimerCRUDUseCase
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
    fun provideTimerCRUDUseCase(timerRepo: TimerRepo): TimerCRUDUseCase {
        return TimerCRUDUseCase(timerRepo)
    }
}