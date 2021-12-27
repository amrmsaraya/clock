package com.github.amrmsaraya.clock.feature_alarm.di

import com.github.amrmsaraya.clock.feature_alarm.domain.repository.AlarmRepo
import com.github.amrmsaraya.clock.feature_alarm.domain.usecase.AlarmCRUDUseCase
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
    fun provideAlarmCRUDUseCase(alarmRepo: AlarmRepo): AlarmCRUDUseCase {
        return AlarmCRUDUseCase(alarmRepo)
    }

}