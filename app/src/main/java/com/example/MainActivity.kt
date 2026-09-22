package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.HabitViewModel
import com.example.ui.components.NavTab
import com.example.ui.components.SparkBottomNavBar
import com.example.ui.screens.CompletionCelebrationDialog
import com.example.ui.screens.CreateHabitSheet
import com.example.ui.screens.HabitDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ShareTrophyDialog
import com.example.ui.screens.StreaksOverviewScreen
import com.example.ui.screens.TrophiesScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.screens.WidgetOptionsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val widgetDestination = intent?.getStringExtra("WIDGET_DESTINATION")
        setContent {
            MyApplicationTheme {
                SparkApp(initialDestination = widgetDestination)
            }
        }
    }
}

@Composable
fun SparkApp(
    initialDestination: String? = null,
    viewModel: HabitViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    androidx.compose.runtime.LaunchedEffect(initialDestination) {
        when (initialDestination) {
            "streaks", "overview" -> viewModel.setShowStreaksOverview(true)
            "flame" -> viewModel.setShowWidgetOptions(true)
            "tasks" -> viewModel.setTab(NavTab.HOME)
        }
    }

    // Top-level sheet: Create a streak
    if (uiState.showCreateHabitSheet) {
        CreateHabitSheet(
            onDismiss = { viewModel.showCreateStreak(false) },
            onCreateHabit = { title, desc, cat, days, freq, time ->
                viewModel.createHabit(title, desc, cat, days, freq, time)
            }
        )
        return
    }

    // Top-level detail: Habit details
    uiState.selectedHabitForDetail?.let { habitWithStats ->
        BackHandler {
            viewModel.selectHabitDetail(null)
        }
        HabitDetailScreen(
            habitWithStats = habitWithStats,
            onBack = { viewModel.selectHabitDetail(null) },
            onToggleComplete = { viewModel.toggleHabitCompletion(habitWithStats.habit.id) },
            onDelete = { viewModel.deleteHabit(habitWithStats.habit.id) },
            onToggleDay = { epochDay -> viewModel.toggleHabitCompletionForDate(habitWithStats.habit.id, epochDay) }
        )
        return
    }

    // Top-level dialog: Share Trophy
    uiState.selectedTrophyForShare?.let { trophy ->
        ShareTrophyDialog(
            trophy = trophy,
            habitTitle = trophy.habitTitle,
            onDismiss = { viewModel.selectTrophyShare(null) }
        )
        return
    }

    // Top-level screen: Streaks Overview
    if (uiState.showStreaksOverview) {
        BackHandler {
            viewModel.setShowStreaksOverview(false)
        }
        StreaksOverviewScreen(
            habits = uiState.habits,
            trophies = uiState.trophies,
            onTrophyClick = { trophy -> viewModel.selectTrophyShare(trophy) },
            onBack = { viewModel.setShowStreaksOverview(false) },
            onToggleHabitForDate = { habitId, dateEpoch -> viewModel.toggleHabitCompletionForDate(habitId, dateEpoch) }
        )
        return
    }

    // Top-level screen: Widget Options
    if (uiState.showWidgetOptions) {
        BackHandler {
            viewModel.setShowWidgetOptions(false)
        }
        WidgetOptionsScreen(
            habits = uiState.habits,
            onBack = { viewModel.setShowWidgetOptions(false) }
        )
        return
    }

    // Welcome Screen (if not completed or previewed)
    if (!uiState.isOnboardingCompleted) {
        WelcomeScreen(
            onGetStarted = { viewModel.setOnboardingCompleted(true) }
        )
        return
    }

    // Main Scaffold with Bottom Navigation Bar
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            SparkBottomNavBar(
                currentTab = uiState.currentTab,
                onTabSelected = { tab -> viewModel.setTab(tab) }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = uiState.currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_transition"
            ) { targetTab ->
                when (targetTab) {
                    NavTab.HOME -> {
                        HomeScreen(
                            userName = uiState.userName,
                            habits = uiState.habits,
                            selectedEpochDay = uiState.selectedDateEpochDay,
                            onSelectDate = { epochDay -> viewModel.setSelectedDate(epochDay) },
                            onAvatarClick = { viewModel.setTab(NavTab.PROFILE) },
                            onCardClick = { habitWithStats -> viewModel.selectHabitDetail(habitWithStats) },
                            onToggleComplete = { habitId -> viewModel.toggleHabitCompletion(habitId) },
                            onToggleDay = { habitId, epochDay -> viewModel.toggleHabitCompletionForDate(habitId, epochDay) },
                            onAddHabitClick = { viewModel.showCreateStreak(true) },
                            onWidgetOptionsClick = { viewModel.setShowWidgetOptions(true) }
                        )
                    }
                    NavTab.TROPHIES -> {
                        TrophiesScreen(
                            trophies = uiState.trophies,
                            onTrophyClick = { trophy -> viewModel.selectTrophyShare(trophy) }
                        )
                    }
                    NavTab.CREATE -> {
                        // Handled by showCreateHabitSheet
                    }
                    NavTab.PROFILE -> {
                        ProfileScreen(
                            userName = uiState.userName,
                            userEmail = uiState.userEmail,
                            notificationsEnabled = uiState.notificationsEnabled,
                            habits = uiState.habits,
                            trophies = uiState.trophies,
                            onNavigateTab = { tab -> viewModel.setTab(tab) },
                            onShowStreaksOverview = { viewModel.setShowStreaksOverview(true) },
                            onShowWidgetOptions = { viewModel.setShowWidgetOptions(true) },
                            onUpdateProfile = { name, email, notifs ->
                                viewModel.updateProfile(name, email, notifs)
                            },
                            onResetDemoData = { viewModel.resetDemoData() },
                            onShowWelcome = { viewModel.setOnboardingCompleted(false) }
                        )
                    }
                }
            }

            // Streak Completion Celebration Dialog
            if (uiState.showCelebrationDialog) {
                CompletionCelebrationDialog(
                    onDismiss = { viewModel.dismissCelebration() }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
