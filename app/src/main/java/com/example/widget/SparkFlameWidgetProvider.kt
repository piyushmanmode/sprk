package com.example.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

class SparkFlameWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        WidgetUpdater.updateAllWidgets(context)
    }

    override fun onEnabled(context: Context) {
        WidgetUpdater.updateAllWidgets(context)
    }
}
