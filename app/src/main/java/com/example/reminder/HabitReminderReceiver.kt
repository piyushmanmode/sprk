package com.example.reminder

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

class HabitReminderReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "HabitReminderReceiver"
        const val ACTION_HABIT_REMINDER = "com.example.action.HABIT_REMINDER"
        const val EXTRA_HABIT_ID = "extra_habit_id"
        const val EXTRA_HABIT_TITLE = "extra_habit_title"
        const val EXTRA_REMINDER_TIME = "extra_reminder_time"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences("spark_prefs", Context.MODE_PRIVATE)
        val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
        if (!notificationsEnabled) {
            Log.d(TAG, "Notifications disabled by user; skipping notification")
            return
        }

        val habitId = intent.getLongExtra(EXTRA_HABIT_ID, 1L)
        val habitTitle = intent.getStringExtra(EXTRA_HABIT_TITLE) ?: "your daily habit"
        val reminderTime = intent.getStringExtra(EXTRA_REMINDER_TIME) ?: "10:00 AM"

        Log.d(TAG, "Received habit reminder trigger for: $habitTitle ($habitId)")

        HabitReminderScheduler.createNotificationChannel(context)

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("target_habit_id", habitId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            habitId.toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, HabitReminderScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_flame_small)
            .setContentTitle("Time for your Spark! 🔥")
            .setContentText("Keep your streak alive: time to complete $habitTitle")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Keep your momentum blazing! Tap to check off '$habitTitle' and extend your daily streak.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(habitId.toInt(), notification)
        } else {
            Log.w(TAG, "POST_NOTIFICATIONS permission not granted")
        }

        // Reschedule for the next day so the habit repeats daily
        HabitReminderScheduler.scheduleReminder(context, habitId, habitTitle, reminderTime)
    }
}
