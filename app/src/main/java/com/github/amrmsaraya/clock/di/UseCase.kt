package com.github.amrmsaraya.clock.di

import com.github.amrmsaraya.clock.domain.repository.AlarmRepo
import com.github.amrmsaraya.clock.domain.repository.ClockRepo
import com.github.amrmsaraya.clock.domain.repository.TimerRepo
import com.github.amrmsaraya.clock.domain.usecase.AlarmCRUDUseCase
import com.github.amrmsaraya.clock.domain.usecase.ClockCRUDUseCase
import com.github.amrmsaraya.clock.domain.usecase.TimerCRUDUseCase
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

    @Singleton
    @Provides
    fun provideAlarmCRUDUseCase(alarmRepo: AlarmRepo): AlarmCRUDUseCase {
        return AlarmCRUDUseCase(alarmRepo)
    }

    @Singleton
    @Provides
    fun provideTimerCRUDUseCase(timerRepo: TimerRepo): TimerCRUDUseCase {
        return TimerCRUDUseCase(timerRepo)
    }
}