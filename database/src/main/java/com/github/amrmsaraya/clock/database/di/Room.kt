package com.github.amrmsaraya.clock.database.di

import android.content.Context
import com.github.amrmsaraya.clock.database.database.AppDatabase
import com.github.amrmsaraya.clock.database.feature.alarm.dao.AlarmDao
import com.github.amrmsaraya.clock.database.feature.clock.dao.ClockDao
import com.github.amrmsaraya.clock.database.feature.timer.dao.TimerDao
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

    @Singleton
    @Provides
    fun provideAlarmDao(@ApplicationContext context: Context): AlarmDao {
        return AppDatabase.getDatabase(context).alarmDao()
    }

    @Singleton
    @Provides
    fun provideTimerDao(@ApplicationContext context: Context): TimerDao {
        return AppDatabase.getDatabase(context).timerDao()
    }
}
