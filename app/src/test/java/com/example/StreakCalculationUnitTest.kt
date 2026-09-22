package com.example

import com.example.data.HabitDao
import com.example.data.HabitRepository
import com.example.data.TrophyDao
import com.example.model.Habit
import com.example.model.HabitCompletion
import com.example.model.Trophy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertEquals
import org.junit.Test

class StreakCalculationUnitTest {

    private val fakeHabitDao = object : HabitDao {
        override fun getAllHabits(): Flow<List<Habit>> = emptyFlow()
        override fun getHabitById(id: Long): Flow<Habit?> = emptyFlow()
        override suspend fun insertHabit(habit: Habit): Long = 0L
        override suspend fun updateHabit(habit: Habit) {}
        override suspend fun deleteHabit(habit: Habit) {}
        override suspend fun deleteHabitById(id: Long) {}
        override fun getCompletionsForHabit(habitId: Long): Flow<List<HabitCompletion>> = emptyFlow()
        override fun getAllCompletions(): Flow<List<HabitCompletion>> = emptyFlow()
        override suspend fun insertCompletion(completion: HabitCompletion): Long = 0L
        override suspend fun deleteCompletion(habitId: Long, dateEpochDay: Long) {}
        override suspend fun deleteAllCompletionsForHabit(habitId: Long) {}
        override suspend fun deleteAllCompletions() {}
        override suspend fun deleteAllHabits() {}
        override suspend fun countCompletion(habitId: Long, dateEpochDay: Long): Int = 0
    }

    private val fakeTrophyDao = object : TrophyDao {
        override fun getAllTrophies(): Flow<List<Trophy>> = emptyFlow()
        override suspend fun insertAll(trophies: List<Trophy>) {}
        override suspend fun updateTrophy(trophy: Trophy) {}
        override suspend fun unlockTrophy(id: String, timestamp: Long) {}
        override suspend fun lockAllTrophies() {}
        override suspend fun deleteAllTrophies() {}
    }

    private val repository = HabitRepository(fakeHabitDao, fakeTrophyDao)

    @Test
    fun emptyCompletions_returnsZeroStreaks() {
        val (current, longest) = repository.calculateStreaks(emptySet(), 20000L)
        assertEquals(0, current)
        assertEquals(0, longest)
    }

    @Test
    fun completedToday_calculatesCurrentAndLongestStreak() {
        val today = 20000L
        val completions = setOf(today - 2, today - 1, today)
        val (current, longest) = repository.calculateStreaks(completions, today)
        assertEquals(3, current)
        assertEquals(3, longest)
    }

    @Test
    fun completedYesterdayNotToday_gracePeriodKeepsStreakActive() {
        val today = 20000L
        // Crossing midnight: yesterday completed, today not yet completed
        val completions = setOf(today - 3, today - 2, today - 1)
        val (current, longest) = repository.calculateStreaks(completions, today)
        assertEquals(3, current)
        assertEquals(3, longest)
    }

    @Test
    fun missedYesterdayAndToday_streakResetsToZero() {
        val today = 20000L
        // Completed 2 days ago and 3 days ago, but missed yesterday and today
        val completions = setOf(today - 3, today - 2)
        val (current, longest) = repository.calculateStreaks(completions, today)
        assertEquals(0, current)
        assertEquals(2, longest)
    }

    @Test
    fun skippedDayInPast_longestStreakCapturesPeak() {
        val today = 20000L
        // 5 consecutive days in past, then a 2-day gap, then 2 consecutive days up to today
        val completions = setOf(
            today - 10, today - 9, today - 8, today - 7, today - 6,
            today - 1, today
        )
        val (current, longest) = repository.calculateStreaks(completions, today)
        assertEquals(2, current)
        assertEquals(5, longest)
    }

    @Test
    fun futureCompletionFromTimezoneShift_countedContinuously() {
        val today = 20000L
        // Completed yesterday, today, and tomorrow (due to travel/timezone)
        val completions = setOf(today - 1, today, today + 1)
        val (current, longest) = repository.calculateStreaks(completions, today)
        assertEquals(3, current)
        assertEquals(3, longest)
    }
}
