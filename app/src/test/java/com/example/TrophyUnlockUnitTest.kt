package com.example

import com.example.model.Trophy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TrophyUnlockUnitTest {

    private fun evaluateGlobalMilestones(bestStreak: Int): Set<String> {
        val unlocked = mutableSetOf<String>()
        if (bestStreak >= 3) unlocked.add("streak_3")
        if (bestStreak >= 7) unlocked.add("streak_7")
        if (bestStreak >= 15) unlocked.add("streak_15")
        if (bestStreak >= 30) unlocked.add("streak_30")
        if (bestStreak >= 60) unlocked.add("streak_60")
        return unlocked
    }

    private fun evaluateHabitMilestones(habitId: Long, bestStreak: Int): Set<String> {
        val unlocked = mutableSetOf<String>()
        if (bestStreak >= 3) unlocked.add("habit_${habitId}_3")
        if (bestStreak >= 7) unlocked.add("habit_${habitId}_7")
        if (bestStreak >= 14) unlocked.add("habit_${habitId}_14")
        if (bestStreak >= 30) unlocked.add("habit_${habitId}_30")
        return unlocked
    }

    @Test
    fun testGlobalTrophyThresholds_0DaysStreak() {
        val unlocked = evaluateGlobalMilestones(0)
        assertTrue(unlocked.isEmpty())
    }

    @Test
    fun testGlobalTrophyThresholds_2DaysStreak() {
        val unlocked = evaluateGlobalMilestones(2)
        assertTrue(unlocked.isEmpty())
    }

    @Test
    fun testGlobalTrophyThresholds_3DaysStreak() {
        val unlocked = evaluateGlobalMilestones(3)
        assertEquals(setOf("streak_3"), unlocked)
    }

    @Test
    fun testGlobalTrophyThresholds_7DaysStreak() {
        val unlocked = evaluateGlobalMilestones(7)
        assertEquals(setOf("streak_3", "streak_7"), unlocked)
    }

    @Test
    fun testGlobalTrophyThresholds_15DaysStreak() {
        val unlocked = evaluateGlobalMilestones(15)
        assertEquals(setOf("streak_3", "streak_7", "streak_15"), unlocked)
    }

    @Test
    fun testGlobalTrophyThresholds_30DaysStreak() {
        val unlocked = evaluateGlobalMilestones(30)
        assertEquals(setOf("streak_3", "streak_7", "streak_15", "streak_30"), unlocked)
    }

    @Test
    fun testGlobalTrophyThresholds_60DaysStreak() {
        val unlocked = evaluateGlobalMilestones(60)
        assertEquals(setOf("streak_3", "streak_7", "streak_15", "streak_30", "streak_60"), unlocked)
    }

    @Test
    fun testHabitSpecificTrophyThresholds() {
        val habitId = 42L
        assertEquals(emptySet<String>(), evaluateHabitMilestones(habitId, 2))
        assertEquals(setOf("habit_42_3"), evaluateHabitMilestones(habitId, 3))
        assertEquals(setOf("habit_42_3", "habit_42_7"), evaluateHabitMilestones(habitId, 7))
        assertEquals(setOf("habit_42_3", "habit_42_7", "habit_42_14"), evaluateHabitMilestones(habitId, 14))
        assertEquals(setOf("habit_42_3", "habit_42_7", "habit_42_14", "habit_42_30"), evaluateHabitMilestones(habitId, 35))
    }

    @Test
    fun testTrophyModelUnlockedState() {
        val lockedTrophy = Trophy(
            id = "test_trophy",
            habitTitle = "Workout",
            title = "Flame Keeper",
            subtitle = "7 Days Streak",
            description = "Reach 7 days streak",
            requiredDays = 7,
            isUnlocked = false
        )
        assertFalse(lockedTrophy.isUnlocked)

        val unlockedTrophy = lockedTrophy.copy(isUnlocked = true, unlockedAtMillis = System.currentTimeMillis())
        assertTrue(unlockedTrophy.isUnlocked)
        assertTrue((unlockedTrophy.unlockedAtMillis ?: 0L) > 0L)
    }
}
