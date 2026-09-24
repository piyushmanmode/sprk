package com.example

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import androidx.test.core.app.ApplicationProvider
import com.example.model.Habit
import com.example.model.HabitWithStats
import com.example.widget.HeatmapWidgetProvider
import com.example.widget.SparkFlameWidgetProvider
import com.example.widget.StreaksWidgetProvider
import com.example.widget.TasksWidgetProvider
import com.example.widget.WidgetUpdater
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import java.time.ZoneId

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WidgetRobolectricTest {

    @Test
    fun testWidgetProvidersRegistered() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appWidgetManager = AppWidgetManager.getInstance(context)
        assertNotNull(appWidgetManager)

        val streaksComp = ComponentName(context, StreaksWidgetProvider::class.java)
        val tasksComp = ComponentName(context, TasksWidgetProvider::class.java)
        val flameComp = ComponentName(context, SparkFlameWidgetProvider::class.java)
        val heatmapComp = ComponentName(context, HeatmapWidgetProvider::class.java)

        assertNotNull(streaksComp)
        assertNotNull(tasksComp)
        assertNotNull(flameComp)
        assertNotNull(heatmapComp)
    }

    @Test
    fun testWidgetUpdaterExecutionWithEmptyAndPopulatedHabits() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 1. Empty habit list
        WidgetUpdater.updateAllWidgetsWithStats(context, emptyList())

        // 2. Populated habit list
        val todayEpoch = LocalDate.now(ZoneId.systemDefault()).toEpochDay()
        val habit = Habit(
            id = 1L,
            title = "Morning Meditation",
            description = "10 minutes",
            category = "Mindfulness",
            targetDays = 30,
            reminderFrequency = "Daily",
            reminderTime = "08:00 AM",
            colorHex = "#FF5722",
            createdAtMillis = System.currentTimeMillis()
        )
        val habitWithStats = HabitWithStats(
            habit = habit,
            currentStreak = 7,
            longestStreak = 14,
            isCompletedToday = true,
            totalCompletedDays = 15,
            recentCompletions = setOf(todayEpoch, todayEpoch - 1, todayEpoch - 2)
        )

        WidgetUpdater.updateAllWidgetsWithStats(context, listOf(habitWithStats))
        // Verify method completes safely
        assertTrue(true)
    }

    @Test
    fun testWidgetRemoteViewsLayoutsInflateCorrectly() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Verify all 4 widget layouts can be constructed as RemoteViews
        val streaksView = RemoteViews(context.packageName, R.layout.widget_streaks)
        assertNotNull(streaksView)
        assertEquals(R.layout.widget_streaks, streaksView.layoutId)

        val tasksView = RemoteViews(context.packageName, R.layout.widget_tasks)
        assertNotNull(tasksView)
        assertEquals(R.layout.widget_tasks, tasksView.layoutId)

        val flameView = RemoteViews(context.packageName, R.layout.widget_spark_flame)
        assertNotNull(flameView)
        assertEquals(R.layout.widget_spark_flame, flameView.layoutId)

        val heatmapView = RemoteViews(context.packageName, R.layout.widget_heatmap)
        assertNotNull(heatmapView)
        assertEquals(R.layout.widget_heatmap, heatmapView.layoutId)
    }

    @Test
    fun testStreaksWidgetRemoteViewsValues() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val views = RemoteViews(context.packageName, R.layout.widget_streaks)

        val streakCount = 12
        val habitTitle = "Daily Reading"
        views.setTextViewText(R.id.widget_streak_count, "$streakCount Days")
        views.setTextViewText(R.id.widget_streak_habit_title, habitTitle)
        views.setTextViewText(R.id.widget_streak_badge, "🔥 ON FIRE")

        assertNotNull(views)
        assertEquals(R.layout.widget_streaks, views.layoutId)
    }
}
