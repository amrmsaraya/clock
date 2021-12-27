package com.github.amrmsaraya.clock.feature_alarm.di

import com.github.amrmsaraya.clock.feature_alarm.data.repoImpl.AlarmRepoImpl
import com.github.amrmsaraya.clock.feature_alarm.data.source.LocalDataSource
import com.github.amrmsaraya.clock.feature_alarm.domain.repository.AlarmRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Repository {

    @Provides
    @Singleton
    fun provideAlarmRepo(localDataSource: LocalDataSource): AlarmRepo {
        return AlarmRepoImpl(localDataSource)
    }

}
