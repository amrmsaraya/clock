package com.github.amrmsaraya.clock.feature_clock.di

import com.github.amrmsaraya.clock.database.feature.clock.dao.ClockDao
import com.github.amrmsaraya.clock.feature_clock.data.source.LocalDataSource
import com.github.amrmsaraya.clock.feature_clock.data.sourceImpl.LocalDataSourceImpl
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
    fun provideClockLocalDataSource(clockDao: ClockDao): LocalDataSource {
        return LocalDataSourceImpl(clockDao)
    }

}
