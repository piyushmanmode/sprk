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

data class SparkUiState(
    val habits: List<HabitWithStats> = emptyList(),
    val trophies: List<Trophy> = emptyList(),
    val currentTab: NavTab = NavTab.HOME,
    val showCreateHabitSheet: Boolean = false,
    val showCelebrationDialog: Boolean = false,
    val showStreaksOverview: Boolean = false,
    val showWidgetOptions: Boolean = false,
    val selectedHabitForDetail: HabitWithStats? = null,
    val selectedTrophyForShare: Trophy? = null,
    val isOnboardingCompleted: Boolean = true,
    val profilePhotoPath: String? = null,
    val userName: String = "Piyush",
    val userEmail: String = "piyushmanmode64@gmail.com",
    val notificationsEnabled: Boolean = true,
    val reminderFrequency: String = "Daily",
    val reminderTime: String = "Mon-Sun 10:00 AM"
)

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = HabitRepository(database.habitDao(), database.trophyDao())
    private val prefs = application.getSharedPreferences("spark_user_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        SparkUiState(
            userName = prefs.getString("user_name", "Piyush") ?: "Piyush",
            userEmail = prefs.getString("user_email", "piyushmanmode64@gmail.com") ?: "piyushmanmode64@gmail.com",
            profilePhotoPath = prefs.getString("profile_photo_path", null)?.takeIf { File(it).exists() },
            notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
        )
    )
    val uiState: StateFlow<SparkUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val hasClearedPreseeded = prefs.getBoolean("has_cleared_preseeded_plain_v1", false)
            if (!hasClearedPreseeded) {
                // Wipe any old pre-seeded template habits for a clean, plain experience
                repository.clearAllData()
                prefs.edit().putBoolean("has_cleared_preseeded_plain_v1", true).apply()
            }
            repository.seedInitialDataIfEmpty()
        }

        viewModelScope.launch {
            combine(
                repository.habitsWithStats,
                repository.allTrophies
            ) { habits, trophies ->
                Pair(habits, trophies)
            }.collect { (habits, trophies) ->
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

    fun toggleHabitCompletion(habitId: Long) {
        viewModelScope.launch {
            val wasAdded = repository.toggleHabitCompletionToday(habitId)
            com.example.widget.WidgetUpdater.updateAllWidgets(getApplication())
            if (wasAdded) {
                val allCompleted = repository.checkAllCompletedToday()
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
            repository.createHabit(newHabit)
            com.example.widget.WidgetUpdater.updateAllWidgets(getApplication())
            _uiState.value = _uiState.value.copy(showCreateHabitSheet = false)
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
            com.example.widget.WidgetUpdater.updateAllWidgets(getApplication())
            if (_uiState.value.selectedHabitForDetail?.habit?.id == habitId) {
                _uiState.value = _uiState.value.copy(selectedHabitForDetail = null)
            }
        }
    }

    fun setOnboardingCompleted(completed: Boolean) {
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
            repository.clearAllData()
            repository.seedInitialDataIfEmpty()
            com.example.widget.WidgetUpdater.updateAllWidgets(getApplication())
        }
    }
}
