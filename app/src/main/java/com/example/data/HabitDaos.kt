package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.Habit
import com.example.model.HabitCompletion
import com.example.model.Trophy
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY createdAtMillis ASC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabitById(id: Long): Flow<Habit?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabitById(id: Long)

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId")
    fun getCompletionsForHabit(habitId: Long): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions")
    fun getAllCompletions(): Flow<List<HabitCompletion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletion): Long

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND dateEpochDay = :dateEpochDay")
    suspend fun deleteCompletion(habitId: Long, dateEpochDay: Long)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId")
    suspend fun deleteAllCompletionsForHabit(habitId: Long)

    @Query("DELETE FROM habit_completions")
    suspend fun deleteAllCompletions()

    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()

    @Query("SELECT COUNT(*) FROM habit_completions WHERE habitId = :habitId AND dateEpochDay = :dateEpochDay")
    suspend fun countCompletion(habitId: Long, dateEpochDay: Long): Int
}

@Dao
interface TrophyDao {
    @Query("SELECT * FROM trophies")
    fun getAllTrophies(): Flow<List<Trophy>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trophies: List<Trophy>)

    @Update
    suspend fun updateTrophy(trophy: Trophy)

    @Query("UPDATE trophies SET isUnlocked = 1, unlockedAtMillis = :timestamp WHERE id = :id")
    suspend fun unlockTrophy(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE trophies SET isUnlocked = 0, unlockedAtMillis = NULL")
    suspend fun lockAllTrophies()

    @Query("DELETE FROM trophies")
    suspend fun deleteAllTrophies()
}
