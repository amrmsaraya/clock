package com.github.amrmsaraya.clock.feature_alarm.di

import com.github.amrmsaraya.clock.database.feature.alarm.dao.AlarmDao
import com.github.amrmsaraya.clock.feature_alarm.data.source.LocalDataSource
import com.github.amrmsaraya.clock.feature_alarm.data.sourceImpl.LocalDataSourceImpl
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
    fun provideAlarmLocalDataSource(alarmDao: AlarmDao): LocalDataSource {
        return LocalDataSourceImpl(alarmDao)
    }

}
