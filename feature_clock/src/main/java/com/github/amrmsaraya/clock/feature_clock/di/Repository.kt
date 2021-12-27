package com.github.amrmsaraya.clock.feature_clock.di

import com.github.amrmsaraya.clock.feature_clock.data.repositoryImpl.ClockRepoImpl
import com.github.amrmsaraya.clock.feature_clock.data.source.LocalDataSource
import com.github.amrmsaraya.clock.feature_clock.domain.repository.ClockRepo
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
    fun provideClockRepo(localDataSource: LocalDataSource): ClockRepo {
        return ClockRepoImpl(localDataSource)
    }

}
