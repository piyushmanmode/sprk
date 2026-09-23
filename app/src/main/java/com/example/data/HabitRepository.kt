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
        return toggleHabitCompletionForDate(habitId, todayEpochDay)
    }

    suspend fun toggleHabitCompletionForDate(habitId: Long, dateEpochDay: Long): Boolean {
        val isCompleted = habitDao.countCompletion(habitId, dateEpochDay) > 0

        if (isCompleted) {
            habitDao.deleteCompletion(habitId, dateEpochDay)
            return false
        } else {
            habitDao.insertCompletion(
                HabitCompletion(
                    habitId = habitId,
                    dateEpochDay = dateEpochDay
                )
            )
            checkAndUnlockTrophies()
            return true
        }
    }

    suspend fun checkAllCompletedToday(): Boolean {
        val todayEpochDay = LocalDate.now(ZoneId.systemDefault()).toEpochDay()
        return checkAllCompletedForDate(todayEpochDay)
    }

    suspend fun checkAllCompletedForDate(dateEpochDay: Long): Boolean {
        val habits = habitDao.getAllHabits().first()
        if (habits.isEmpty()) return false
        val completions = habitDao.getAllCompletions().first().filter { it.dateEpochDay == dateEpochDay }
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

    suspend fun checkAndUnlockTrophies() {
        val habitsWithStatsList = habitsWithStats.first()
        val maxStreak = habitsWithStatsList.maxOfOrNull { it.currentStreak } ?: 0
        val maxLongestStreak = habitsWithStatsList.maxOfOrNull { it.longestStreak } ?: 0
        val bestStreak = maxOf(maxStreak, maxLongestStreak)

        // Global streak milestones
        if (bestStreak >= 3) {
            trophyDao.unlockTrophy("streak_3")
        }
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

        // Habit-specific streak milestones
        habitsWithStatsList.forEach { habitWithStats ->
            val habit = habitWithStats.habit
            val streak = maxOf(habitWithStats.currentStreak, habitWithStats.longestStreak)
            if (streak >= 3) {
                trophyDao.unlockTrophy("habit_${habit.id}_3")
            }
            if (streak >= 7) {
                trophyDao.unlockTrophy("habit_${habit.id}_7")
            }
            if (streak >= 14) {
                trophyDao.unlockTrophy("habit_${habit.id}_14")
            }
            if (streak >= 30) {
                trophyDao.unlockTrophy("habit_${habit.id}_30")
            }
        }
    }

    suspend fun ensureTrophiesForHabits(habits: List<HabitWithStats>) {
        if (habits.isEmpty()) return
        val currentTrophies = trophyDao.getAllTrophies().first()
        val currentIds = currentTrophies.map { it.id }.toSet()
        val newTrophies = mutableListOf<Trophy>()

        habits.forEach { habitWithStats ->
            val habit = habitWithStats.habit
            val streak = maxOf(habitWithStats.currentStreak, habitWithStats.longestStreak)

            val milestones = listOf(
                Triple(3, "3-Day Spark", "wreath_star"),
                Triple(7, "7-Day Flame", "medal"),
                Triple(14, "14-Day Blaze", "cup"),
                Triple(30, "30-Day Master", "wreath_star")
            )

            milestones.forEach { (days, title, iconType) ->
                val id = "habit_${habit.id}_$days"
                if (!currentIds.contains(id)) {
                    val isUnlocked = streak >= days
                    newTrophies.add(
                        Trophy(
                            id = id,
                            title = title,
                            subtitle = "${habit.title} Milestone",
                            description = "Reach a $days-day streak in ${habit.title}",
                            requiredDays = days,
                            habitTitle = habit.title,
                            isUnlocked = isUnlocked,
                            unlockedAtMillis = if (isUnlocked) System.currentTimeMillis() else null,
                            iconType = iconType
                        )
                    )
                }
            }
        }

        if (newTrophies.isNotEmpty()) {
            trophyDao.insertAll(newTrophies)
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
                    id = "streak_3",
                    title = "3-Day Spark",
                    subtitle = "First Spark Milestone",
                    description = "Keep any habit streak alive for 3 consecutive days to unlock",
                    requiredDays = 3,
                    habitTitle = "Global Streak Milestones",
                    isUnlocked = false,
                    unlockedAtMillis = null,
                    iconType = "wreath_star"
                ),
                Trophy(
                    id = "streak_7",
                    title = "7-Day Flame",
                    subtitle = "1-Week Consistency Flame",
                    description = "Keep any habit streak alive for 7 consecutive days to unlock",
                    requiredDays = 7,
                    habitTitle = "Global Streak Milestones",
                    isUnlocked = false,
                    unlockedAtMillis = null,
                    iconType = "medal"
                ),
                Trophy(
                    id = "streak_15",
                    title = "15-Day Blaze",
                    subtitle = "15-Day Consistency Trophy",
                    description = "Maintain unwavering consistency for 15 full days to unlock",
                    requiredDays = 15,
                    habitTitle = "Global Streak Milestones",
                    isUnlocked = false,
                    unlockedAtMillis = null,
                    iconType = "medal"
                ),
                Trophy(
                    id = "streak_30",
                    title = "30-Day Legend",
                    subtitle = "Monthly Flame Champion",
                    description = "Achieve a monumental 30-day streak unbroken to unlock",
                    requiredDays = 30,
                    habitTitle = "Global Streak Milestones",
                    isUnlocked = false,
                    unlockedAtMillis = null,
                    iconType = "cup"
                ),
                Trophy(
                    id = "streak_60",
                    title = "60-Day Grandmaster",
                    subtitle = "Grandmaster Achiever",
                    description = "Complete an entire 60-day streak journey to true mastery to unlock",
                    requiredDays = 60,
                    habitTitle = "Global Streak Milestones",
                    isUnlocked = false,
                    unlockedAtMillis = null,
                    iconType = "wreath_star"
                )
            )
            trophyDao.insertAll(trophies)
        }
    }

    fun calculateStreaks(completions: Set<Long>, todayEpochDay: Long): Pair<Int, Int> {
        if (completions.isEmpty()) return Pair(0, 0)

        // Determine starting day for current streak:
        // 1. If today is completed, check if tomorrow was also completed (e.g. timezone shift ahead).
        // 2. Otherwise start from today.
        // 3. If today is not completed, start from yesterday (grace period until end of day).
        // 4. If neither today nor yesterday is completed, the streak is broken (0).
        val startDay: Long? = when {
            completions.contains(todayEpochDay + 1) && completions.contains(todayEpochDay) -> todayEpochDay + 1
            completions.contains(todayEpochDay) -> todayEpochDay
            completions.contains(todayEpochDay - 1) -> todayEpochDay - 1
            else -> null
        }

        var currentStreak = 0
        if (startDay != null) {
            var checkDay = startDay
            while (completions.contains(checkDay)) {
                currentStreak++
                checkDay--
            }
        }

        // Longest streak across all historical completions
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
