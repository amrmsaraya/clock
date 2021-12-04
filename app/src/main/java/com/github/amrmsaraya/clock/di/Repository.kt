package com.github.amrmsaraya.clock.di

import com.github.amrmsaraya.clock.data.repositoryImpl.AlarmRepoImpl
import com.github.amrmsaraya.clock.data.repositoryImpl.ClockRepoImpl
import com.github.amrmsaraya.clock.data.source.AlarmLocalDataSource
import com.github.amrmsaraya.clock.data.source.ClockLocalDataSource
import com.github.amrmsaraya.clock.domain.repository.AlarmRepo
import com.github.amrmsaraya.clock.domain.repository.ClockRepo
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
    fun provideClockRepo(localDataSource: ClockLocalDataSource): ClockRepo {
        return ClockRepoImpl(localDataSource)
    }

    @Provides
    @Singleton
    fun provideAlarmRepo(localDataSource: AlarmLocalDataSource): AlarmRepo {
        return AlarmRepoImpl(localDataSource)
    }

}
