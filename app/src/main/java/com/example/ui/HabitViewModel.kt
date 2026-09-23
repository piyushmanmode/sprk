package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.HabitRepository
import com.example.model.Habit
import com.example.model.HabitWithStats
import com.example.model.Trophy
import com.example.ui.components.NavTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

import java.time.LocalDate
import java.time.ZoneId

data class SparkUiState(
    val habits: List<HabitWithStats> = emptyList(),
    val trophies: List<Trophy> = emptyList(),
    val currentTab: NavTab = NavTab.HOME,
    val selectedDateEpochDay: Long = LocalDate.now(ZoneId.systemDefault()).toEpochDay(),
    val showCreateHabitSheet: Boolean = false,
    val showCelebrationDialog: Boolean = false,
    val showStreaksOverview: Boolean = false,
    val showWidgetOptions: Boolean = false,
    val selectedHabitForDetail: HabitWithStats? = null,
    val selectedTrophyForShare: Trophy? = null,
    val isOnboardingCompleted: Boolean = false,
    val profilePhotoPath: String? = null,
    val userName: String = "",
    val userEmail: String = "",
    val notificationsEnabled: Boolean = true,
    val reminderFrequency: String = "Daily",
    val reminderTime: String = "Mon-Sun 10:00 AM"
)

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = HabitRepository(database.habitDao(), database.trophyDao())
    private val prefs = application.getSharedPreferences("spark_user_prefs", Context.MODE_PRIVATE)

    private val _uiState: MutableStateFlow<SparkUiState>
    val uiState: StateFlow<SparkUiState>

    init {
        // Scrub any legacy hardcoded credentials if previously saved
        val rawSavedName = prefs.getString("user_name", "") ?: ""
        val rawSavedEmail = prefs.getString("user_email", "") ?: ""
        val cleanedName = if (rawSavedName.equals("Piyush", ignoreCase = true)) "" else rawSavedName
        val cleanedEmail = if (rawSavedEmail.contains("piyush", ignoreCase = true)) "" else rawSavedEmail
        if (rawSavedName != cleanedName || rawSavedEmail != cleanedEmail) {
            prefs.edit().putString("user_name", cleanedName).putString("user_email", cleanedEmail).apply()
        }

        val hasCompletedOnboarding = prefs.getBoolean("onboarding_completed", false) && cleanedName.isNotBlank()

        _uiState = MutableStateFlow(
            SparkUiState(
                userName = cleanedName,
                userEmail = cleanedEmail,
                isOnboardingCompleted = hasCompletedOnboarding,
                profilePhotoPath = prefs.getString("profile_photo_path", null)?.takeIf { File(it).exists() },
                notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
            )
        )
        uiState = _uiState.asStateFlow()

        com.example.reminder.HabitReminderScheduler.createNotificationChannel(application)
        viewModelScope.launch {
            // Seed initial trophy milestones if needed without clearing existing habits/completions
            repository.seedInitialDataIfEmpty()
            if (_uiState.value.notificationsEnabled) {
                com.example.reminder.HabitReminderScheduler.rescheduleAllReminders(application)
            }
        }

        viewModelScope.launch {
            combine(
                repository.habitsWithStats,
                repository.allTrophies
            ) { habits, trophies ->
                Pair(habits, trophies)
            }.collect { (habits, trophies) ->
                // Ensure habit-specific milestone trophies exist for each habit
                repository.ensureTrophiesForHabits(habits)
                repository.checkAndUnlockTrophies()

                _uiState.value = _uiState.value.copy(
                    habits = habits,
                    trophies = trophies,
                    selectedHabitForDetail = _uiState.value.selectedHabitForDetail?.let { current ->
                        habits.find { it.habit.id == current.habit.id } ?: current
                    }
                )
                com.example.widget.WidgetUpdater.updateAllWidgets(application)
            }
        }
    }

    fun setTab(tab: NavTab) {
        if (tab == NavTab.CREATE) {
            _uiState.value = _uiState.value.copy(showCreateHabitSheet = true)
        } else {
            _uiState.value = _uiState.value.copy(
                currentTab = tab,
                selectedHabitForDetail = null
            )
        }
    }

    fun showCreateStreak(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCreateHabitSheet = show)
    }

    fun selectHabitDetail(habit: HabitWithStats?) {
        _uiState.value = _uiState.value.copy(selectedHabitForDetail = habit)
    }

    fun selectTrophyShare(trophy: Trophy?) {
        _uiState.value = _uiState.value.copy(selectedTrophyForShare = trophy)
    }

    fun setShowStreaksOverview(show: Boolean) {
        _uiState.value = _uiState.value.copy(showStreaksOverview = show)
    }

    fun setShowWidgetOptions(show: Boolean) {
        _uiState.value = _uiState.value.copy(showWidgetOptions = show)
    }

    fun dismissCelebration() {
        _uiState.value = _uiState.value.copy(showCelebrationDialog = false)
    }

    fun triggerCelebration() {
        _uiState.value = _uiState.value.copy(showCelebrationDialog = true)
    }

    fun setSelectedDate(epochDay: Long) {
        _uiState.value = _uiState.value.copy(selectedDateEpochDay = epochDay)
    }

    fun toggleHabitCompletion(habitId: Long) {
        toggleHabitCompletionForDate(habitId, _uiState.value.selectedDateEpochDay)
    }

    fun toggleHabitCompletionForDate(habitId: Long, dateEpochDay: Long) {
        viewModelScope.launch {
            val wasAdded = repository.toggleHabitCompletionForDate(habitId, dateEpochDay)
            com.example.widget.WidgetUpdater.updateAllWidgets(getApplication())
            if (wasAdded) {
                val allCompleted = repository.checkAllCompletedForDate(dateEpochDay)
                if (allCompleted) {
                    _uiState.value = _uiState.value.copy(showCelebrationDialog = true)
                }
            }
        }
    }

    fun createHabit(
        title: String,
        description: String,
        category: String,
        targetDays: Int,
        reminderFreq: String,
        reminderTime: String
    ) {
        viewModelScope.launch {
            val newHabit = Habit(
                title = title.trim(),
                description = description.trim(),
                category = category,
                targetDays = targetDays,
                reminderFrequency = reminderFreq,
                reminderTime = reminderTime,
                timeSpentMinutes = 0
            )
            val habitId = repository.createHabit(newHabit)
            if (reminderFreq.isNotBlank() && _uiState.value.notificationsEnabled) {
                com.example.reminder.HabitReminderScheduler.scheduleReminder(
                    getApplication(),
                    habitId,
                    newHabit.title,
                    newHabit.reminderTime
                )
            }
            com.example.widget.WidgetUpdater.updateAllWidgets(getApplication())
            _uiState.value = _uiState.value.copy(showCreateHabitSheet = false)
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            com.example.reminder.HabitReminderScheduler.cancelReminder(getApplication(), habitId)
            repository.deleteHabit(habitId)
            com.example.widget.WidgetUpdater.updateAllWidgets(getApplication())
            if (_uiState.value.selectedHabitForDetail?.habit?.id == habitId) {
                _uiState.value = _uiState.value.copy(selectedHabitForDetail = null)
            }
        }
    }

    fun setOnboardingCompleted(completed: Boolean, name: String = "", email: String = "") {
        prefs.edit()
            .putBoolean("onboarding_completed", completed)
            .apply()
        if (name.isNotBlank() || email.isNotBlank()) {
            val updatedName = if (name.isNotBlank()) name.trim() else _uiState.value.userName
            val updatedEmail = if (email.isNotBlank()) email.trim() else _uiState.value.userEmail
            updateProfile(updatedName, updatedEmail, _uiState.value.notificationsEnabled)
        }
        _uiState.value = _uiState.value.copy(isOnboardingCompleted = completed)
    }

    fun updateProfile(name: String, email: String, notifications: Boolean) {
        prefs.edit()
            .putString("user_name", name)
            .putString("user_email", email)
            .putBoolean("notifications_enabled", notifications)
            .apply()
        _uiState.value = _uiState.value.copy(
            userName = name,
            userEmail = email,
            notificationsEnabled = notifications
        )
        if (notifications) {
            com.example.reminder.HabitReminderScheduler.rescheduleAllReminders(getApplication())
        } else {
            com.example.reminder.HabitReminderScheduler.cancelAllReminders(getApplication())
        }
    }

    fun saveSelectedProfilePhoto(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val file = File(context.filesDir, "user_profile_photo.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                val path = file.absolutePath
                prefs.edit().putString("profile_photo_path", path).apply()
                _uiState.value = _uiState.value.copy(profilePhotoPath = path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeProfilePhoto() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val file = File(context.filesDir, "user_profile_photo.jpg")
                if (file.exists()) {
                    file.delete()
                }
                prefs.edit().remove("profile_photo_path").apply()
                _uiState.value = _uiState.value.copy(profilePhotoPath = null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            com.example.reminder.HabitReminderScheduler.cancelAllReminders(getApplication())
            repository.clearAllData()
            repository.seedInitialDataIfEmpty()
            com.example.widget.WidgetUpdater.updateAllWidgets(getApplication())
        }
    }
}
