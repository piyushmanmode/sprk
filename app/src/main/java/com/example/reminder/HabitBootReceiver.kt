package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class HabitBootReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "HabitBootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            Log.d(TAG, "Reboot or update detected; rescheduling all habit reminders and updating widgets")
            HabitReminderScheduler.rescheduleAllReminders(context)
            com.example.widget.WidgetUpdater.updateAllWidgets(context)
        }
    }
}
