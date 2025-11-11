package com.example.myapplication.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context

@Database(
    entities = [Habit::class, HabitCompletion::class, Notification::class, UserProfile::class],
    version = 10,
    exportSchema = true
)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    
    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null
        
        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_database"
                )
                .fallbackToDestructiveMigration() // Упрощаем - просто пересоздаем базу
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
