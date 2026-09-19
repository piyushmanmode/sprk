package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.Habit
import com.example.model.HabitCompletion
import com.example.model.Trophy

@Database(
    entities = [Habit::class, HabitCompletion::class, Trophy::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun trophyDao(): TrophyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "spark_habits.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
