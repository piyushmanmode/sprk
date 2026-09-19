package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "General",
    val targetDays: Int = 60,
    val startDateMillis: Long = System.currentTimeMillis(),
    val endDateMillis: Long? = null,
    val reminderFrequency: String = "Daily",
    val reminderTime: String = "10:00 AM",
    val timeSpentMinutes: Int = 0,
    val colorHex: String = "#FF5722",
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "habit_completions",
    indices = [androidx.room.Index(value = ["habitId", "dateEpochDay"], unique = true)]
)
data class HabitCompletion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val dateEpochDay: Long,
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "trophies")
data class Trophy(
    @PrimaryKey
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val requiredDays: Int,
    val habitTitle: String? = null,
    val isUnlocked: Boolean = false,
    val unlockedAtMillis: Long? = null,
    val iconType: String = "wreath_star" // "wreath_star", "medal", "cup", "flame_master"
)

data class HabitWithStats(
    val habit: Habit,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalCompletedDays: Int,
    val isCompletedToday: Boolean,
    val recentCompletions: Set<Long> // Set of dateEpochDay
)
