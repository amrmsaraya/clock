package com.github.amrmsaraya.clock.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.amrmsaraya.clock.data.model.AlarmDTO
import com.github.amrmsaraya.clock.data.model.ClockDTO
import com.github.amrmsaraya.clock.data.model.TimerDTO

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
