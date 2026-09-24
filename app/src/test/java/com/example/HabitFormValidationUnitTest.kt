package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HabitFormValidationUnitTest {

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null,
        val sanitizedTitle: String = "",
        val sanitizedDays: Int = 60
    )

    private fun validateHabitForm(
        rawTitle: String,
        rawDescription: String,
        targetDays: Int,
        reminderTime: String,
        colorHex: String
    ): ValidationResult {
        val trimmedTitle = rawTitle.trim()
        if (trimmedTitle.isBlank()) {
            return ValidationResult(isValid = false, errorMessage = "Habit title cannot be empty")
        }
        if (trimmedTitle.length > 80) {
            return ValidationResult(isValid = false, errorMessage = "Habit title exceeds maximum length of 80 characters")
        }
        if (targetDays <= 0) {
            return ValidationResult(isValid = false, errorMessage = "Target days must be at least 1 day")
        }
        val hexRegex = Regex("^#[0-9a-fA-F]{6}$")
        if (!hexRegex.matches(colorHex)) {
            return ValidationResult(isValid = false, errorMessage = "Invalid color hex format")
        }
        val timeRegex = Regex("^(0?[1-9]|1[0-2]):[0-5][0-9] (AM|PM)$")
        if (!timeRegex.matches(reminderTime)) {
            return ValidationResult(isValid = false, errorMessage = "Invalid reminder time format (expected HH:MM AM/PM)")
        }

        return ValidationResult(
            isValid = true,
            sanitizedTitle = trimmedTitle,
            sanitizedDays = targetDays
        )
    }

    @Test
    fun testValidation_EmptyTitle_IsRejected() {
        val result = validateHabitForm(
            rawTitle = "",
            rawDescription = "Morning run",
            targetDays = 30,
            reminderTime = "08:00 AM",
            colorHex = "#FF5722"
        )
        assertFalse(result.isValid)
        assertEquals("Habit title cannot be empty", result.errorMessage)
    }

    @Test
    fun testValidation_WhitespaceOnlyTitle_IsRejected() {
        val result = validateHabitForm(
            rawTitle = "   \n\t  ",
            rawDescription = "Morning run",
            targetDays = 30,
            reminderTime = "08:00 AM",
            colorHex = "#FF5722"
        )
        assertFalse(result.isValid)
        assertEquals("Habit title cannot be empty", result.errorMessage)
    }

    @Test
    fun testValidation_TrimmedTitle_IsAccepted() {
        val result = validateHabitForm(
            rawTitle = "  Drink 2L Water   ",
            rawDescription = "Hydration goal",
            targetDays = 60,
            reminderTime = "09:00 AM",
            colorHex = "#2196F3"
        )
        assertTrue(result.isValid)
        assertEquals("Drink 2L Water", result.sanitizedTitle)
    }

    @Test
    fun testValidation_ZeroOrNegativeTargetDays_IsRejected() {
        val resultZero = validateHabitForm(
            rawTitle = "Daily Journaling",
            rawDescription = "Write daily",
            targetDays = 0,
            reminderTime = "10:00 PM",
            colorHex = "#4CAF50"
        )
        assertFalse(resultZero.isValid)
        assertEquals("Target days must be at least 1 day", resultZero.errorMessage)

        val resultNegative = validateHabitForm(
            rawTitle = "Daily Journaling",
            rawDescription = "Write daily",
            targetDays = -5,
            reminderTime = "10:00 PM",
            colorHex = "#4CAF50"
        )
        assertFalse(resultNegative.isValid)
        assertEquals("Target days must be at least 1 day", resultNegative.errorMessage)
    }

    @Test
    fun testValidation_InvalidColorHex_IsRejected() {
        val result = validateHabitForm(
            rawTitle = "Reading",
            rawDescription = "Books",
            targetDays = 30,
            reminderTime = "08:00 PM",
            colorHex = "NOT_A_HEX"
        )
        assertFalse(result.isValid)
        assertEquals("Invalid color hex format", result.errorMessage)
    }

    @Test
    fun testValidation_InvalidReminderTime_IsRejected() {
        val result = validateHabitForm(
            rawTitle = "Reading",
            rawDescription = "Books",
            targetDays = 30,
            reminderTime = "25:99 XX",
            colorHex = "#FF5722"
        )
        assertFalse(result.isValid)
        assertEquals("Invalid reminder time format (expected HH:MM AM/PM)", result.errorMessage)
    }

    @Test
    fun testValidation_ValidHabit_IsApproved() {
        val result = validateHabitForm(
            rawTitle = "Evening Stretches",
            rawDescription = "Before bed routine",
            targetDays = 60,
            reminderTime = "10:30 PM",
            colorHex = "#9C27B0"
        )
        assertTrue(result.isValid)
        assertEquals("Evening Stretches", result.sanitizedTitle)
        assertEquals(60, result.sanitizedDays)
    }
}
