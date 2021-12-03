package com.github.amrmsaraya.clock.di

import android.content.Context
import com.github.amrmsaraya.clock.data.local.AppDatabase
import com.github.amrmsaraya.clock.data.local.ClockDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Room {

    @Singleton
    @Provides
    fun provideClockDao(@ApplicationContext context: Context): ClockDao {
        return AppDatabase.getDatabase(context).clockDao()
    }
}
