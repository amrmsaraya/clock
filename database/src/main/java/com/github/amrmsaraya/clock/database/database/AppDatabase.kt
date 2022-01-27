package com.github.amrmsaraya.clock.database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.amrmsaraya.clock.database.converter.Converter
import com.github.amrmsaraya.clock.database.feature.alarm.dao.AlarmDao
import com.github.amrmsaraya.clock.database.feature.alarm.model.AlarmDTO
import com.github.amrmsaraya.clock.database.feature.clock.dao.ClockDao
import com.github.amrmsaraya.clock.database.feature.clock.model.ClockDTO
import com.github.amrmsaraya.clock.database.feature.timer.dao.TimerDao
import com.github.amrmsaraya.clock.database.feature.timer.model.TimerDTO

@Database(
    entities = [ClockDTO::class, AlarmDTO::class, TimerDTO::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clockDao(): ClockDao
    abstract fun alarmDao(): AlarmDao
    abstract fun timerDao(): TimerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}
