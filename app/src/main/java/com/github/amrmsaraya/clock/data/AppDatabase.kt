package com.github.amrmsaraya.clock.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.amrmsaraya.clock.feature_alarm.data.local.AlarmDao
import com.github.amrmsaraya.clock.feature_alarm.data.model.AlarmDTO
import com.github.amrmsaraya.clock.feature_clock.data.local.ClockDao
import com.github.amrmsaraya.clock.feature_clock.data.model.ClockDTO
import com.github.amrmsaraya.clock.feature_timer.data.local.TimerDao
import com.github.amrmsaraya.clock.feature_timer.data.model.TimerDTO

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
