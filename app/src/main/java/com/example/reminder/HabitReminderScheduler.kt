package com.example.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.regex.Pattern

object HabitReminderScheduler {
    private const val TAG = "HabitReminderScheduler"
    const val CHANNEL_ID = "spark_habit_reminders"
    const val CHANNEL_NAME = "Daily Habit Reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily motivational notifications to keep your habit streaks alive"
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    fun scheduleReminder(
        context: Context,
        habitId: Long,
        habitTitle: String,
        reminderTimeStr: String
    ) {
        createNotificationChannel(context)
        val (hour, minute) = parseTime(reminderTimeStr)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, HabitReminderReceiver::class.java).apply {
            action = HabitReminderReceiver.ACTION_HABIT_REMINDER
            putExtra(HabitReminderReceiver.EXTRA_HABIT_ID, habitId)
            putExtra(HabitReminderReceiver.EXTRA_HABIT_TITLE, habitTitle)
            putExtra(HabitReminderReceiver.EXTRA_REMINDER_TIME, reminderTimeStr)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled reminder for '$habitTitle' at ${calendar.time}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule alarm for $habitTitle", e)
        }
    }

    fun cancelReminder(context: Context, habitId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, HabitReminderReceiver::class.java).apply {
            action = HabitReminderReceiver.ACTION_HABIT_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Cancelled reminder for habitId: $habitId")
        }
    }

    fun cancelAllReminders(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val habits = db.habitDao().getAllHabits().first()
                for (h in habits) {
                    cancelReminder(context, h.id)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to cancel all reminders", e)
            }
        }
    }

    fun rescheduleAllReminders(context: Context) {
        createNotificationChannel(context)
        val prefs = context.getSharedPreferences("spark_prefs", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean("notifications_enabled", true)
        if (!enabled) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val habits = db.habitDao().getAllHabits().first()
                for (h in habits) {
                    scheduleReminder(context, h.id, h.title, h.reminderTime)
                }
                Log.d(TAG, "Rescheduled ${habits.size} habit reminders")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to reschedule reminders", e)
            }
        }
    }

    private fun parseTime(timeStr: String): Pair<Int, Int> {
        // Handle formats like "10:00 AM", "Mon-Sun 10:00 AM", "09:30 PM"
        val pattern = Pattern.compile("(\\d{1,2}):(\\d{2})\\s*(AM|PM)?", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(timeStr)
        if (matcher.find()) {
            var hour = matcher.group(1)?.toIntOrNull() ?: 10
            val minute = matcher.group(2)?.toIntOrNull() ?: 0
            val amPm = matcher.group(3)?.uppercase()
            if (amPm == "PM" && hour < 12) hour += 12
            if (amPm == "AM" && hour == 12) hour = 0
            return Pair(hour, minute)
        }
        return Pair(10, 0) // Default 10:00 AM
    }
}
