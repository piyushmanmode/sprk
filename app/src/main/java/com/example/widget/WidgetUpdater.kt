package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import com.example.data.HabitRepository
import com.example.model.HabitWithStats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object WidgetUpdater {

    fun updateAllWidgets(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val repository = HabitRepository(db.habitDao(), db.trophyDao())
                val habits = repository.habitsWithStats.first()
                updateAllWidgetsWithStats(context, habits)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateAllWidgetsWithStats(context: Context, habits: List<HabitWithStats>) {
        updateStreaksWidget(context, habits)
        updateTasksWidget(context, habits)
        updateFlameWidget(context, habits)
        updateHeatmapWidget(context, habits)
    }

    private fun getLaunchIntent(context: Context, destination: String = "home"): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("WIDGET_DESTINATION", destination)
        }
        return PendingIntent.getActivity(
            context,
            destination.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun updateStreaksWidget(
        context: Context,
        habits: List<HabitWithStats>
    ) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, StreaksWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        if (appWidgetIds.isEmpty()) return

        val topHabit = habits.maxByOrNull { it.currentStreak }
        val maxStreak = topHabit?.currentStreak ?: 0
        val habitTitle = topHabit?.habit?.title ?: "Create your first streak"

        val views = RemoteViews(context.packageName, R.layout.widget_streaks)
        views.setTextViewText(R.id.widget_streak_count, "$maxStreak Days")
        views.setTextViewText(R.id.widget_streak_habit_title, habitTitle)

        val isCompletedToday = topHabit?.isCompletedToday == true
        if (isCompletedToday) {
            views.setTextViewText(R.id.widget_streak_badge, "✓ DONE TODAY")
            views.setTextViewText(R.id.widget_streak_status_text, "Streak kept alive for today! 🔥")
        } else {
            views.setTextViewText(R.id.widget_streak_badge, "🔥 ON FIRE")
            views.setTextViewText(R.id.widget_streak_status_text, "Tap to log today's streak")
        }

        val pendingIntent = getLaunchIntent(context, "streaks")
        views.setOnClickPendingIntent(R.id.widget_streaks_root, pendingIntent)

        for (appWidgetId in appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    fun updateTasksWidget(
        context: Context,
        habits: List<HabitWithStats>
    ) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, TasksWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        if (appWidgetIds.isEmpty()) return

        val views = RemoteViews(context.packageName, R.layout.widget_tasks)
        val totalCount = habits.size
        val doneCount = habits.count { it.isCompletedToday }

        views.setTextViewText(R.id.widget_tasks_progress, "$doneCount/$totalCount Done")

        // Task 1
        if (habits.isNotEmpty()) {
            val h1 = habits[0]
            views.setViewVisibility(R.id.widget_task_row_1, View.VISIBLE)
            views.setTextViewText(R.id.widget_task_title_1, h1.habit.title)
            views.setImageViewResource(
                R.id.widget_task_check_1,
                if (h1.isCompletedToday) R.drawable.ic_check_circle else R.drawable.ic_circle_outline
            )
        } else {
            views.setViewVisibility(R.id.widget_task_row_1, View.GONE)
        }

        // Task 2
        if (habits.size > 1) {
            val h2 = habits[1]
            views.setViewVisibility(R.id.widget_task_row_2, View.VISIBLE)
            views.setTextViewText(R.id.widget_task_title_2, h2.habit.title)
            views.setImageViewResource(
                R.id.widget_task_check_2,
                if (h2.isCompletedToday) R.drawable.ic_check_circle else R.drawable.ic_circle_outline
            )
        } else {
            views.setViewVisibility(R.id.widget_task_row_2, View.GONE)
        }

        // Task 3
        if (habits.size > 2) {
            val h3 = habits[2]
            views.setViewVisibility(R.id.widget_task_row_3, View.VISIBLE)
            views.setTextViewText(R.id.widget_task_title_3, h3.habit.title)
            views.setImageViewResource(
                R.id.widget_task_check_3,
                if (h3.isCompletedToday) R.drawable.ic_check_circle else R.drawable.ic_circle_outline
            )
        } else {
            views.setViewVisibility(R.id.widget_task_row_3, View.GONE)
        }

        val pendingIntent = getLaunchIntent(context, "tasks")
        views.setOnClickPendingIntent(R.id.widget_tasks_root, pendingIntent)

        for (appWidgetId in appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    fun updateFlameWidget(
        context: Context,
        habits: List<HabitWithStats>
    ) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, SparkFlameWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        if (appWidgetIds.isEmpty()) return

        val maxStreak = habits.maxOfOrNull { it.currentStreak } ?: 0
        val pendingCount = habits.count { !it.isCompletedToday }

        val views = RemoteViews(context.packageName, R.layout.widget_spark_flame)
        views.setTextViewText(R.id.widget_flame_count_badge, "🔥 $maxStreak STREAK")

        val quote = when {
            pendingCount == 0 && habits.isNotEmpty() -> "“All streaks completed today! You are on fire!”"
            maxStreak > 7 -> "“Keep the streak alive, spark your daily motivation.”"
            else -> "“Small daily disciplines produce massive personal growth.”"
        }
        views.setTextViewText(R.id.widget_quote_text, quote)
        views.setTextViewText(
            R.id.widget_quote_subtext,
            if (pendingCount > 0) "$pendingCount streak habits pending today" else "All goals crushed today"
        )

        val pendingIntent = getLaunchIntent(context, "flame")
        views.setOnClickPendingIntent(R.id.widget_flame_root, pendingIntent)

        for (appWidgetId in appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    fun updateHeatmapWidget(
        context: Context,
        habits: List<HabitWithStats>
    ) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, HeatmapWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        if (appWidgetIds.isEmpty()) return

        val views = RemoteViews(context.packageName, R.layout.widget_heatmap)
        val totalActive = habits.maxOfOrNull { it.currentStreak } ?: 0
        views.setTextViewText(R.id.widget_heatmap_count, "$totalActive Days Streak")

        val pendingIntent = getLaunchIntent(context, "overview")
        views.setOnClickPendingIntent(R.id.widget_heatmap_root, pendingIntent)

        for (appWidgetId in appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
