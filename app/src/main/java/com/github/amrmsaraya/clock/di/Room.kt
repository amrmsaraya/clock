package com.github.amrmsaraya.clock.di

import android.content.Context
import com.github.amrmsaraya.clock.data.AppDatabase
import com.github.amrmsaraya.clock.feature_alarm.data.local.AlarmDao
import com.github.amrmsaraya.clock.feature_clock.data.local.ClockDao
import com.github.amrmsaraya.clock.feature_timer.data.local.TimerDao
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
