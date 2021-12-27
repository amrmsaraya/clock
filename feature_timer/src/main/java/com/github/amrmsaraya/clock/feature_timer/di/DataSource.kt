package com.github.amrmsaraya.clock.feature_timer.di

import com.github.amrmsaraya.clock.feature_timer.data.local.TimerDao
import com.github.amrmsaraya.clock.feature_timer.data.source.LocalDataSource
import com.github.amrmsaraya.clock.feature_timer.data.sourceImpl.LocalDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSource {

    @Provides
    @Singleton
    fun provideTimerLocalDataSource(timerDao: TimerDao): LocalDataSource {
        return LocalDataSourceImpl(timerDao)
    }

}
