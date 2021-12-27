package com.github.amrmsaraya.clock.feature_timer.di

import com.github.amrmsaraya.clock.feature_timer.data.repositoryImpl.TimerRepoImpl
import com.github.amrmsaraya.clock.feature_timer.data.source.LocalDataSource
import com.github.amrmsaraya.clock.feature_timer.domain.repository.TimerRepo
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
    fun provideTimerRepo(localDataSource: LocalDataSource): TimerRepo {
        return TimerRepoImpl(localDataSource)
    }

}
