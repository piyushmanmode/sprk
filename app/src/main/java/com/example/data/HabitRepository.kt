package com.example.data

import com.example.model.Habit
import com.example.model.HabitCompletion
import com.example.model.HabitWithStats
import com.example.model.Trophy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneId

class HabitRepository(
    private val habitDao: HabitDao,
    private val trophyDao: TrophyDao
) {
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()
    val allCompletions: Flow<List<HabitCompletion>> = habitDao.getAllCompletions()
    val allTrophies: Flow<List<Trophy>> = trophyDao.getAllTrophies()

    val habitsWithStats: Flow<List<HabitWithStats>> = combine(
        allHabits,
        allCompletions
    ) { habits, completions ->
        val todayEpochDay = LocalDate.now(ZoneId.systemDefault()).toEpochDay()
        val completionsByHabit = completions.groupBy { it.habitId }

        habits.map { habit ->
            val habitComps = completionsByHabit[habit.id]?.map { it.dateEpochDay }?.toSet() ?: emptySet()
            val (currentStreak, longestStreak) = calculateStreaks(habitComps, todayEpochDay)
            val isCompletedToday = habitComps.contains(todayEpochDay)

            HabitWithStats(
                habit = habit,
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                totalCompletedDays = habitComps.size,
                isCompletedToday = isCompletedToday,
                recentCompletions = habitComps
            )
        }
    }

    suspend fun toggleHabitCompletionToday(habitId: Long): Boolean {
        val todayEpochDay = LocalDate.now(ZoneId.systemDefault()).toEpochDay()
        val isCompleted = habitDao.countCompletion(habitId, todayEpochDay) > 0

        if (isCompleted) {
            habitDao.deleteCompletion(habitId, todayEpochDay)
            return false
        } else {
            habitDao.insertCompletion(
                HabitCompletion(
                    habitId = habitId,
                    dateEpochDay = todayEpochDay
                )
            )
            checkAndUnlockTrophies()
            return true
        }
    }

    suspend fun checkAllCompletedToday(): Boolean {
        val habits = habitDao.getAllHabits().first()
        if (habits.isEmpty()) return false
        val todayEpochDay = LocalDate.now(ZoneId.systemDefault()).toEpochDay()
        val completions = habitDao.getAllCompletions().first().filter { it.dateEpochDay == todayEpochDay }
        val completedHabitIds = completions.map { it.habitId }.toSet()
        return habits.all { completedHabitIds.contains(it.id) }
    }

    suspend fun createHabit(habit: Habit): Long {
        return habitDao.insertHabit(habit)
    }

    suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteAllCompletionsForHabit(habitId)
        habitDao.deleteHabitById(habitId)
    }

    suspend fun unlockTrophy(trophyId: String) {
        trophyDao.unlockTrophy(trophyId)
    }

    private suspend fun checkAndUnlockTrophies() {
        val habitsWithStatsList = habitsWithStats.first()
        val maxStreak = habitsWithStatsList.maxOfOrNull { it.currentStreak } ?: 0
        val maxLongestStreak = habitsWithStatsList.maxOfOrNull { it.longestStreak } ?: 0
        val bestStreak = maxOf(maxStreak, maxLongestStreak)

        if (bestStreak >= 7) {
            trophyDao.unlockTrophy("streak_7")
        }
        if (bestStreak >= 15) {
            trophyDao.unlockTrophy("streak_15")
        }
        if (bestStreak >= 30) {
            trophyDao.unlockTrophy("streak_30")
        }
        if (bestStreak >= 60) {
            trophyDao.unlockTrophy("streak_60")
        }
    }

    suspend fun clearAllData() {
        habitDao.deleteAllHabits()
        habitDao.deleteAllCompletions()
        trophyDao.lockAllTrophies()
    }

    suspend fun seedInitialDataIfEmpty() {
        val existingTrophies = trophyDao.getAllTrophies().first()
        if (existingTrophies.isEmpty()) {
            val trophies = listOf(
                Trophy(
                    id = "streak_7",
                    title = "07 days Streaks",
                    subtitle = "7-Day Milestone Trophy",
                    description = "Keep any habit streak alive for 7 consecutive days to unlock",
                    requiredDays = 7,
                    habitTitle = "7-Day Streak Goal",
                    isUnlocked = false,
                    unlockedAtMillis = null,
                    iconType = "wreath_star"
                ),
                Trophy(
                    id = "streak_15",
                    title = "15 days Streaks",
                    subtitle = "15-Day Milestone Trophy",
                    description = "Maintain unwavering consistency for 15 full days to unlock",
                    requiredDays = 15,
                    habitTitle = "15-Day Streak Goal",
                    isUnlocked = false,
                    unlockedAtMillis = null,
                    iconType = "medal"
                ),
                Trophy(
                    id = "streak_30",
                    title = "30 days Streaks",
                    subtitle = "Monthly Flame Champion",
                    description = "Achieve a monumental 30-day streak unbroken to unlock",
                    requiredDays = 30,
                    habitTitle = "30-Day Streak Goal",
                    isUnlocked = false,
                    unlockedAtMillis = null,
                    iconType = "cup"
                ),
                Trophy(
                    id = "streak_60",
                    title = "60 days Grandmaster",
                    subtitle = "60-Day Habit Achiever",
                    description = "Complete an entire 60-day streak journey to true mastery to unlock",
                    requiredDays = 60,
                    habitTitle = "60-Day Streak Goal",
                    isUnlocked = false,
                    unlockedAtMillis = null,
                    iconType = "wreath_star"
                )
            )
            trophyDao.insertAll(trophies)
        }
    }

    private fun calculateStreaks(completions: Set<Long>, todayEpochDay: Long): Pair<Int, Int> {
        if (completions.isEmpty()) return Pair(0, 0)

        // Current streak: counts backwards from today (or yesterday if today isn't done yet)
        var checkDay = if (completions.contains(todayEpochDay)) todayEpochDay else todayEpochDay - 1
        var currentStreak = 0
        while (completions.contains(checkDay)) {
            currentStreak++
            checkDay--
        }

        // Longest streak
        val sortedDays = completions.sorted()
        var longestStreak = 0
        var currentRun = 0
        var previousDay: Long? = null

        for (day in sortedDays) {
            if (previousDay == null) {
                currentRun = 1
            } else if (day == previousDay + 1) {
                currentRun++
            } else if (day > previousDay + 1) {
                currentRun = 1
            }
            if (currentRun > longestStreak) {
                longestStreak = currentRun
            }
            previousDay = day
        }

        return Pair(currentStreak, maxOf(longestStreak, currentStreak))
    }
}
